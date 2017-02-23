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
        String formattedAddress = "O.R. Tambo International Airport, O.r. Tambo International Airport, O R Tambo Airport Rd, Johannesburg, 1619, South Africa";
        GeocodingResult[] geocodingResults = externalService.getGeocodingResults(address);
        Assert.notEmpty(geocodingResults);
        DecodedAddress decodedAddress = DecodedAddress.newInstance(geocodingResults, address);

        Assert.isTrue(28.2307006 == geocodingResults[0].geometry.location.lng, "Not matching lng");
        Assert.isTrue(-26.1331917 == geocodingResults[0].geometry.location.lat, "Not matching lat");
        Assert.isTrue("ZA".equals(decodedAddress.getCountryShortName()), "Country short name is not MY");

        Assert.isTrue(formattedAddress.equals(decodedAddress.getFormattedAddress()), "Formatted address matches");
        Assert.isTrue(address.equals(decodedAddress.getAddress()), "Address matches");
    }

    @Test
    public void getPlaceDetails() {
        String address = "Lot F7, 1st Floor, Bangsar Shopping Centre, No 1, Jln Tetawi 1, Bangsar Baru 59700 K Lumpur";
        String formattedAddress = "285, Jalan Maarof, Bukit Bandaraya, 59000 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia";
        GeocodingResult[] geocodingResults = externalService.getGeocodingResults(address);
        Assert.notEmpty(geocodingResults);
        DecodedAddress decodedAddress = DecodedAddress.newInstance(geocodingResults, address);

        Assert.isTrue(101.6673598 == geocodingResults[0].geometry.location.lng, "Not matching lng");
        Assert.isTrue(3.1430501 == geocodingResults[0].geometry.location.lat, "Not matching lat");
        Assert.isTrue("MY".equals(decodedAddress.getCountryShortName()), "Country short name is not MY");

        Assert.isTrue(formattedAddress.equals(decodedAddress.getFormattedAddress()), "Formatted address matches " + decodedAddress.getFormattedAddress());
        Assert.isTrue(address.equals(decodedAddress.getAddress()), "Address matches");

        PlaceDetails placeDetails = externalService.getPlaceDetails(decodedAddress.getPlaceId());
        Assert.notEmpty(placeDetails.types, "Place Type is Empty");
    }
}
