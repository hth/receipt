package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.CronStatsEntity;
import com.receiptofi.domain.annotation.TemporaryCode;
import com.receiptofi.repository.BizNameManager;
import com.receiptofi.service.CronStatsService;

import org.apache.commons.lang3.text.WordUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This was temporary code to change all caps biz name to fully capitalize instead.
 * User: hitender
 * Date: 4/8/15 11:35 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
@TemporaryCode
@Deprecated
public class StoreBizNameUpdateProcess {
    private static final Logger LOG = LoggerFactory.getLogger(StoreBizNameUpdateProcess.class);

    private int recordFetchLimit;
    private String storeBizNameUpdateProcessSwitch;
    private BizNameManager bizNameManager;
    private CronStatsService cronStatsService;

    @Autowired
    public StoreBizNameUpdateProcess(
            @Value ("${recordFetchLimit:1000}")
            int recordFetchLimit,

            @Value ("${storeBizNameUpdateProcessSwitch:OFF}")
            String storeBizNameUpdateProcessSwitch,

            BizNameManager bizNameManager,
            CronStatsService cronStatsService
    ) {
        this.recordFetchLimit = recordFetchLimit;
        this.storeBizNameUpdateProcessSwitch = storeBizNameUpdateProcessSwitch;
        this.bizNameManager = bizNameManager;
        this.cronStatsService = cronStatsService;
    }

    @Scheduled (cron = "${loader.StoreBizNameUpdateProcess.updateNameOfBiz}")
    public void updateNameOfBiz() {
        LOG.info("begins");

        CronStatsEntity cronStats = new CronStatsEntity(
                StoreBizNameUpdateProcess.class.getName(),
                "Update_Name_Of_Biz",
                storeBizNameUpdateProcessSwitch);

        if ("ON".equalsIgnoreCase(storeBizNameUpdateProcessSwitch)) {
            List<BizNameEntity> bizNames;

            int success = 0, failure = 0, total = 0;
            try {
                int skip = 0;
                while (true) {
                    bizNames = bizNameManager.findAll(skip, recordFetchLimit);

                    if (bizNames.isEmpty()) {
                        break;
                    } else {
                        skip += recordFetchLimit;
                        total += bizNames.size();
                    }

                    for (BizNameEntity bizName : bizNames) {
                        try {
                            bizName.setBusinessName(WordUtils.capitalizeFully(bizName.getBusinessName()));
                            bizNameManager.save(bizName);
                            success++;
                        } catch (Exception e) {
                            LOG.error("Error updating bizStore, reason={}", e.getLocalizedMessage(), e);
                            failure++;
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error("Error decoding, reason={}", e.getLocalizedMessage(), e);
            } finally {
                cronStats.addStats("total", total);
                cronStats.addStats("success", success);
                cronStats.addStats("failure", failure);
                cronStatsService.save(cronStats);

                LOG.info("Complete updated bizName count={} success={} failure={}", total, success, failure);
            }
        } else {
            LOG.info("feature is {}", storeBizNameUpdateProcessSwitch);
        }
    }
}
