package com.tholix.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.NumberFormat;

/**
 * User: hitender
 * Date: 4/16/13
 * Time: 1:55 PM
 *
 * https://developers.google.com/maps/articles/phpsqlsearch_v3
 *
 * SELECT id, ( 3959 * acos( cos( radians(37) ) * cos( radians( lat ) ) * cos( radians( lng ) - radians(-122) ) +
 * sin( radians(37) ) * sin( radians( lat ) ) ) ) AS distance FROM markers HAVING distance < 25 ORDER BY distance LIMIT 0 , 20;
 */
public class Location extends BaseEntity {


    @NotNull
    @Size(min = 0, max = 60)
    private String name;

    @Size(min = 0, max = 128)
    private String address;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private double lat;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private double lng;

    private Location() {}

    public static Location newInstance() {
        return new Location();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
