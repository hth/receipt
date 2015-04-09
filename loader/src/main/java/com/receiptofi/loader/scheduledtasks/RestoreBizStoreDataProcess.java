package com.receiptofi.loader.scheduledtasks;

import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.repository.BizStoreManager;
import com.receiptofi.service.ExternalService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * In case latitude, longitude or address data is corrupted, then this process can attempt to fix the issue. Can be
 * used to update new information or field by re-fetching data from external api.
 * User: hitender
 * Date: 4/5/15 1:36 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class RestoreBizStoreDataProcess {
    private static final Logger LOG = LoggerFactory.getLogger(RestoreBizStoreDataProcess.class);

    private boolean searchAddressesNotValidatedThroughExternalApi;
    private String restoreAddresses;
    private int recordFetchLimit;
    private BizStoreManager bizStoreManager;
    private ExternalService externalService;

    @Autowired
    public RestoreBizStoreDataProcess(
            @Value ("${searchAddressesNotValidatedThroughExternalApi:true}")
            boolean searchAddressesNotValidatedThroughExternalApi,

            @Value ("${restoreAddresses:ON}")
            String restoreAddresses,

            @Value ("${recordFetchLimit:100}")
            int recordFetchLimit,

            BizStoreManager bizStoreManager,
            ExternalService externalService
    ) {
        this.searchAddressesNotValidatedThroughExternalApi = searchAddressesNotValidatedThroughExternalApi;
        this.restoreAddresses = restoreAddresses;
        this.recordFetchLimit = recordFetchLimit;
        this.bizStoreManager = bizStoreManager;
        this.externalService = externalService;
    }

    @Scheduled (cron = "${loader.RestoreBizStoreDataProcess.restoreAddresses}")
    public void restoreAddresses() {
        LOG.info("begins");
        if ("ON".equalsIgnoreCase(restoreAddresses)) {
            List<BizStoreEntity> bizStores;

            int success = 0, failure = 0, total = 0;
            try {
                int skip = 0;
                while (true) {
                    if (searchAddressesNotValidatedThroughExternalApi) {
                        LOG.info("Looking for BizStoreEntity that were not updated through external api");
                        bizStores = bizStoreManager.getAllWhereNotValidatedUsingExternalAPI(skip, recordFetchLimit);
                    } else {
                        LOG.info("Updating all the BizStoreEntity data");
                        bizStores = bizStoreManager.getAll(skip, recordFetchLimit);
                    }

                    if (bizStores.isEmpty()) {
                        break;
                    } else {
                        skip += recordFetchLimit;
                        total += bizStores.size();
                    }

                    for (BizStoreEntity bizStore : bizStores) {
                        try {
                            externalService.decodeAddress(bizStore);
                            bizStoreManager.save(bizStore);
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
                LOG.info("Complete searchAddressesNotValidatedThroughExternalApi={} count={} success={} failure={}",
                        searchAddressesNotValidatedThroughExternalApi, total, success, failure);
            }
        } else {
            LOG.info("feature is {}", restoreAddresses);
        }
    }
}
