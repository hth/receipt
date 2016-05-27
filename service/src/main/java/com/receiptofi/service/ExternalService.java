package com.receiptofi.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PlacesApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.PlaceDetails;

import com.receiptofi.domain.BizStoreEntity;

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
     * Find and populate Address, Latitude and Longitude for a given address from Google API Service.
     *
     * @param bizStore
     */
    public void decodeAddress(BizStoreEntity bizStore) {
        try {
            GeocodingResult[] results = getGeocodingResults(bizStore.getAddress());
            if (null != results && results.length > 0) {
                Assert.notNull(results[0].geometry, "Address is null hence geometry is null");
                Assert.notNull(results[0].geometry.location, "Geometry is null hence location is null");

                String formattedAddress = results[0].formattedAddress;
                bizStore.setAddress(formattedAddress);

                for (AddressComponent addressComponent : results[0].addressComponents) {
                    for (AddressComponentType addressComponentType : addressComponent.types) {
                        switch (addressComponentType) {
                            case COUNTRY:
                                LOG.debug("country code={}", addressComponent.shortName);
                                bizStore.setCountryShortName(addressComponent.shortName);
                                break;
                        }
                    }
                }

                if (null != results[0].geometry) {
                    double lat = results[0].geometry.location.lat;
                    double lng = results[0].geometry.location.lng;
                    bizStore.setCoordinate(lat, lng);
                }

                String placeId = results[0].placeId;
                bizStore.setPlaceId(placeId);

                PlaceDetails placeDetails = getPlaceDetails(placeId);
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
            return PlacesApi.placeDetails(context, placeId).await();
        } catch (Exception e) {
            LOG.error("Failed fetching from google placeId={} reason={}", placeId, e.getLocalizedMessage(), e);
        }
        return null;
    }
}
