package com.receiptofi.domain;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * User: hitender
 * Date: 1/4/17 1:49 AM
 */
public abstract class AbstractDomain {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractDomain.class);

    /**
     * @deprecated (This adds tons of accept charset.)
     * Converts this object to JSON representation;
     * do not use annotation as this breaks and content length is set to -1
     */
    @Deprecated
    public String asJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Writer writer = new StringWriter();
            mapper.writeValue(writer, this);
            return writer.toString();
        } catch (IOException e) {
            LOG.error("transforming object error={}", e.getLocalizedMessage(), e);
            return "{}";
        }
    }
}
