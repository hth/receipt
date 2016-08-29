package com.receiptofi.security.social.repository;

import com.receiptofi.domain.social.RememberMeTokenEntity;
import com.receiptofi.repository.social.RememberMeTokenManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * User: hitender
 * Date: 3/30/14 4:23 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
@Qualifier ("persistentTokenRepositoryImpl")
public class PersistentTokenRepositoryImpl implements PersistentTokenRepository {

    private final RememberMeTokenManager rememberMeTokenManager;

    @Autowired
    public PersistentTokenRepositoryImpl(RememberMeTokenManager rememberMeTokenManager) {
        this.rememberMeTokenManager = rememberMeTokenManager;
    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        RememberMeTokenEntity newToken = new RememberMeTokenEntity(token);
        rememberMeTokenManager.save(newToken);
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        RememberMeTokenEntity token = rememberMeTokenManager.findBySeries(series);
        if (token != null) {
            rememberMeTokenManager.updateToken(token.getId(), tokenValue);
        }
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        RememberMeTokenEntity token = rememberMeTokenManager.findBySeries(seriesId);
        if (token != null) {
            return new PersistentRememberMeToken(
                    token.getUsername(),
                    token.getSeries(),
                    token.getTokenValue(),
                    token.getUpdated()
            );
        }
        /* This can happen when data is reset or token is missing. Returning null will send user to login page */
        return null;
    }

    @Override
    public void removeUserTokens(String username) {
        rememberMeTokenManager.deleteTokensWithUsername(username);
    }
}
