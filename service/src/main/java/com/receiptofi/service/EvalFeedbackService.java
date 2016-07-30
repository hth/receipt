package com.receiptofi.service;

import com.receiptofi.domain.EvalFeedbackEntity;
import com.receiptofi.domain.shared.UploadDocumentImage;
import com.receiptofi.domain.types.FileTypeEnum;
import com.receiptofi.repository.EvalFeedbackManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.List;

/**
 * User: hitender
 * Date: 7/20/13
 * Time: 5:32 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class EvalFeedbackService {
    private static final Logger LOG = LoggerFactory.getLogger(EvalFeedbackService.class);

    private int limit;
    private EvalFeedbackManager evalFeedbackManager;
    private FileDBService fileDBService;

    @Autowired
    public EvalFeedbackService(
            @Value("${EvalFeedbackService.limit:20}")
            int limit,

            EvalFeedbackManager evalFeedbackManager,
            FileDBService fileDBService
    ) {
        this.limit = limit;
        this.evalFeedbackManager = evalFeedbackManager;
        this.fileDBService = fileDBService;
    }

    public void addFeedback(String comment, int rating, CommonsMultipartFile fileData, String receiptUserId) {
        String blobId = "";
        try {
            if (fileData.getSize() > 0) {
                UploadDocumentImage uploadReceiptImage = UploadDocumentImage.newInstance(FileTypeEnum.F)
                        .setFileData(fileData)
                        .setRid(receiptUserId);

                blobId = fileDBService.saveFile(uploadReceiptImage);
            }

            EvalFeedbackEntity evalFeedback = EvalFeedbackEntity.newInstance(comment, rating, receiptUserId);
            if (!StringUtils.isEmpty(blobId)) {
                evalFeedback.setAttachmentBlobId(blobId);
            }
            evalFeedbackManager.save(evalFeedback);
        } catch (Exception exce) {
            LOG.error("Feedback failed reason={}", exce.getLocalizedMessage(), exce);
        }
    }

    public List<EvalFeedbackEntity> latestFeedback() {
        return evalFeedbackManager.latestFeedback(limit);
    }

    public long collectionSize() {
        return evalFeedbackManager.collectionSize();
    }
}
