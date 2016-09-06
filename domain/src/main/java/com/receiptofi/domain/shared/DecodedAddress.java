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

    private String address;
    private String formattedAddress;
    private String postalCode;
    private String countryShortName;

    /** Format Longitude and then Latitude. */
    private double[] coordinate;
    private String placeId;
    private boolean empty = true;

    /** Based on size of the address, the bigger address is selected. */
    private DecodedAddress(GeocodingResult[] results, String address) {
        if (null != results && results.length > 0) {
            empty = false;
            Assert.notNull(results[0].geometry, "Address is null hence geometry is null");
            Assert.notNull(results[0].geometry.location, "Geometry is null hence location is null");

            this.address = address;
            formattedAddress = results[0].formattedAddress;

            for (AddressComponent addressComponent : results[0].addressComponents) {
                for (AddressComponentType addressComponentType : addressComponent.types) {
                    switch (addressComponentType) {
                        case COUNTRY:
                            LOG.debug("country code={}", addressComponent.shortName);
                            countryShortName = addressComponent.shortName;
                            break;
                        case POSTAL_CODE:
                            LOG.debug("postal code={}", addressComponent.longName);
                            postalCode = addressComponent.longName;
                            break;
                    }
                }
            }

            if (null != results[0].geometry) {
                this.coordinate = new double[]{
                        /** Mongo: Specify coordinates in this order: “longitude, latitude.” */
                        results[0].geometry.location.lng,
                        results[0].geometry.location.lat
                };
            }

            placeId = results[0].placeId;
        }
    }

    public static DecodedAddress newInstance(GeocodingResult[] results, String address) {
        return new DecodedAddress(results, address);
    }

    /**
     * Address entered, searched or as on receipt.
     * Example:
     *  Lot F7, 1st Floor, Bangsar Shopping Centre, No 1, Jln Tetawi 1, Bangsar Baru 59700 K Lumpur
     *  OR Tambo Airport Rd, Level 2, Domtex Building, OR Tambo International Airport, Johannesburg, 1627, South Africa
     *
     * External source could not locate these address. So to preserve whats entered (as un-altered) we save the address.
     */
    public String getAddress() {
        return address;
    }

    /** Address sourced from third party. External source. */
    public String getFormattedAddress() {
        return formattedAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public double[] getCoordinate() {
        return coordinate;
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
