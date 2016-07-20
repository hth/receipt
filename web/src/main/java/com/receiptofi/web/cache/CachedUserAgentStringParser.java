package com.receiptofi.web.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pieroxy.ua.detection.UserAgentDetectionResult;
import net.pieroxy.ua.detection.UserAgentDetector;

import java.util.concurrent.TimeUnit;

/**
 * User: hitender
 * Date: 5/26/13
 * Time: 2:46 PM
 *
 * @link http://uadetector.sourceforge.net/usage.html#usage_in_a_servlet
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class CachedUserAgentStringParser {
    private static final Logger LOG = LoggerFactory.getLogger(CachedUserAgentStringParser.class);

    //Set cache parameters
    private final Cache<String, UserAgentDetectionResult> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build();

    private CachedUserAgentStringParser() {
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        static final CachedUserAgentStringParser INSTANCE = new CachedUserAgentStringParser();

        private SingletonHolder() {
        }
    }

    public static CachedUserAgentStringParser getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public UserAgentDetectionResult parse(String userAgentString) {
        UserAgentDetectionResult result = cache.getIfPresent(userAgentString);
        if (null == result) {
            LOG.info("Cache : No : UserAgentString: {}", userAgentString);
            result = new UserAgentDetector().parseUserAgent(userAgentString);
            cache.put(userAgentString, result);
        }
        return result;
    }
}
