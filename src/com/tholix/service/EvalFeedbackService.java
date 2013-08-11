package com.tholix.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.tholix.domain.EvalFeedbackEntity;
import com.tholix.domain.UserSession;
import com.tholix.domain.types.FileTypeEnum;
import com.tholix.repository.EvalFeedbackManager;
import com.tholix.web.form.UploadReceiptImage;

/**
 * User: hitender
 * Date: 7/20/13
 * Time: 5:32 PM
 */
@Service
public final class EvalFeedbackService {
    private static final Logger log = Logger.getLogger(EvalFeedbackService.class);

    @Autowired EvalFeedbackManager evalFeedbackManager;
    @Autowired FileDBService fileDBService;

    public void addFeedback(String comment, int rating, CommonsMultipartFile fileData, UserSession userSession) {
        String blobId = "";
        try {
            if(fileData.getSize() > 0) {
                UploadReceiptImage uploadReceiptImage = UploadReceiptImage.newInstance();
                uploadReceiptImage.setFileData(fileData);
                uploadReceiptImage.setEmailId(userSession.getEmailId());
                uploadReceiptImage.setUserProfileId(userSession.getUserProfileId());
                uploadReceiptImage.setFileType(FileTypeEnum.FEEDBACK);

                blobId = fileDBService.saveFile(uploadReceiptImage);
            }

            EvalFeedbackEntity evalFeedbackEntity = EvalFeedbackEntity.newInstance(comment, rating, userSession.getUserProfileId());
            if(!StringUtils.isEmpty(blobId)) {
                evalFeedbackEntity.setAttachmentBlobId(blobId);
            }
            evalFeedbackManager.save(evalFeedbackEntity);
        } catch (Exception exce) {
            log.error(exce.getLocalizedMessage());
        }
    }
}
