package com.receiptofi.social.connect;

import com.receiptofi.domain.types.ProviderEnum;
import com.receiptofi.social.annotation.Social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;

import java.util.List;
import java.util.Set;

/**
 * sadly there is no default implementation for Mongo :(.
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Social
public class MongoUsersConnectionRepository implements UsersConnectionRepository {

    private String userId;

    private ConnectionService connectionService;
    private ConnectionFactoryLocator connectionFactoryLocator;
    private TextEncryptor textEncryptor;
    private ConnectionSignUp connectionSignUp;

    @Autowired
    public MongoUsersConnectionRepository(
            String userId,
            ConnectionService connectionService,
            ConnectionFactoryLocator connectionFactoryLocator,
            TextEncryptor textEncryptor) {
        this.userId = userId;
        this.connectionService = connectionService;
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.textEncryptor = textEncryptor;
    }

    public MongoUsersConnectionRepository(
            ConnectionFactoryLocator connectionFactoryLocator,
            TextEncryptor textEncryptor) {
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.textEncryptor = textEncryptor;
    }

    public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
        this.connectionSignUp = connectionSignUp;
    }

    public void removeConnections(ProviderEnum providerId) {
        connectionService.remove(userId, providerId);
    }

    public void removeConnection(ConnectionKey connectionKey) {
        connectionService.remove(userId, connectionKey);
    }

    /**
     * Find or create new user account.
     *
     * @param connection
     * @return
     */
    public List<String> findUserIdsWithConnection(final Connection<?> connection) {
        ProviderEnum providerId = ProviderEnum.valueOf(connection.getKey().getProviderId().toUpperCase());
        List<String> userIds = connectionService.getUserIds(providerId, connection.getKey().getProviderUserId());

        if (userIds.isEmpty()) {
            connectionService.create(connection.getKey().getProviderUserId(), connection);
            userIds.add(connection.getKey().getProviderUserId());
        }

        return userIds;
    }

    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        return connectionService.getUserIds(ProviderEnum.valueOf(providerId), providerUserIds);
    }

    public ConnectionRepository createConnectionRepository(String userId) {
        if (null == userId) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return new MongoConnectionRepository(userId, connectionService, connectionFactoryLocator, textEncryptor);
    }
}
