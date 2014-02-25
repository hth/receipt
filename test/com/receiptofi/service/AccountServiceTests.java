package com.receiptofi.service;

import com.receiptofi.domain.ForgotRecoverEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.repository.ForgotRecoverManager;
import com.receiptofi.repository.UserAuthenticationManager;
import com.receiptofi.repository.UserPreferenceManager;
import com.receiptofi.repository.UserProfileManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * User: hitender
 * Date: 2/19/14 11:21 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTests {

    @Mock private UserAuthenticationManager userAuthenticationManager;
    @Mock private UserProfileManager userProfileManager;
    @Mock private UserPreferenceManager userPreferenceManager;
    @Mock private ForgotRecoverManager forgotRecoverManager;

    private AccountService accountService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        accountService = new AccountService(
                userAuthenticationManager,
                userProfileManager,
                userPreferenceManager,
                forgotRecoverManager
        );
    }

    @Test
    public void testFindIfUser_Does_Not_Exists() throws Exception {
        when(userProfileManager.findOneByEmail(anyString())).thenReturn(null);
        assertNull(accountService.findIfUserExists("user_community_3@receiptofi.com"));
    }

    @Test
    public void testCreateNewAccount() throws Exception {

    }

    @Test
    public void testInitiateAccountRecovery() throws Exception {
        when(userProfileManager.findOneByEmail(anyString())).thenReturn(new UserProfileEntity());
        accountService.initiateAccountRecovery(new UserProfileEntity());

        verify(forgotRecoverManager, atLeastOnce()).save(any(ForgotRecoverEntity.class));
    }

    @Test(expected = Exception.class)
    public void testInitiateAccountRecovery_Fails_When_Saving() throws Exception {
        doThrow(new Exception()).when(forgotRecoverManager).save((ForgotRecoverEntity) anyObject());
        when(userProfileManager.findOneByEmail(anyString())).thenReturn(new UserProfileEntity());
        accountService.initiateAccountRecovery(new UserProfileEntity());

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
