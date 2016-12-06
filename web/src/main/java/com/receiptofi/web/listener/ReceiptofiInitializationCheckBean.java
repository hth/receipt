package com.receiptofi.web.listener;

import com.receiptofi.service.cache.RedisCacheConfig;
import com.receiptofi.service.ftp.FtpService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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

    @Value ("${expensofiReportLocation}")
    private String expensofiReportLocation;

    private RedisCacheConfig redisCacheConfig;
    private FtpService ftpService;

    @Autowired
    public ReceiptofiInitializationCheckBean(RedisCacheConfig redisCacheConfig, FtpService ftpService) {
        this.redisCacheConfig = redisCacheConfig;
        this.ftpService = ftpService;
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
    public void hasAccessToFileSystem() {
        if (!ftpService.exist()) {
            /* Check if access set correctly for the user and remote location exists. */
            LOG.error("Cannot access file system directory, location={}", expensofiReportLocation);
            throw new RuntimeException("File server could not be connected");
        }
        LOG.info("Found and has access, to remote ftp directory={}", expensofiReportLocation);
    }
}
