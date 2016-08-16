package com.receiptofi.social.connect;

import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.types.ProviderEnum;
import com.receiptofi.social.annotation.Social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.stereotype.Component;

@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
@Social
public class ConnectionConverter {
    private final ConnectionFactoryLocator connectionFactoryLocator;
    private final TextEncryptor textEncryptor;

    @Autowired
    public ConnectionConverter(ConnectionFactoryLocator connectionFactoryLocator, TextEncryptor textEncryptor) {
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.textEncryptor = textEncryptor;
    }

    public Connection<?> convert(UserAccountEntity userAccount) {
        if (null == userAccount) {
            return null;
        }

        ConnectionData connectionData = fillConnectionData(userAccount);
        ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId());
        return connectionFactory.createConnection(connectionData);
    }

    private ConnectionData fillConnectionData(UserAccountEntity userAccount) {
        return new ConnectionData(
                userAccount.getProviderId().toString().toLowerCase(),
                userAccount.getProviderUserId(),
                userAccount.getDisplayName(),
                userAccount.getProfileUrl(),
                userAccount.getImageUrl(),
                decrypt(userAccount.getAccessToken()),
                decrypt(userAccount.getSecret()),
                decrypt(userAccount.getRefreshToken()),
                userAccount.getExpireTime()
        );
    }

    /**
     * Should contain connect converter related operation. Any other changes should go in the specific method calls.
     *
     * @param userId
     * @param receiptUserId
     * @param cnn
     * @return
     */
    public UserAccountEntity convert(String userId, String receiptUserId, Connection<?> cnn) {
        ConnectionData data = cnn.createData();

        UserAccountEntity userAccount = UserAccountEntity.newInstance(
                receiptUserId,
                userId,
                "",
                "",
                UserAuthenticationEntity.blankInstance()
        );
        userAccount.setProviderId(ProviderEnum.valueOf(data.getProviderId().toUpperCase()));
        userAccount.setProviderUserId(data.getProviderUserId());

        userAccount.setDisplayName(data.getDisplayName());
        userAccount.setProfileUrl(data.getProfileUrl());
        userAccount.setExpireTime(data.getExpireTime());
        userAccount.setAccessToken(encrypt(data.getAccessToken()));
        userAccount.setImageUrl(data.getImageUrl());

        userAccount.setSecret(encrypt(data.getSecret()));
        userAccount.setRefreshToken(encrypt(data.getRefreshToken()));
        return userAccount;
    }

    public UserAccountEntity convert(String userId, Connection<?> cnn) {
        return convert(userId, null, cnn);
    }

    private String decrypt(String encryptedText) {
        return null == encryptedText ? encryptedText : textEncryptor.decrypt(encryptedText);
    }

    private String encrypt(String text) {
        return null == text ? text : textEncryptor.encrypt(text);
    }
}
