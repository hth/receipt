package com.tholix.repository.util;

import org.springframework.data.mongodb.core.query.Criteria;

/**
 * User: hitender
 * Date: 6/26/13
 * Time: 10:47 PM
 */
public final class RC {

    public static Criteria isDeleted(Criteria criteria) {
        return criteria.andOperator(Criteria.where("deleted").is(true));
    }

    public static Criteria isActive(Criteria criteria) {
        return criteria.andOperator(Criteria.where("active").is(true));
    }

    public static Criteria isNotDeleted(Criteria criteria) {
        return criteria.andOperator(Criteria.where("deleted").is(false));
    }

    public static Criteria isNotActive(Criteria criteria) {
        return criteria.andOperator(Criteria.where("active").is(false));
    }

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

    @SuppressWarnings("unused")
    public static  Criteria isActiveAndDeleted(Criteria criteria) {
        return isDeleted(isActive(criteria));
    }

    @SuppressWarnings("unused")
    public static  Criteria isActiveAndNotDeleted(Criteria criteria) {
        return isNotDeleted(isActive(criteria));
    }

    @SuppressWarnings("unused")
    public static  Criteria isNotActiveAndNotDeleted(Criteria criteria) {
        return isNotDeleted(isNotActive(criteria));
    }

    @SuppressWarnings("unused")
    public static  Criteria isNotActiveAndDeleted(Criteria criteria) {
        return isDeleted(isNotActive(criteria));
    }

    @SuppressWarnings("unused")
    public static  Criteria isActiveAndDeleted() {
        return isDeleted(isActive(new Criteria()));
    }

    @SuppressWarnings("unused")
    public static  Criteria isActiveAndNotDeleted() {
        return isNotDeleted(isActive(new Criteria()));
    }

    @SuppressWarnings("unused")
    public static  Criteria isNotActiveAndNotDeleted() {
        return isNotDeleted(isNotActive(new Criteria()));
    }

    @SuppressWarnings("unused")
    public static  Criteria isNotActiveAndDeleted() {
        return isDeleted(isNotActive(new Criteria()));
    }

}
