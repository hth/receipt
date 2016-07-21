package com.receiptofi.web.listener;

import com.receiptofi.web.cache.RedisCacheConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

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
}
