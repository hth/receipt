package com.receiptofi.service;

import com.receiptofi.domain.CloudFileEntity;
import com.receiptofi.domain.CommentEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.MileageEntity;
import com.receiptofi.domain.types.CommentTypeEnum;
import com.receiptofi.repository.DocumentManager;
import com.receiptofi.repository.MileageManager;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 12/25/13 4:16 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class MileageService {
    private static final Logger LOG = LoggerFactory.getLogger(MileageService.class);

    private MileageManager mileageManager;
    private CommentService commentService;
    private DocumentManager documentManager;
    private FileSystemService fileSystemService;
    private CloudFileService cloudFileService;

    @Autowired
    public MileageService(
            MileageManager mileageManager,
            CommentService commentService,
            DocumentManager documentManager,
            FileSystemService fileSystemService,
            CloudFileService cloudFileService) {

        this.mileageManager = mileageManager;
        this.commentService = commentService;
        this.documentManager = documentManager;
        this.fileSystemService = fileSystemService;
        this.cloudFileService = cloudFileService;
    }

    public void save(MileageEntity mileageEntity) throws Exception {
        mileageManager.save(mileageEntity);
    }

    public List<MileageEntity> getMileageForThisMonth(String receiptUserId, DateTime monthYear) {
        DateTime startTime = monthYear.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
        return mileageManager.getMileageForThisMonth(
                receiptUserId,
                startTime,
                startTime.plusMonths(1).withTimeAtStartOfDay().minusMillis(1)
        );
    }

    public int monthlyTotal(String receiptUserId, DateTime monthYear) {
        return mileageTotal(getMileageForThisMonth(receiptUserId, monthYear));
    }

    public int mileageTotal(List<MileageEntity> mileageEntities) {
        int total = 0;
        for (MileageEntity mileageEntity : mileageEntities) {
            if (mileageEntity.isComplete()) {
                total += mileageEntity.getTotal();
            }
        }
        return total;
    }

    public MileageEntity merge(String id1, String id2, String receiptUserId) {
        MileageEntity m1 = mileageManager.findOne(id1, receiptUserId);
        MileageEntity m2 = mileageManager.findOne(id2, receiptUserId);

        try {
            if (m1 != null && m2 != null && !m1.isComplete() && !m2.isComplete()) {
                if (Integer.compare(m1.getStart(), m2.getStart()) == 1) {
                    m2.mergeEndingMileage(m1);
                    if (m2.getMileageNotes() != null) {
                        commentService.save(m2.getMileageNotes());
                    }
                    mileageManager.save(m2);
                    mileageManager.deleteHard(m1);
                    return m2;
                } else if (Integer.compare(m1.getStart(), m2.getStart()) == -1) {
                    m1.mergeEndingMileage(m2);
                    if (m1.getMileageNotes() != null) {
                        commentService.save(m1.getMileageNotes());
                    }
                    mileageManager.save(m1);
                    mileageManager.deleteHard(m2);
                    return m1;
                } else if (Integer.compare(m1.getStart(), m2.getStart()) == 0) {
                    //There should not be a duplicate data; it should have been rejected
                    throw new RuntimeException("as starting mileage are equal");
                }
            }
        } catch (RuntimeException re) {
            LOG.error("Merge failed to save id1:id2 {}:{}, reason={}", id1, id2, re.getLocalizedMessage(), re);
            throw new RuntimeException("Merge failed to save " + re.getLocalizedMessage());
        } catch (Exception exception) {
            LOG.error("Merge failed to save id1:id2 {}:{}, reason={}",
                    id1, id2, exception.getLocalizedMessage(), exception);
            throw new RuntimeException("Merge failed to save");
        }
        throw new RuntimeException("Merge failed as one or both could not be merged");
    }

    public List<MileageEntity> split(String id, String receiptUserId) {
        MileageEntity m1 = mileageManager.findOne(id, receiptUserId);
        try {
            if (m1 != null && m1.isComplete()) {
                MileageEntity m2 = m1.splitMileage();
                mileageManager.save(m1);
                mileageManager.save(m2);

                List<MileageEntity> list = new LinkedList<>();
                list.add(m2);
                list.add(m1);
                return list;
            }
        } catch (Exception exception) {
            LOG.error("Split failed to save, id={}, reason={}", id, exception.getLocalizedMessage(), exception);
            throw new RuntimeException("Split failed to save");
        }
        throw new RuntimeException("Could not process split");
    }

    public MileageEntity getMileage(String mileageId, String receiptUserId) {
        return mileageManager.findOne(mileageId, receiptUserId);
    }

    public boolean updateStartDate(String mileageId, String date, String receiptUserId) {
        return mileageManager.updateStartDate(
                mileageId,
                DateTime.parse(date, DateTimeFormat.forPattern("MM/dd/yyyy")), receiptUserId);
    }

    public boolean updateEndDate(String mileageId, String date, String receiptUserId) {
        return mileageManager.updateEndDate(
                mileageId,
                DateTime.parse(date, DateTimeFormat.forPattern("MM/dd/yyyy")), receiptUserId);
    }

    /**
     * Saves notes to mileage.
     *
     * @param notes
     * @param mileageId
     * @param userProfileId
     * @return
     */
    public boolean updateMileageNotes(String notes, String mileageId, String rid) {
        MileageEntity mileageEntity = mileageManager.findOne(mileageId, rid);
        CommentEntity commentEntity = mileageEntity.getMileageNotes();
        boolean commentEntityBoolean = false;
        if (null == commentEntity) {
            commentEntityBoolean = true;
            commentEntity = CommentEntity.newInstance(rid, CommentTypeEnum.NOTES);
            commentEntity.setText(notes);
        } else {
            commentEntity.setText(notes);
        }
        try {
            commentEntity.setUpdated();
            commentService.save(commentEntity);
            if (commentEntityBoolean) {
                mileageEntity.setMileageNotes(commentEntity);
                mileageManager.save(mileageEntity);
            }
            return true;
        } catch (Exception exce) {
            LOG.error("Failed updating notes for mileage={}, reason={}", mileageId, exce.getLocalizedMessage(), exce);
            return false;
        }
    }

    /**
     * Delete mileage and its associated data.
     *
     * @param mileageId - Mileage id to delete
     */
    public boolean deleteHardMileage(String mileageId, String rid) throws Exception {
        MileageEntity mileage = mileageManager.findOne(mileageId, rid);
        if (mileage != null) {
            mileageManager.deleteHard(mileage);
            for (FileSystemEntity fileSystem : mileage.getFileSystemEntities()) {
                CloudFileEntity cloudFile = CloudFileEntity.newInstance(fileSystem.getKey(), fileSystem.getFileType());
                cloudFileService.save(cloudFile);
            }
            fileSystemService.deleteHard(mileage.getFileSystemEntities());
            DocumentEntity documentEntity = documentManager.findDocumentByRid(mileage.getDocumentId(), rid);
            if (documentEntity != null) {
                documentManager.deleteHard(documentEntity);
            }
            return true;
        }
        return false;
    }
}
