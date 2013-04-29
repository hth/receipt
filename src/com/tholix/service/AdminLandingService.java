package com.tholix.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.joda.time.DateTime;

import com.tholix.domain.BizNameEntity;
import com.tholix.domain.BizStoreEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.UserProfileEntity;
import com.tholix.repository.BizNameManager;
import com.tholix.repository.BizStoreManager;
import com.tholix.repository.UserProfileManager;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.UserSearchForm;

/**
 * User: hitender
 * Date: 4/28/13
 * Time: 8:34 PM
 */
@Service
public class AdminLandingService {
    private static final Logger log = Logger.getLogger(AdminLandingService.class);

    @Autowired private UserProfileManager userProfileManager;
    @Autowired private BizNameManager bizNameManager;
    @Autowired private BizStoreManager bizStoreManager;

    /**
     * This method is being used by Admin to create new Business and Stores. Also this method is being used by receipt update to do the same.
     * @param receiptEntity
     */
    public void saveNewBusinessAndOrStore(ReceiptEntity receiptEntity) {
        BizNameEntity bizNameEntity = receiptEntity.getBizName();
        BizStoreEntity bizStoreEntity = receiptEntity.getBizStore();

        BizNameEntity bizName = bizNameManager.findOneByName(bizNameEntity.getName());
        if(bizName == null) {
            try {
                bizNameManager.save(bizNameEntity);
                bizStoreEntity.setBizName(bizNameEntity);
                bizStoreManager.save(bizStoreEntity);
            } catch (Exception e) {
                //TODO add condition
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            BizStoreEntity bizStore = bizStoreManager.findOne(bizStoreEntity);
            if(bizStore == null) {
                try {
                    bizStoreEntity.setBizName(bizName);
                    bizStoreManager.save(bizStoreEntity);
                } catch (Exception e) {
                    //TODO add condition
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }


            //This is used by Receipt update  process and not by Admin
            receiptEntity.setBizName(bizName);
            receiptEntity.setBizStore(bizStore);
        }
    }

    /**
     * //TODO merge receipt and receiptOCR. This will eliminate such duplicate code
     *
     * This method is being used by Admin to create new Business and Stores. Also this method is being used by receipt update to do the same.
     * @param receiptEntity
     */
    public void saveNewBusinessAndOrStore(ReceiptEntityOCR receiptEntity) {
        BizNameEntity bizNameEntity = receiptEntity.getBizName();
        BizStoreEntity bizStoreEntity = receiptEntity.getBizStore();

        BizNameEntity bizName = bizNameManager.findOneByName(bizNameEntity.getName());
        if(bizName == null) {
            try {
                bizNameManager.save(bizNameEntity);
                bizStoreEntity.setBizName(bizNameEntity);
                bizStoreManager.save(bizStoreEntity);
            } catch (Exception e) {
                //TODO add condition
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            BizStoreEntity bizStore = bizStoreManager.findOne(bizStoreEntity);
            if(bizStore == null) {
                try {
                    bizStoreEntity.setBizName(bizName);
                    bizStoreManager.save(bizStoreEntity);
                } catch (Exception e) {
                    //TODO add condition
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }


            //This is used by Receipt update  process and not by Admin
            receiptEntity.setBizName(bizName);
            receiptEntity.setBizStore(bizStore);
        }
    }

    /**
     * This method is called from AJAX to get the matching list of users in the system
     *
     * @param name
     * @return
     */
    public List<String> findMatchingUsers(String name) {
        DateTime time = DateUtil.now();
        List<String> users = new ArrayList<>();
        for(UserSearchForm userSearchForm : findAllUsers(name)) {
            users.add(userSearchForm.getUserName());
        }
        log.info(users);
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return users;
    }

    /**
     * This method returns well populated users with 'id' and other relevant data for showing user profile.
     *
     * @param name
     * @return
     */
    public List<UserSearchForm> findAllUsers(String name) {
        DateTime time = DateUtil.now();
        log.info("Search string for user name: " + name);
        List<UserSearchForm> userList = new ArrayList<UserSearchForm>();
        for(UserProfileEntity user : userProfileManager.searchAllByName(name)) {
            UserSearchForm userForm = UserSearchForm.newInstance(user);
            userList.add(userForm);
        }
        log.info("found users.. total size " + userList.size());
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return userList;
    }
}
