package com.receiptofi.service;

import com.google.maps.model.GeocodingResult;
import com.google.maps.model.PlaceDetails;

import com.receiptofi.ITest;
import com.receiptofi.domain.shared.DecodedAddress;

import org.springframework.util.Assert;

import org.junit.Test;

/**
 * User: hitender
 * Date: 9/6/16 12:12 AM
 */
public class ExternalServiceITest extends ITest {

    @Test
    public void getGeocodingResultNotValidAddress() {
        String address = "OR Tambo Airport Rd, Level 2, Domtex Building, OR Tambo International Airport, Johannesburg, 1627, South Africa";
        GeocodingResult[] geocodingResults = externalService.getGeocodingResults(address);
        Assert.notEmpty(geocodingResults);
        DecodedAddress decodedAddress = DecodedAddress.newInstance(geocodingResults, address);

        Assert.isTrue(28.2411459 == geocodingResults[0].geometry.location.lng, "Not matching lng");
        Assert.isTrue(-26.1366728 == geocodingResults[0].geometry.location.lat, "Not matching lat");
        Assert.isTrue("ZA".equals(decodedAddress.getCountryShortName()), "Country short name is not MY");

        String formattedAddress = decodedAddress.getFormattedAddress();
        Assert.isTrue(address.equals(formattedAddress), "Formatted address does not match");
    }

    @Test
    public void getPlaceDetails() {
        String address = "Lot F7, 1st Floor, Bangsar Shopping Centre, No 1, Jln Tetawi 1, Bangsar Baru 59700 K Lumpur";
        GeocodingResult[] geocodingResults = externalService.getGeocodingResults(address);
        Assert.notEmpty(geocodingResults);
        DecodedAddress decodedAddress = DecodedAddress.newInstance(geocodingResults, address);

        Assert.isTrue(101.6711938 == geocodingResults[0].geometry.location.lng, "Not matching lng");
        Assert.isTrue(3.1328404 == geocodingResults[0].geometry.location.lat, "Not matching lat");
        Assert.isTrue("MY".equals(decodedAddress.getCountryShortName()), "Country short name is not MY");

        String formattedAddress = decodedAddress.getFormattedAddress();
        Assert.isTrue(!address.equals(formattedAddress), "Formatted address does not match");

        PlaceDetails placeDetails = externalService.getPlaceDetails(decodedAddress.getPlaceId());
        Assert.notEmpty(placeDetails.types, "Place Type is Empty");
    }
}
