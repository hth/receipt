package com.receiptofi.social.config;

import com.receiptofi.service.RegistrationService;
import com.receiptofi.social.annotation.Social;
import com.receiptofi.social.connect.ConnectionConverter;
import com.receiptofi.social.connect.ConnectionServiceImpl;
import com.receiptofi.social.connect.MongoUsersConnectionRepository;
import com.receiptofi.social.service.CustomUserDetailsService;
import com.receiptofi.social.user.SignInAdapterImpl;
import com.receiptofi.social.user.SimpleConnectionSignUp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.connect.GoogleConnectionFactory;

/**
 * User: hitender
 * Date: 5/10/14 12:35 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Configuration
@Social
public class SocialConfig {
    private static final Logger LOG = LoggerFactory.getLogger(SocialConfig.class);

    @Value ("${facebook.client.id}")
    private String facebookClientId;

    @Value ("${facebook.client.secret}")
    private String facebookClientSecret;

    @Value ("${google.client.id}")
    private String googleClientId;

    @Value ("${google.client.secret}")
    private String googleClientSecret;

    @Value ("${indexController:/open/login.htm}")
    private String loginController;

    @Value ("${accessLanding:/access/landing.htm}")
    private String accessLanding;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private RegistrationService registrationService;

    /**
     * When a new provider is added to the app, register its {@link org.springframework.social.connect.ConnectionFactory} here.
     *
     * @see org.springframework.social.google.connect.GoogleConnectionFactory
     */
    @Bean
    @Scope (value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
    public ConnectionFactoryLocator connectionFactoryLocator() {
        LOG.info("Initializing connectionFactoryLocator");
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
        registry.addConnectionFactory(new FacebookConnectionFactory(facebookClientId, facebookClientSecret));
        registry.addConnectionFactory(new GoogleConnectionFactory(googleClientId, googleClientSecret));
        return registry;
    }

    /**
     * Singleton data access object providing access to connections across all users.
     */
    @Bean
    public UsersConnectionRepository usersConnectionRepository() {
        LOG.info("Initializing usersConnectionRepository");
        return new MongoUsersConnectionRepository(connectionFactoryLocator(), Encryptors.noOpText());
    }

    /**
     * The Spring MVC Controller that allows users to sign-in with their provider accounts.
     */
    @Bean
    public ProviderSignInController providerSignInController(RequestCache requestCache) {
        LOG.info("Initializing ProviderSignInController");
        ConnectionFactoryLocator connFactLocator = connectionFactoryLocator();
        UsersConnectionRepository usrConnRepo = usersConnectionRepository();
        SignInAdapterImpl signInAdapter = new SignInAdapterImpl(requestCache, customUserDetailsService, registrationService);
        ProviderSignInController controller = new ProviderSignInController(connFactLocator, usrConnRepo, signInAdapter);

        controller.setSignUpUrl(loginController);
        controller.setSignInUrl(loginController);
        controller.setPostSignInUrl(accessLanding);

        return controller;
    }

    @Bean
    public TextEncryptor textEncryptor() {
        LOG.info("Initializing textEncryptor");
        return Encryptors.noOpText();
    }

    @Bean
    public ConnectionConverter connectionConverter() {
        LOG.info("Initializing connectionConverter");
        return new ConnectionConverter(connectionFactoryLocator(), textEncryptor());
    }

    @Bean
    public ConnectionServiceImpl mongoConnectionService() {
        LOG.info("Initializing mongoConnectionService");
        return new ConnectionServiceImpl(
                mongoTemplate,
                connectionConverter()
        );
    }
}
