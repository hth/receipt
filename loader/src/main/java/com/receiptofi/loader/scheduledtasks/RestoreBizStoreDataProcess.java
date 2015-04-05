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
    private BizStoreManager bizStoreManager;
    private ExternalService externalService;

    @Autowired
    public RestoreBizStoreDataProcess(
            @Value ("${searchAddressesNotValidatedThroughExternalApi:true}")
            boolean searchAddressesNotValidatedThroughExternalApi,

            @Value ("${restoreAddresses:ON}")
            String restoreAddresses,

            BizStoreManager bizStoreManager,
            ExternalService externalService
    ) {
        this.searchAddressesNotValidatedThroughExternalApi = searchAddressesNotValidatedThroughExternalApi;
        this.restoreAddresses = restoreAddresses;
        this.bizStoreManager = bizStoreManager;
        this.externalService = externalService;
    }

    @Scheduled (cron = "${loader.RestoreBizStoreDataProcess.restoreAddresses}")
    public void restoreAddresses() {
        LOG.info("begins");
        if ("ON".equalsIgnoreCase(restoreAddresses)) {
            List<BizStoreEntity> bizStores;

            if (searchAddressesNotValidatedThroughExternalApi) {
                LOG.info("Looking for BizStoreEntity that were not updated through external api");
                bizStores = bizStoreManager.getAllWhereNotValidatedUsingExternalAPI();
            } else {
                LOG.info("Updating all the BizStoreEntity data");
                bizStores = bizStoreManager.getAll();
            }

            int success = 0, failure = 0;
            try {
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
            } catch (Exception e) {
                LOG.error("Error and quiting updating, reason={}", e.getLocalizedMessage(), e);
            } finally {
                LOG.info("Complete count={} success={} failure={}", bizStores.size(), success, failure);
            }
        } else {
            LOG.info("feature is {}", restoreAddresses);
        }
    }
}
