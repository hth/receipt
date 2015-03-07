package com.receiptofi.repository;

import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.types.AccountInactiveReasonEnum;
import com.receiptofi.domain.types.ProviderEnum;

import java.util.Date;
import java.util.List;

/**
 * User: hitender
 * Date: 4/23/14 6:43 AM
 */
public interface UserAccountManager extends RepositoryManager<UserAccountEntity> {
    UserAccountEntity findByReceiptUserId(String rid);

    UserAccountEntity findByUserId(String mail);

    UserAccountEntity findByProviderUserId(String providerUserId);

    UserAccountEntity findByAuthorizationCode(ProviderEnum provider, String authorizationCode);

    int inactiveNonValidatedAccount(Date pastActivationDate);

    List<UserAccountEntity> findRegisteredAccountWhenRegistrationIsOff(int registrationInviteDailyLimit);

    void removeRegistrationIsOffFrom(String id);

    void updateAccountToValidated(String id, AccountInactiveReasonEnum air);
}
