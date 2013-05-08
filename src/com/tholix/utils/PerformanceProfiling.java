package com.tholix.utils;

import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;

import org.joda.time.DateTime;

/**
 * Helps in profiling the duration it takes to complete a task
 *
 * User: hitender
 * Date: 4/7/13
 * Time: 11:39 AM
 */
public final class PerformanceProfiling {
    private static final Logger log = Logger.getLogger(PerformanceProfiling.class);
    private static final int QUARTER_SECOND = 250;
    private static final int HALF_SECOND = 500;

    /**
     * Logs the start of the process
     *
     * @param type
     * @param message
     * @param <T>
     * @return
     */
    public static <T> Date log(Class<T> type, String... message) {
        Date time = DateUtil.nowTime();
        log.debug(type.getName() + "  " + Arrays.asList(message).toString() + " " + time);
        return time;
    }

    /**
     * Logs the completion of the process
     *
     * @param type
     * @param time
     * @param message
     * @param <T>
     */
    public static <T> void log(Class<T> type, DateTime time, String... message) {
        //log.info(type.getName() + "  " + Arrays.asList(message).toString()  +  ", " + time + ", duration in ss: " + DateUtil.duration(time).getSeconds());
        if(System.currentTimeMillis() - time.getMillis() > QUARTER_SECOND) {
            log.warn(type.getName() + "  " + Arrays.asList(message).toString() + ", " + time + ", duration in ms: " + (System.currentTimeMillis() - time.getMillis()) + " ms");
        } else {
            log.debug(type.getName() + "  " + Arrays.asList(message).toString()  +  ", " + time + ", duration in ms: " + (System.currentTimeMillis() - time.getMillis()) + " ms");
        }
    }

    /**
     * Shows if the log is for a success of the method execution or for a failure
     *
     * @param type
     * @param time
     * @param condition - boolean
     * @param <T>
     */
    public static <T> void log(Class<T> type, DateTime time, String methodName, boolean condition) {
        String message = condition ? "Success" : "Failure";
        log(type, time, methodName, message);
    }
}
