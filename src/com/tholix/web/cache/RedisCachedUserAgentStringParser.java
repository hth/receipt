package com.tholix.web.cache;

import org.apache.log4j.Logger;

import org.springframework.cache.annotation.Cacheable;

/**
 * User: hitender
 * Date: 6/1/13
 * Time: 5:54 PM
 * http://blog.joshuawhite.com/java/caching-with-spring-data-redis/
 * @deprecated could not use with Redis cacheable
 */
public class RedisCachedUserAgentStringParser {
    private static final Logger log = Logger.getLogger(RedisCachedUserAgentStringParser.class);

    @Cacheable(value="name", condition="'hitender'.equals(#name)")
    public String getName(String name) {
        log.info("Not from cache : " + name);
        return "Hello " + name + "!";
    }
}
