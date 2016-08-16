package com.receiptofi.social.service;

import com.receiptofi.social.annotation.Social;
import com.receiptofi.utils.ParseJsonStringToMap;
import com.receiptofi.utils.ScrubbedInput;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Retrieves Google access token and refresh token from authorization code. This activity is used when mobile device
 * retrieves authorization code which is used by web application to get access token to get profile info and refresh
 * token to be used in future under offline scenario.
 * User: hitender
 * Date: 12/7/14 8:02 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Social
@Service
class GoogleAccessTokenService {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleAccessTokenService.class);
    private String googleAuthenticationTokenRetrieval;
    private String googleClientId;
    private String googleClientSecret;

    @Autowired
    public GoogleAccessTokenService(
            @Value ("${google.authentication.token.retrieval:https://www.googleapis.com/oauth2/v3/token}")
            String googleAuthenticationTokenRetrieval,

            @Value ("${google.client.id}")
            String googleClientId,

            @Value ("${google.client.secret}")
            String googleClientSecret
    ) {
        this.googleAuthenticationTokenRetrieval = googleAuthenticationTokenRetrieval;
        this.googleClientId = googleClientId;
        this.googleClientSecret = googleClientSecret;
    }

    /**
     * Takes in authorization code to get access token and refresh token.
     * @param authorizationCode
     * @return
     */
    Map<String, ScrubbedInput> getTokenForAuthorizationCode(String authorizationCode) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(googleAuthenticationTokenRetrieval);
        LOG.info("Auth code={}", authorizationCode);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("code", authorizationCode));
        urlParameters.add(new BasicNameValuePair("client_id", googleClientId));
        urlParameters.add(new BasicNameValuePair("client_secret", googleClientSecret));
        urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));

        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
        } catch (UnsupportedEncodingException e) {
            LOG.error("url parameter encoding reason={}", e.getLocalizedMessage(), e);
        }

        Map<String, ScrubbedInput> map = new HashMap<>();
        try {
            HttpResponse response = client.execute(post);
            String jsonResponse = EntityUtils.toString(response.getEntity());
            if (200 == response.getStatusLine().getStatusCode()) {
                LOG.info("body={}", jsonResponse);
                map = ParseJsonStringToMap.jsonStringToMap(jsonResponse);
            } else {
                LOG.error("response from google with status code={} reason={}", response.getStatusLine().getStatusCode(), jsonResponse);
                throw new HttpClientErrorException(HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
            }
        } catch (IOException e) {
            LOG.error("error executing url={} reason={}",
                    googleAuthenticationTokenRetrieval,
                    e.getLocalizedMessage(),
                    e);
        }

        return map;
    }
}
