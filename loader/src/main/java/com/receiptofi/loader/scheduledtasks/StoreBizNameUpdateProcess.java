package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.annotation.TemporaryCode;
import com.receiptofi.repository.BizNameManager;

import org.apache.commons.lang3.text.WordUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
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
public class StoreBizNameUpdateProcess {
    private static final Logger LOG = LoggerFactory.getLogger(StoreBizNameUpdateProcess.class);

    private int recordFetchLimit;
    private String storeBizNameUpdateProcess;
    private BizNameManager bizNameManager;

    @Autowired
    public StoreBizNameUpdateProcess(
            @Value ("${recordFetchLimit:1000}")
            int recordFetchLimit,

            @Value ("${storeBizNameUpdateProcess:ON}")
            String storeBizNameUpdateProcess,

            BizNameManager bizNameManager
    ) {
        this.recordFetchLimit = recordFetchLimit;
        this.storeBizNameUpdateProcess = storeBizNameUpdateProcess;
        this.bizNameManager = bizNameManager;
    }

    @Scheduled (cron = "${loader.StoreBizNameUpdateProcess.updateNameOfBiz}")
    public void updateNameOfBiz() {
        LOG.info("begins");
        if ("ON".equalsIgnoreCase(storeBizNameUpdateProcess)) {
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
                LOG.info("Complete updated bizName count={} success={} failure={}", total, success, failure);
            }
        } else {
            LOG.info("feature is {}", storeBizNameUpdateProcess);
        }
    }
}
