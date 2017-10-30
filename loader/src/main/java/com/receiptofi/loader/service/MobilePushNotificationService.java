package com.receiptofi.loader.service;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsDelegate;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.DeliveryError;
import com.receiptofi.domain.RegisteredDeviceEntity;
import com.receiptofi.domain.json.fcm.JsonMessage;
import com.receiptofi.domain.types.NotificationSendStateEnum;
import com.receiptofi.repository.RegisteredDeviceManager;
import com.receiptofi.utils.DateUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
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

    public List<NotificationSendStateEnum> sendNotification(String message, String rid, boolean sound) {
        List<RegisteredDeviceEntity> registeredDevices = registeredDeviceManager.getDevicesForRid(rid);

        List<NotificationSendStateEnum> notificationSendStates = new LinkedList<>();
        for (RegisteredDeviceEntity registeredDevice : registeredDevices) {
            LOG.info("Invoked notification for rid={} deviceType={}", registeredDevice.getReceiptUserId(), registeredDevice.getDeviceType());
            switch (registeredDevice.getDeviceType()) {
                case A:
                    notificationSendStates.add(invokeGoogleNotification(message, rid, registeredDevice));
                    break;
                case I:
                    notificationSendStates.add(invokeAppleNotification(message, rid, registeredDevice, sound));
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

    private NotificationSendStateEnum invokeAppleNotification(String message, String rid, RegisteredDeviceEntity registeredDevice, boolean sound) {
        LOG.info("Invoked apple notification rid={}", rid);

        NotificationSendStateEnum notificationSendState = NotificationSendStateEnum.FAILED;
        if (null == registeredDevice.getToken()) {
            LOG.info("Skipped notifying as token is missing rid={}", rid);
        } else {
            String payload;

            if (sound) {
                payload = APNS.newPayload()
                        .alertBody(message)
                        .sound("default")
                        .instantDeliveryOrSilentNotification()
                        .build();
            } else {
                payload = APNS.newPayload()
                        .alertBody(message)
                        .instantDeliveryOrSilentNotification()
                        .build();
            }
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
