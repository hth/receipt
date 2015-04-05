package com.receiptofi.domain.value;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 4/4/15 12:33 PM
 */
@Document
public class Coordinate {

    @Field ("LAT")
    private double lat;

    @Field ("LNG")
    private double lng;

    public Coordinate() {

    }

    public Coordinate(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
