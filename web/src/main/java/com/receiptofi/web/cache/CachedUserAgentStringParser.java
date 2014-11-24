package com.receiptofi.web.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * User: hitender
 * Date: 5/26/13
 * Time: 2:46 PM
 *
 * @see http://uadetector.sourceforge.net/usage.html#usage_in_a_servlet
 */
public final class CachedUserAgentStringParser implements UserAgentStringParser {
    private static final Logger LOG = LoggerFactory.getLogger(CachedUserAgentStringParser.class);

    private static UserAgentStringParser parser = UADetectorServiceFactory.getCachingAndUpdatingParser();

    //Set cache parameters
    private final Cache<String, ReadableUserAgent> cache = CacheBuilder.newBuilder()
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
        public static final CachedUserAgentStringParser INSTANCE = new CachedUserAgentStringParser();

        private SingletonHolder() {
        }
    }

    public static CachedUserAgentStringParser getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public String getDataVersion() {
        return parser.getDataVersion();
    }

    @Override
    public ReadableUserAgent parse(final String userAgentString) {
        ReadableUserAgent result = cache.getIfPresent(userAgentString);
        if (null == result) {
            LOG.info("Cache : No : UserAgentString: {}", userAgentString);
            result = parser.parse(userAgentString);
            cache.put(userAgentString, result);
        }
        return result;
    }

    @Override
    public void shutdown() {
        LOG.info("Shutting down - uadetector - UserAgentStringParser");
    }
}
