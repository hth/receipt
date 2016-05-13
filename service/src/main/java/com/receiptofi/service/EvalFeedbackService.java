package com.receiptofi.service;

import com.receiptofi.domain.EvalFeedbackEntity;
import com.receiptofi.domain.shared.UploadDocumentImage;
import com.receiptofi.domain.types.FileTypeEnum;
import com.receiptofi.repository.EvalFeedbackManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

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

    @Autowired EvalFeedbackManager evalFeedbackManager;
    @Autowired FileDBService fileDBService;

    public void addFeedback(String comment, int rating, CommonsMultipartFile fileData, String receiptUserId) {
        String blobId = StringUtils.EMPTY;
        try {
            if (fileData.getSize() > 0) {
                UploadDocumentImage uploadReceiptImage = UploadDocumentImage.newInstance();
                uploadReceiptImage.setFileData(fileData);
                uploadReceiptImage.setRid(receiptUserId);
                uploadReceiptImage.setFileType(FileTypeEnum.F);

                blobId = fileDBService.saveFile(uploadReceiptImage);
            }

            EvalFeedbackEntity evalFeedbackEntity = EvalFeedbackEntity.newInstance(comment, rating, receiptUserId);
            if (!StringUtils.isEmpty(blobId)) {
                evalFeedbackEntity.setAttachmentBlobId(blobId);
            }
            evalFeedbackManager.save(evalFeedbackEntity);
        } catch (Exception exce) {
            LOG.error(exce.getLocalizedMessage());
        }
    }
}
