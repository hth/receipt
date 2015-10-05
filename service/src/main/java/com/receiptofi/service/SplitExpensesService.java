package com.receiptofi.service;

import com.receiptofi.domain.SplitExpensesEntity;
import com.receiptofi.domain.json.JsonFriend;
import com.receiptofi.repository.SplitExpensesManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void save(SplitExpensesEntity splitExpenses) {
        splitExpensesManager.save(splitExpenses);
    }

    public void deleteHard(SplitExpensesEntity splitExpenses) {
        splitExpensesManager.deleteHard(splitExpenses);
    }

    public boolean deleteHard(String rdid, String rid, String fid) {
        return splitExpensesManager.deleteHard(rdid, rid, fid);
    }

    public List<JsonFriend> populateProfileOfFriends(String rdid, String rid, Map<String, JsonFriend> jsonFriends) {
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

    public boolean updateSplitTotal(String receiptId, Double splitTotal) {
        return splitExpensesManager.updateSplitTotal(receiptId, splitTotal);
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
}
