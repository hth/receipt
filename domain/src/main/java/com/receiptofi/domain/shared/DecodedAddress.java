package com.receiptofi.domain.shared;

import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

/**
 * User: hitender
 * Date: 5/23/16 1:00 PM
 */
public class DecodedAddress {
    private static final Logger LOG = LoggerFactory.getLogger(DecodedAddress.class);

    private String formattedAddress;
    private String countryShortName;
    private double lat;
    private double lng;
    private String placeId;
    private boolean empty = true;

    public DecodedAddress(GeocodingResult[] results) {
        if (null != results && results.length > 0) {
            empty = false;
            Assert.notNull(results[0].geometry, "Address is null hence geometry is null");
            Assert.notNull(results[0].geometry.location, "Geometry is null hence location is null");

            formattedAddress = results[0].formattedAddress;

            for (AddressComponent addressComponent : results[0].addressComponents) {
                for (AddressComponentType addressComponentType : addressComponent.types) {
                    switch (addressComponentType) {
                        case COUNTRY:
                            LOG.debug("country code={}", addressComponent.shortName);
                            countryShortName = addressComponent.shortName;
                            break;
                    }
                }
            }

            lat = results[0].geometry.location.lat;
            lng = results[0].geometry.location.lng;

            placeId = results[0].placeId;
        }
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getPlaceId() {
        return placeId;
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean isNotEmpty() {
        return !empty;
    }
}
