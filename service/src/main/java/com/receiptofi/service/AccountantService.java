package com.receiptofi.service;

import com.receiptofi.domain.AccountantEntity;
import com.receiptofi.repository.AccountantManager;
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
public class AccountantService {

    private AccountantManager accountantManager;

    @Value ("${AccountantService.limit:10}")
    private int limit;

    @Autowired
    public AccountantService(AccountantManager accountantManager) {
        this.accountantManager = accountantManager;
    }

    /**
     * @param rid
     * @param aid is RID but its called AID as Accountant Id of user
     */
    public void createInvite(String rid, String aid) {
        accountantManager.save(
                AccountantEntity.newInstance(
                        rid,
                        aid,
                        HashText.computeBCrypt(RandomString.newInstance().nextString()))
        );
    }

    /**
     * @param aid is RID of business user.
     * @return
     */
    public List<AccountantEntity> getUsersSubscribedToAccountant(String aid) {
        return accountantManager.getUsersSubscribedToAccountant(aid, limit);
    }

    public AccountantEntity getUserForAccountant(String rid, String auth, String aid, String ip) {
        return accountantManager.getUserForAccountant(rid, auth, aid, ip);
    }
}
