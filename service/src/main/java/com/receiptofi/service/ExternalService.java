package com.receiptofi.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.value.Coordinate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * User: hitender
 * Date: 5/9/13
 * Time: 7:51 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class ExternalService {
    private static final Logger LOG = LoggerFactory.getLogger(ExternalService.class);

    private GeoApiContext context;

    @Autowired
    public ExternalService(
            @Value ("${google-server-api-key}")
            String googleServerApiKey
    ) {
        this.context = new GeoApiContext().setApiKey(googleServerApiKey);
    }

    /**
     * Find and populate Address, Latitude and Longitude for a given address from Google API Service.
     *
     * @param bizStore
     */
    public void decodeAddress(BizStoreEntity bizStore) {
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, bizStore.getAddress()).await();
            Assert.notNull(results, "Geocoding result from address is null");
            Assert.notNull(results[0].geometry, "Address is null hence geometry is null");
            Assert.notNull(results[0].geometry.location, "Geometry is null hence location is null");

            String formattedAddress = results[0].formattedAddress;
            bizStore.setAddress(formattedAddress);

            double lat = results[0].geometry.location.lat;
            double lng = results[0].geometry.location.lng;

            bizStore.setCoordinate(new Coordinate(lat, lng));
            bizStore.setValidatedUsingExternalAPI(true);
        } catch (Exception e) {
            LOG.error("Failed to get address from google java API service, reason={}", e.getLocalizedMessage(), e);
        }
    }
}
