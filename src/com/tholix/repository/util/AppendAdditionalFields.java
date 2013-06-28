package com.tholix.repository.util;

import org.apache.log4j.Logger;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import com.tholix.utils.DateUtil;

/**
 * User: hitender
 * Date: 6/26/13
 * Time: 10:47 PM
 */
public final class AppendAdditionalFields {
    private static final Logger log = Logger.getLogger(AppendAdditionalFields.class);

    public static Criteria isDeleted() {
        return Criteria.where("deleted").is(true);
    }

    public static Criteria isActive() {
        return Criteria.where("active").is(true);
    }

    public static Criteria isNotDeleted() {
        return Criteria.where("deleted").is(false);
    }

    public static Criteria isNotActive() {
        return Criteria.where("active").is(false);
    }

    /**
     * Appends BaseEntity fields
     *
     * This method updates the version and last update date for a Entity that is updated directly by a query
     *
     * @param update
     * @return
     */
    public static Update update(Update update) {
        if(update != null) {
            return update.set("updated", DateUtil.nowTime()).inc("version", 1);
        } else {
            log.error("Update cannot be null");
        }
        return null;
    }

}
