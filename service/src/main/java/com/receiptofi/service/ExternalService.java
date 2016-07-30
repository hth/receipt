package com.receiptofi.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PlacesApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.PlaceDetails;

import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.shared.DecodedAddress;

import org.apache.commons.lang3.StringUtils;

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
     * Find and populate Address, Latitude and Longitude for a given address from Google API Service to bizStore.
     *
     * @param bizStore
     */
    public void decodeAddress(BizStoreEntity bizStore) {
        try {
            DecodedAddress decodedAddress = DecodedAddress.newInstance(getGeocodingResults(bizStore.getAddress()), bizStore.getAddress());
            if (decodedAddress.isNotEmpty()) {
                bizStore.setAddress(decodedAddress.getFormattedAddress());
                bizStore.setPostalCode(decodedAddress.getPostalCode());
                bizStore.setCountryShortName(decodedAddress.getCountryShortName());
                if (null != decodedAddress.getCoordinate()) {
                    bizStore.setCoordinate(decodedAddress.getCoordinate());
                }
                bizStore.setPlaceId(decodedAddress.getPlaceId());

                PlaceDetails placeDetails = getPlaceDetails(decodedAddress.getPlaceId());
                if (null != placeDetails) {
                    bizStore.setPlaceType(placeDetails.types);
                    bizStore.setPlaceRating(placeDetails.rating);
                    if (StringUtils.isNotEmpty(placeDetails.formattedPhoneNumber)) {
                        bizStore.setPhone(placeDetails.formattedPhoneNumber);
                    }
                }

                bizStore.setValidatedUsingExternalAPI(true);
            } else {
                LOG.warn("Geocoding result from address is empty for bizStoreId={} bizStoreAddress={}",
                        bizStore.getId(), bizStore.getAddress());
            }
        } catch (Exception e) {
            LOG.error("Failed to get address from google java API service bizStoreId={} bizStoreAddress={} reason={}",
                    bizStore.getId(), bizStore.getAddress(), e.getLocalizedMessage(), e);
        }
    }

    public GeocodingResult[] getGeocodingResults(String address) {
        try {
            Assert.hasText(address, "Address is empty");
            return GeocodingApi.geocode(context, address).await();
        } catch (Exception e) {
            LOG.error("Failed fetching from google address={} reason={}", address, e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * External call to find types and rating for a particular store.
     *
     * @param placeId
     * @return
     * @throws Exception
     */
    public PlaceDetails getPlaceDetails(String placeId) {
        try {
            Assert.hasText(placeId, "PlaceId is empty");
            return PlacesApi.placeDetails(context, placeId).await();
        } catch (Exception e) {
            LOG.error("Failed fetching from google placeId={} reason={}", placeId, e.getLocalizedMessage(), e);
        }
        return null;
    }
}
