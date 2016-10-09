package com.receiptofi.service;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.repository.ForgotRecoverManager;
import com.receiptofi.repository.InviteManager;
import com.receiptofi.repository.UserAccountManager;
import com.receiptofi.repository.UserAuthenticationManager;
import com.receiptofi.repository.UserPreferenceManager;
import com.receiptofi.repository.UserProfileManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * User: hitender
 * Date: 2/19/14 11:21 PM
 */
public class AccountServiceTest {

    @Mock private UserAuthenticationManager userAuthenticationManager;
    @Mock private UserProfileManager userProfileManager;
    @Mock private UserPreferenceManager userPreferenceManager;
    @Mock private ForgotRecoverManager forgotRecoverManager;
    @Mock private UserAccountManager userAccountManager;
    @Mock private GenerateUserIdService generateUserIdService;
    @Mock private EmailValidateService emailValidateService;
    @Mock private RegistrationService registrationService;
    @Mock private ExpensesService expensesService;
    @Mock private BillingService billingService;
    @Mock private NotificationService notificationService;
    @Mock private InviteManager inviteManager;

    private AccountService accountService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        accountService = new AccountService(
                new String[]{"HOME", "BUSINESS"},
                new String[]{"#1a9af9", "#b492e8"},
                new String[]{"V101", "V102"},
                3,
                userAccountManager,
                userAuthenticationManager,
                userProfileManager,
                userPreferenceManager,
                forgotRecoverManager,
                generateUserIdService,
                emailValidateService,
                registrationService,
                expensesService,
                billingService,
                notificationService,
                inviteManager);
    }

    @Test
    public void testFindIfUser_Does_Not_Exists() throws Exception {
        when(userProfileManager.findOneByMail(anyString())).thenReturn(null);
        assertNull(accountService.doesUserExists("user_community_3@receiptofi.com"));
    }

    @Test
    public void testCreateNewAccount() throws Exception {

    }

    @Test
    public void testInitiateAccountRecovery() throws Exception {
        when(userProfileManager.findOneByMail(anyString())).thenReturn(new UserProfileEntity());
        accountService.initiateAccountRecovery(anyString());

        verify(forgotRecoverManager, atLeastOnce()).save(any(ForgotRecoverEntity.class));
    }

    @Test (expected = Exception.class)
    public void testInitiateAccountRecovery_Fails_When_Saving() throws Exception {
        doThrow(Exception.class).when(forgotRecoverManager).save(anyObject());
        when(userProfileManager.findOneByMail(anyString())).thenReturn(new UserProfileEntity());
        accountService.initiateAccountRecovery(anyString());

        verify(forgotRecoverManager, atLeastOnce()).save(any(ForgotRecoverEntity.class));
    }

    @Test
    public void testInvalidateAllEntries() throws Exception {

    }

    @Test
    public void testFindAccountAuthenticationForKey() throws Exception {

    }

    @Test
    public void testUpdateAuthentication() throws Exception {

    }

    @Test
    public void testGetPreference() throws Exception {

    }
}
