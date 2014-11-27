package com.receiptofi.web.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.Cacheable;

/**
 * User: hitender
 * Date: 6/1/13
 * Time: 5:54 PM
 * link http://blog.joshuawhite.com/java/caching-with-spring-data-redis/
 *
 * @deprecated could not use with Redis cacheable
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Deprecated
public class RedisCachedUserAgentStringParser {
    private static final Logger LOG = LoggerFactory.getLogger(RedisCachedUserAgentStringParser.class);

    @Cacheable (value = "name", condition = "'hitender'.equals(#name)")
    public String getName(String name) {
        LOG.info("Not from cache : " + name);
        return "Hello " + name + "!";
    }
}
