package com.receiptofi.service;

import com.receiptofi.domain.SplitExpensesEntity;
import com.receiptofi.domain.json.JsonFriend;
import com.receiptofi.domain.types.SplitStatusEnum;
import com.receiptofi.repository.SplitExpensesManager;
import com.receiptofi.utils.Maths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 9/27/15 2:45 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class SplitExpensesService {
    @Autowired private SplitExpensesManager splitExpensesManager;

    public SplitExpensesEntity getById(String id, String rid) {
        return splitExpensesManager.getById(id, rid);
    }

    public void save(SplitExpensesEntity splitExpenses) {
        splitExpensesManager.save(splitExpenses);
    }

    public void deleteHard(SplitExpensesEntity splitExpenses) {
        splitExpensesManager.deleteHard(splitExpenses);
    }

    public boolean deleteHard(String rdid, String rid, String fid) {
        return splitExpensesManager.deleteHard(rdid, rid, fid);
    }

    public List<JsonFriend> populateProfileOfFriends(String rdid, Map<String, JsonFriend> jsonFriends) {
        List<SplitExpensesEntity> splitExpenses = splitExpensesManager.getSplitExpensesFriendsForReceipt(rdid);

        List<JsonFriend> jsonSplitFriends = new ArrayList<>();
        for (SplitExpensesEntity splitExpense : splitExpenses) {
            if (jsonFriends.containsKey(splitExpense.getFriendUserId())) {
                jsonSplitFriends.add(jsonFriends.get(splitExpense.getFriendUserId()));
                jsonFriends.remove(splitExpense.getFriendUserId());
            }
        }

        return jsonSplitFriends;
    }

    public boolean doesExists(String rdid, String rid, String fid) {
        return splitExpensesManager.doesExists(rdid, rid, fid);
    }

    /**
     * Update total for matching receipt id.
     *
     * @param rdid       Receipt Id
     * @param splitTotal Total amount
     * @return
     */
    public boolean updateSplitTotal(String rdid, Double splitTotal) {
        return splitExpensesManager.updateSplitTotal(rdid, splitTotal);
    }

    public List<SplitExpensesEntity> getOwesMe(String rid) {
        return splitExpensesManager.getOwesMe(rid);
    }

    public List<SplitExpensesEntity> getOwesOthers(String rid) {
        return splitExpensesManager.getOwesOthers(rid);
    }

    public List<SplitExpensesEntity> getSplitExpenses(String rid, String fid) {
        return splitExpensesManager.getSplitExpenses(rid, fid);
    }

    public SplitExpensesEntity findSplitExpensesToSettle(String fid, String rid, Double splitTotal) {
        return splitExpensesManager.findSplitExpensesToSettle(fid, rid, splitTotal);
    }

    public void settleSplitExpenses(SplitExpensesEntity splitExpenses, SplitExpensesEntity splitToSettle) {
        BigDecimal settled = Maths.subtract(splitToSettle.getSplitTotal(), splitExpenses.getSplitTotal());
        switch (settled.compareTo(BigDecimal.ZERO)) {
            case 1:
                splitExpenses.setSplitStatus(SplitStatusEnum.S);
                splitExpenses.setSplitTotal(0.00);
                save(splitExpenses);

                splitToSettle.setSplitStatus(SplitStatusEnum.P);
                splitToSettle.setSplitTotal(settled.doubleValue());
                save(splitToSettle);
                break;
            case 0:
                splitExpenses.setSplitStatus(SplitStatusEnum.S);
                splitExpenses.setSplitTotal(0.00);
                save(splitExpenses);

                splitToSettle.setSplitStatus(SplitStatusEnum.S);
                splitToSettle.setSplitTotal(0.00);
                save(splitToSettle);
                break;
            case -1:
                settled = Maths.subtract(splitExpenses.getSplitTotal(), splitToSettle.getSplitTotal());
                splitExpenses.setSplitStatus(SplitStatusEnum.P);
                splitExpenses.setSplitTotal(settled.doubleValue());
                save(splitExpenses);

                splitToSettle.setSplitStatus(SplitStatusEnum.S);
                splitToSettle.setSplitTotal(0.00);
                save(splitToSettle);
                break;
        }
    }

    public boolean hasSettleProcessStarted(String rdid) {
        return splitExpensesManager.hasSettleProcessStarted(rdid);
    }
}
