package com.receiptofi.service;

import com.receiptofi.domain.ExpenseTallyEntity;
import com.receiptofi.repository.ExpenseTallyManager;
import com.receiptofi.utils.HashText;
import com.receiptofi.utils.RandomString;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 7/23/16 5:39 PM
 */
@Service
public class ExpenseTallyService {

    private ExpenseTallyManager expenseTallyManager;

    @Value ("${ExpenseTallyService.limit:10}")
    private int limit;

    @Autowired
    public ExpenseTallyService(ExpenseTallyManager expenseTallyManager) {
        this.expenseTallyManager = expenseTallyManager;
    }

    /**
     * @param rid
     * @param tid is RID but its called TID as Tally Id of user
     */
    public void createInvite(String rid, String tid) {
        expenseTallyManager.save(
                ExpenseTallyEntity.newInstance(
                        rid,
                        tid,
                        HashText.computeBCrypt(RandomString.newInstance().nextString()))
        );
    }

    /**
     * @param tid is RID of business user.
     * @return
     */
    public List<ExpenseTallyEntity> getUsersForExpenseTally(String tid) {
        return expenseTallyManager.getUsersForExpenseTally(tid, limit);
    }
}
