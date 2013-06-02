package com.tholix.web.cache;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import net.sf.uadetector.UserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * User: hitender
 * Date: 5/26/13
 * Time: 2:46 PM
 *
 * @see http://uadetector.sourceforge.net/usage.html#usage_in_a_servlet
 */
public final class CachedUserAgentStringParser implements UserAgentStringParser {
    private static final Logger log = Logger.getLogger(CachedUserAgentStringParser.class);

    private static UserAgentStringParser parser = UADetectorServiceFactory.getCachingAndUpdatingParser();
    private static CachedUserAgentStringParser instance;

    //Set cache parameters
    private final Cache<String, UserAgent> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build();

    public static CachedUserAgentStringParser newInstance() {
        if(instance == null) {
            instance = new CachedUserAgentStringParser();
            parser = UADetectorServiceFactory.getCachingAndUpdatingParser();
        }
        return instance;
    }

    @Override
    public String getDataVersion() {
        return parser.getDataVersion();
    }

    @Override
    public UserAgent parse(final String userAgentString) {
        UserAgent result = cache.getIfPresent(userAgentString);
        if (result == null) {
            log.info("Cache : No : UserAgentString: " + userAgentString);
            result = parser.parse(userAgentString);
            cache.put(userAgentString, result);
        }
        return result;
    }

}