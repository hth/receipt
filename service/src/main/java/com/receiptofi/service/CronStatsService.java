package com.receiptofi.service;

import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.repository.CronStatsManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * User: hitender
 * Date: 4/21/15 1:26 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class CronStatsService {
    @Autowired private CronStatsManager cronStatsManager;

    public void save(CronStatsEntity cronStats) {
        cronStats.setEnd(new Date());
        cronStatsManager.save(cronStats);
    }

    public void deleteHard(CronStatsEntity cronStats) {
        cronStatsManager.deleteHard(cronStats);
    }
}
