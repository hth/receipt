package com.tholix.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.tholix.domain.CommentEntity;
import com.tholix.domain.FeedbackEntity;
import com.tholix.domain.UploadReceiptImage;
import com.tholix.domain.UserSession;
import com.tholix.domain.types.FileTypeEnum;
import com.tholix.repository.CommentManager;
import com.tholix.repository.FeedbackManager;

/**
 * User: hitender
 * Date: 7/20/13
 * Time: 5:32 PM
 */
@Service
public class FeedbackService {
    private static final Logger log = Logger.getLogger(FeedbackService.class);

    @Autowired CommentManager commentManager;
    @Autowired FeedbackManager feedbackManager;
    @Autowired FileDBService fileDBService;

    public void addFeedback(String comment, int rating, CommonsMultipartFile fileData, UserSession userSession) {
        CommentEntity commentEntity;
        String blobId = "";
        try {
            commentEntity = CommentEntity.newInstance();
            commentEntity.setText(comment);
            commentManager.save(commentEntity);

            if(fileData != null) {
                UploadReceiptImage uploadReceiptImage = UploadReceiptImage.newInstance();
                uploadReceiptImage.setFileData(fileData);
                uploadReceiptImage.setEmailId(userSession.getEmailId());
                uploadReceiptImage.setUserProfileId(userSession.getUserProfileId());
                uploadReceiptImage.setFileType(FileTypeEnum.FEEDBACK);

                blobId = fileDBService.saveFile(uploadReceiptImage);
            }

            FeedbackEntity feedbackEntity = FeedbackEntity.newInstance(commentEntity, rating, userSession.getUserProfileId());
            if(!StringUtils.isEmpty(blobId)) {
                feedbackEntity.setAttachmentBlobId(blobId);
            }
            feedbackManager.save(feedbackEntity);
        } catch (Exception exce) {
            log.error(exce.getLocalizedMessage());
        }
    }
}
