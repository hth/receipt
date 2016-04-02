package com.receiptofi.service;

import com.receiptofi.domain.SplitExpensesEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.json.JsonFriend;
import com.receiptofi.domain.json.JsonOweExpenses;
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
    private SplitExpensesManager splitExpensesManager;
    private UserProfilePreferenceService userProfilePreferenceService;

    @Autowired
    public SplitExpensesService(
            SplitExpensesManager splitExpensesManager,
            UserProfilePreferenceService userProfilePreferenceService) {
        this.splitExpensesManager = splitExpensesManager;
        this.userProfilePreferenceService = userProfilePreferenceService;
    }

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
        List<SplitExpensesEntity> splitExpenses = getSplitExpensesFriendsForReceipt(rdid);

        List<JsonFriend> jsonSplitFriends = new ArrayList<>();
        for (SplitExpensesEntity splitExpense : splitExpenses) {
            if (jsonFriends.containsKey(splitExpense.getFriendUserId())) {
                jsonSplitFriends.add(jsonFriends.get(splitExpense.getFriendUserId()));
                jsonFriends.remove(splitExpense.getFriendUserId());
            }
        }

        return jsonSplitFriends;
    }

    boolean doesExists(String rdid, String rid, String fid) {
        return splitExpensesManager.doesExists(rdid, rid, fid);
    }

    @Mobile
    public List<SplitExpensesEntity> getSplitExpensesFriendsForReceipt(String rdid) {
        return splitExpensesManager.getSplitExpensesFriendsForReceipt(rdid);
    }

    /**
     * Update total for matching receipt id.
     *
     * @param rdid       Receipt Id
     * @param splitTotal Total amount
     * @return
     */
    boolean updateSplitTotal(String rdid, Double splitTotal) {
        return splitExpensesManager.updateSplitTotal(rdid, splitTotal);
    }

    private List<SplitExpensesEntity> getOwesMe(String rid) {
        return splitExpensesManager.getOwesMe(rid);
    }

    private List<SplitExpensesEntity> getOwesOthers(String rid) {
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

    public List<JsonOweExpenses> getJsonOweOthersExpenses(String rid) {
        List<JsonOweExpenses> jsonOweOthers = new ArrayList<>();
        List<SplitExpensesEntity> splitExpenses = getOwesOthers(rid);
        for (SplitExpensesEntity splitExpense : splitExpenses) {
            if (splitExpense.getSplitTotal() > 0) {
                UserProfileEntity userProfile = userProfilePreferenceService.findByReceiptUserId(splitExpense.getReceiptUserId());
                JsonOweExpenses jsonOweExpense = new JsonOweExpenses(
                        splitExpense.getReceiptUserId(),
                        splitExpense.getFriendUserId(),
                        splitExpense.getSplitTotal(),
                        userProfile.getName());

                jsonOweOthers.add(jsonOweExpense);
            }
        }
        return jsonOweOthers;
    }

    public List<JsonOweExpenses> getJsonOweExpenses(String rid) {
        List<JsonOweExpenses> jsonOweMe = new ArrayList<>();
        List<SplitExpensesEntity> splitExpenses = getOwesMe(rid);
        for (SplitExpensesEntity splitExpense : splitExpenses) {
            if (splitExpense.getSplitTotal() > 0) {
                UserProfileEntity userProfile = userProfilePreferenceService.findByReceiptUserId(splitExpense.getFriendUserId());
                JsonOweExpenses jsonOweExpense = new JsonOweExpenses(
                        splitExpense.getReceiptUserId(),
                        splitExpense.getFriendUserId(),
                        splitExpense.getSplitTotal(),
                        userProfile.getName());

                jsonOweMe.add(jsonOweExpense);
            }
        }
        return jsonOweMe;
    }
}
