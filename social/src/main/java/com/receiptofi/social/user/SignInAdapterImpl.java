package com.receiptofi.social.user;

import com.receiptofi.domain.types.ProviderEnum;
import com.receiptofi.social.annotation.Social;
import com.receiptofi.social.config.RegistrationConfig;
import com.receiptofi.social.service.CustomUserDetailsService;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Collection;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
@Profile(value = "DEV")
@Social
public final class SignInAdapterImpl implements SignInAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(SignInAdapterImpl.class);

    private static final String completeProfileController = "/access/completeprofile.htm";

    private final RequestCache requestCache;
    private final CustomUserDetailsService customUserDetailsService;
    private final RegistrationConfig registrationConfig;

    @Inject
    public SignInAdapterImpl(RequestCache requestCache, CustomUserDetailsService customUserDetailsService, RegistrationConfig registrationConfig) {
        this.requestCache = requestCache;
        this.customUserDetailsService = customUserDetailsService;
        this.registrationConfig = registrationConfig;
    }

    public String signIn(String localUserId, Connection<?> connection, NativeWebRequest request) {
        UserDetails user;
        if(localUserId.contains("@")) {
            LOG.info("signin in user={} from receiptofi login page", localUserId);
            user = customUserDetailsService.loadUserByUsername(StringUtils.lowerCase(localUserId));
        } else {
            userSignedInUsingProvider(localUserId, request);
            user = customUserDetailsService.loadUserByUserId(localUserId);
        }
        Assert.notNull(user);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return extractOriginalUrl(request, user);
    }

    private void userSignedInUsingProvider(String localUserId, NativeWebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        if(servletWebRequest.getRequest().getRequestURI().contains(ProviderEnum.FACEBOOK.name().toLowerCase())) {
            LOG.info("signin in user={} provider={}", localUserId, ProviderEnum.FACEBOOK);
        } else if(servletWebRequest.getRequest().getRequestURI().contains(ProviderEnum.GOOGLE.name().toLowerCase())) {
            LOG.info("signin in user={} provider={}", localUserId, ProviderEnum.GOOGLE);
        }
    }

    private String extractOriginalUrl(NativeWebRequest request, UserDetails user) {
        HttpServletRequest nativeReq = request.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse nativeRes = request.getNativeResponse(HttpServletResponse.class);
        SavedRequest saved = requestCache.getRequest(nativeReq, nativeRes);

        if(registrationConfig.checkRegistrationIsTurnedOn(user)) {
            return registrationConfig.getIndexController();
        }

        if(isProfileNotComplete(user)) {
            LOG.info("profile not complete, user={}", user.getUsername());
            return completeProfileController;
        }

        if(saved == null) {
            return null;
        }

        requestCache.removeRequest(nativeReq, nativeRes);
        removeAuthenticationAttributes(nativeReq.getSession(false));
        return saved.getRedirectUrl();
    }

    private void removeAuthenticationAttributes(HttpSession session) {
        if(session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    private boolean isProfileNotComplete(UserDetails user) {
        return !user.getUsername().contains("@");
    }
}