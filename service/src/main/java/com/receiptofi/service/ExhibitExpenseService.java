package com.receiptofi.service;

import com.receiptofi.domain.ExhibitExpenseEntity;
import com.receiptofi.repository.ExhibitExpenseManager;
import com.receiptofi.utils.HashText;
import com.receiptofi.utils.RandomString;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 7/23/16 5:39 PM
 */
@Service
public class ExhibitExpenseService {

    private ExhibitExpenseManager exhibitExpenseManager;

    @Autowired
    public ExhibitExpenseService(ExhibitExpenseManager exhibitExpenseManager) {
        this.exhibitExpenseManager = exhibitExpenseManager;
    }

    /**
     *
     * @param rid
     * @param eid is RID but its called EID as Expense Id of user
     */
    public void createInvite(String rid, String eid) {
        exhibitExpenseManager.save(
                ExhibitExpenseEntity.newInstance(
                        rid,
                        eid,
                        HashText.computeBCrypt(RandomString.newInstance().nextString()))
        );
    }
}
