package com.receiptofi.loader.service;

import com.receiptofi.domain.RegisteredDeviceEntity;
import com.receiptofi.domain.json.fcm.JsonMessage;
import com.receiptofi.domain.types.NotificationSendStateEnum;
import com.receiptofi.repository.RegisteredDeviceManager;
import com.receiptofi.utils.CommonUtil;
import com.receiptofi.utils.DateUtil;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsDelegate;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.DeliveryError;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * User: hitender
 * Date: 8/30/15 11:50 AM
 */
@Service
public class MobilePushNotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(MobilePushNotificationService.class);

    private static final String FCM_LINK = "https://fcm.googleapis.com/fcm/send";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String firebaseServerKey;
    private String authorizationKey;
    private RegisteredDeviceManager registeredDeviceManager;
    private ApnsService apnsService;
    private OkHttpClient client;

    @Autowired
    public MobilePushNotificationService(
            @Value ("${firebase.server.key}")
            String firebaseServerKey,

            @Value ("${braintree.environment}")
            String brainTreeEnvironment,

            @Value ("${apns.cert.password}")
            String apnsCertificatePassword,

            RegisteredDeviceManager registeredDeviceManager
    ) {
        Assert.notNull(apnsCertificatePassword, "Empty cert password");

        this.firebaseServerKey = firebaseServerKey;
        this.authorizationKey = "key=" + firebaseServerKey;
        this.registeredDeviceManager = registeredDeviceManager;

        if ("PRODUCTION".equals(brainTreeEnvironment)) {
            this.apnsService = APNS.newService()
                    .withCert(this.getClass().getClassLoader().getResourceAsStream("/cert/aps_prod_credentials.p12"), apnsCertificatePassword)
                    .withProductionDestination()
                    .withDelegate(getDelegate())
                    .build();
        } else {
            this.apnsService = APNS.newService()
                    .withCert(this.getClass().getClassLoader().getResourceAsStream("/cert/aps_sandbox_credentials.p12"), apnsCertificatePassword)
                    .withSandboxDestination()
                    .withDelegate(getDelegate())
                    .build();
        }

        client = new OkHttpClient();
    }

    private ApnsDelegate getDelegate() {
        return new ApnsDelegate() {
            public void notificationsResent(int resendCount) {
                LOG.info("resendCount={}", resendCount);
            }

            public void messageSent(ApnsNotification message, boolean resent) {
                LOG.info("Message sent. Payload={}", message);
            }

            public void messageSendFailed(ApnsNotification message, Throwable e) {
                LOG.warn("Message send failed. Message={} token={} {} reason={}",
                        message.toString(),
                        message.getDeviceToken().toString(),
                        message.getPayload().toString(),
                        e.getLocalizedMessage(), e);
                throw new RuntimeException("Failed to send Apple Notification");
            }

            public void connectionClosed(DeliveryError e, int messageIdentifier) {
                LOG.error("Connection closed. Message={}", e.toString());
            }

            public void cacheLengthExceeded(int newCacheLength) {
                LOG.error("{}", newCacheLength);
            }
        };
    }

    public List<NotificationSendStateEnum> sendNotification(String message, String rid) {
        List<RegisteredDeviceEntity> registeredDevices = registeredDeviceManager.getDevicesForRid(rid);

        List<NotificationSendStateEnum> notificationSendStates = new LinkedList<>();
        for (RegisteredDeviceEntity registeredDevice : registeredDevices) {
            LOG.info("Invoked notification for rid={} deviceType={}", registeredDevice.getReceiptUserId(), registeredDevice.getDeviceType());
            switch (registeredDevice.getDeviceType()) {
                case A:
                    notificationSendStates.add(invokeGoogleNotification(message, rid, registeredDevice));
                    break;
                case I:
                    notificationSendStates.add(invokeAppleNotification(message, rid, registeredDevice));
                    break;
                default:
                    LOG.error("DeviceTypeEnum={} not defined", registeredDevice.getDeviceType());
                    throw new UnsupportedOperationException("DeviceTypeEnum not supported " + registeredDevice.getDeviceType());
            }
        }

        return notificationSendStates;
    }

    private NotificationSendStateEnum invokeGoogleNotification(String message, String rid, RegisteredDeviceEntity registeredDevice) {
        NotificationSendStateEnum notificationSendState = NotificationSendStateEnum.FAILED;

        JsonMessage jsonMessage = new JsonMessage(registeredDevice.getToken(), message);
        LOG.info("Message body={}", jsonMessage.asJson());

        RequestBody body = RequestBody.create(JSON, jsonMessage.asJson());
        Request request = new Request.Builder()
                .url(FCM_LINK)
                .addHeader("Authorization", authorizationKey)
                .post(body)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            LOG.error("Error making FCM request reason={}", e.getLocalizedMessage(), e);
            return notificationSendState;
        }

        LOG.debug("FCM success topic={} response={}", registeredDevice.getToken(), response.body());

        if (!response.isSuccessful()) {
            if (DateUtil.getDaysBetween(registeredDevice.getUpdated(), DateUtil.nowDate()) > 45) {
                LOG.warn("Deleting {} device older than 45 days rid={} did={}",
                        registeredDevice.getDeviceType(), rid, registeredDevice.getDeviceId());
                registeredDeviceManager.deleteHard(rid, registeredDevice.getDeviceId());
            } else {
                LOG.warn("Error while sending notification reason={} deviceId={} rid={}",
                        response.toString(), registeredDevice.getDeviceId(), rid);
            }
        } else {
            notificationSendState = NotificationSendStateEnum.SUCCESS;
        }

        return notificationSendState;
    }

    @Deprecated
    private NotificationSendStateEnum invokeGoogleNotificationObsolete(String message, String rid, RegisteredDeviceEntity registeredDevice) {
        NotificationSendStateEnum notificationSendState = NotificationSendStateEnum.FAILED;
        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.
            JSONObject jGcmData = new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("message", message);
            // Where to send GCM message.
            jGcmData.put("to", "/topics/" + registeredDevice.getDeviceId());

            // What to send in GCM message.
            jGcmData.put("data", jData);

            // Create connection to send GCM Message request.
            URL url = new URL(FCM_LINK);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + firebaseServerKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());

            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            String resp = IOUtils.toString(inputStream);
            if (CommonUtil.isJSONValid(resp)) {
                try {
                    org.json.JSONObject jo = new org.json.JSONObject(resp);
                    if (jo.has("error")) {
                        if (DateUtil.getDaysBetween(registeredDevice.getUpdated(), DateUtil.nowDate()) > 45) {
                            LOG.warn("Deleting {} device older than 45 days rid={} did={}",
                                    registeredDevice.getDeviceType(), rid, registeredDevice.getDeviceId());
                            registeredDeviceManager.deleteHard(rid, registeredDevice.getDeviceId());
                        } else {
                            LOG.warn("Error while sending notification reason={} deviceId={} rid={}",
                                    jo.getString("error"), registeredDevice.getDeviceId(), rid);
                        }
                    }

                    if (jo.has("message_id")) {
                        LOG.info("Success sending notification messageId={} deviceId={} rid={}",
                                jo.getInt("message_id"), registeredDevice.getDeviceId(), rid);

                        notificationSendState = NotificationSendStateEnum.SUCCESS;
                    }
                } catch (JSONException e) {
                    LOG.error("Failed parsing JSON string={}", resp);
                }
            } else {
                LOG.info(resp);
            }
        } catch (IOException e) {
            LOG.error("Unable to send GCM message. Reason={}", e.getLocalizedMessage(), e);
        }

        return notificationSendState;
    }

    private NotificationSendStateEnum invokeAppleNotification(String message, String rid, RegisteredDeviceEntity registeredDevice) {
        LOG.info("Invoked apple notification rid={}", rid);

        NotificationSendStateEnum notificationSendState = NotificationSendStateEnum.FAILED;
        if (null == registeredDevice.getToken()) {
            LOG.info("Skipped notifying as token is missing rid={}", rid);
        } else {
            String payload = APNS.newPayload()
                    .alertBody(message)
                    .sound("default")
                    .instantDeliveryOrSilentNotification()
                    .build();
            try {
                apnsService.push(registeredDevice.getToken(), payload);
                Map<String, Date> inactiveDevices = apnsService.getInactiveDevices();
                for (String id : inactiveDevices.keySet()) {
                    if (DateUtil.getDaysBetween(registeredDevice.getUpdated(), DateUtil.nowDate()) > 45) {
                        LOG.warn("Deleting {} device older than 45 days rid={} did={}",
                                registeredDevice.getDeviceType(), rid, registeredDevice.getDeviceId());
                        registeredDeviceManager.deleteHard(rid, registeredDevice.getDeviceId());
                    } else {
                        LOG.warn("Apple inactive rid={} token={} id={} date={}",
                                rid, registeredDevice.getToken(), id, inactiveDevices.get(id));
                    }
                }

                if (inactiveDevices.isEmpty()) {
                    notificationSendState = NotificationSendStateEnum.SUCCESS;
                }
            } catch (RuntimeException e) {
                LOG.error("Failed sending Apple Notification {} {} {} reason={}",
                        rid, registeredDevice.getDeviceId(), message, e.getMessage(), e);
            }
        }

        return notificationSendState;
    }
}
