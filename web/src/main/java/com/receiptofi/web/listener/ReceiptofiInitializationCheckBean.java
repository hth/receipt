package com.receiptofi.web.listener;

import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.service.cache.RedisCacheConfig;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;

/**
 * Checks if all vital setup are running before starting server.
 * User: hitender
 * Date: 9/6/14 1:52 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class ReceiptofiInitializationCheckBean {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptofiInitializationCheckBean.class);

    private JmsTemplate jmsSenderTemplate;
    private RedisCacheConfig redisCacheConfig;

    @Autowired
    private ReceiptManager receiptManager;

    @Autowired
    public ReceiptofiInitializationCheckBean(JmsTemplate jmsSenderTemplate, RedisCacheConfig redisCacheConfig) {
        this.jmsSenderTemplate = jmsSenderTemplate;
        this.redisCacheConfig = redisCacheConfig;
    }

    @PostConstruct
    public void checkActiveMQ() {
        try {
            jmsSenderTemplate.getConnectionFactory().createConnection();
            LOG.info("ActiveMQ messaging is running");
        } catch (JMSException e) {
            LOG.error("ActiveMQ messaging is unavailable reason={}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @PostConstruct
    public void checkRedisConnection() {
        RedisConnection redisConnection = redisCacheConfig.redisTemplate().getConnectionFactory().getConnection();
        if (redisConnection.isClosed()) {
            LOG.error("Redis Server could not be connected");
            throw new RuntimeException("Redis Server could not be connected");
        }
        LOG.info("Redis Server connected");
    }

    @PostConstruct
    public void updateLocation() {
        List<ReceiptEntity> receipts = receiptManager.getAllReceipts();
        LOG.info("Found receipt size={}", receipts.size());
        int success = 0, skipped = 0;
        for(ReceiptEntity receipt : receipts) {
            if (null != receipt.getBizStore() && StringUtils.isNotBlank(receipt.getBizStore().getCountryShortName())) {
                success ++;
                LOG.info("count={} CS={}", success, receipt.getBizStore().getCountryShortName());
                receipt.setCountryShortName(receipt.getBizStore().getCountryShortName());
                receiptManager.save(receipt);
            } else {
                skipped ++;
                LOG.info("count={} CS={}", skipped, receipt.getId());
            }
        }
        LOG.info("Update receipt success={} skipped={}", success, skipped);
    }
}
