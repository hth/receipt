package com.receiptofi.service;

import com.receiptofi.ITest;

import org.junit.Test;

/**
 * User: hitender
 * Date: 9/6/16 12:12 AM
 */
public class ExternalServiceITest extends ITest {

    @Test
    public void getGeocodingResultNotValidAddress() {
//        String address = "OR Tambo Airport Rd, Level 2, Domtex Building, OR Tambo International Airport, Johannesburg, 1627, South Africa";
//        String formattedAddress = "O.R. Tambo International Airport, O R Tambo Airport Rd, O.R. Tambo, Johannesburg, 1627, South Africa";
//        GeocodingResult[] geocodingResults = externalService.getGeocodingResults(address);
//        Assert.notEmpty(geocodingResults, "Geocoding Result cannot be empty");
//        DecodedAddress decodedAddress = DecodedAddress.newInstance(geocodingResults, address);
//
//        Assert.isTrue(28.2411459 == geocodingResults[0].geometry.location.lng, "Not matching lng");
//        Assert.isTrue(-26.1366728 == geocodingResults[0].geometry.location.lat, "Not matching lat");
//        Assert.isTrue("ZA".equals(decodedAddress.getCountryShortName()), "Country short name is not MY");
//
//        Assert.isTrue(formattedAddress.equals(decodedAddress.getFormattedAddress()), "Formatted address matches");
//        Assert.isTrue(address.equals(decodedAddress.getAddress()), "Address matches");
    }

    @Test
    public void getPlaceDetails() {
//        String address = "Lot F7, 1st Floor, Bangsar Shopping Centre, No 1, Jln Tetawi 1, Bangsar Baru 59700 K Lumpur";
//        String formattedAddress = "2, Jalan Telawi 1, Bangsar Baru, 59100 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia";
//        GeocodingResult[] geocodingResults = externalService.getGeocodingResults(address);
//        Assert.notEmpty(geocodingResults, "Geocoding Result cannot be empty");
//        DecodedAddress decodedAddress = DecodedAddress.newInstance(geocodingResults, address);
//
//        Assert.isTrue(101.6708411 == geocodingResults[0].geometry.location.lng, "Not matching lng");
//        Assert.isTrue(3.129752 == geocodingResults[0].geometry.location.lat, "Not matching lat");
//        Assert.isTrue("MY".equals(decodedAddress.getCountryShortName()), "Country short name is not MY");
//
//        Assert.isTrue(formattedAddress.equals(decodedAddress.getFormattedAddress()), "Formatted address matches " + decodedAddress.getFormattedAddress());
//        Assert.isTrue(address.equals(decodedAddress.getAddress()), "Address matches");
//
//        PlaceDetails placeDetails = externalService.getPlaceDetails(decodedAddress.getPlaceId());
//        Assert.notEmpty(placeDetails.types, "Place Type is Empty");
    }
}
