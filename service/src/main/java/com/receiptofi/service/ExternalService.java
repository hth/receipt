package com.receiptofi.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import com.receiptofi.domain.BizStoreEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

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

    private boolean invokeGoogleMapJavaApi;
    private GeoApiContext context;

    private static final String ADDRESS_DECODE_URL = "https://maps.googleapis.com/maps/api/geocode/json?sensor=false&address=";

    @Autowired
    public ExternalService(
            @Value("${google-browser-api-key}")
            String googleBrowserApiKey,

            @Value("${invokeGoogleMapJavaApi:false}")
            boolean invokeGoogleMapJavaApi
    ) {
        this.context = new GeoApiContext().setApiKey(googleBrowserApiKey);
        this.invokeGoogleMapJavaApi = invokeGoogleMapJavaApi;
    }

    /**
     * Finds Latitude and Longitude for a given address from Google Web Service.
     *
     * @param bizStoreEntity
     * @throws Exception
     */
    public void decodeAddress(BizStoreEntity bizStoreEntity) throws IOException {
        if(invokeGoogleMapJavaApi) {
            //TODO move towards JAVA API
            populateBizStore(bizStoreEntity.getAddress(), bizStoreEntity);
        } else {
            URL url = null;
            try {
                String encodeAddress = URLEncoder.encode(bizStoreEntity.getAddress(), "UTF-8");
                url = new URL(ADDRESS_DECODE_URL + encodeAddress);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
                reader.close();

                populateBizStore(output, bizStoreEntity);
            } catch (MalformedURLException e) {
                String result = (null == url) ? bizStoreEntity.getAddress() : url.toString() + ", " + bizStoreEntity.getAddress();
                LOG.error("URL: " + result + ", " + e.getLocalizedMessage());
                throw new MalformedURLException("URL: " + result + ", " + e.getLocalizedMessage());
            } catch (IOException e) {
                String result = (null == url) ? bizStoreEntity.getAddress() : url.toString() + ", " + bizStoreEntity.getAddress();
                throw new IOException("URL: " + result + ", " + e.getLocalizedMessage());
            }
        }
    }

    /**
     * Populates address, lat, lng for address submitted.
     *
     * @param output         - JSON returned by google web service
     * @param bizStoreEntity
     */
    private void populateBizStore(StringBuilder output, BizStoreEntity bizStoreEntity) {
        //TODO add check for Status Codes - https://developers.google.com/maps/documentation/geocoding/#StatusCodes
        JsonElement root = new JsonParser().parse(output.toString());
        JsonArray results = root.getAsJsonObject().getAsJsonArray("results");
        Iterator<JsonElement> jsonElementIterator = results.iterator();
        if (jsonElementIterator.hasNext()) {
            JsonElement element = jsonElementIterator.next();

            JsonElement geometry = element.getAsJsonObject().get("geometry");
            JsonElement location = geometry.getAsJsonObject().get("location");
            double lat = location.getAsJsonObject().get("lat").getAsDouble();
            double lng = location.getAsJsonObject().get("lng").getAsDouble();
            bizStoreEntity.setLat(lat);
            bizStoreEntity.setLng(lng);

            JsonElement formattedAddressElement = element.getAsJsonObject().get("formatted_address");
            String formattedAddress = formattedAddressElement.getAsString();
            bizStoreEntity.setAddress(formattedAddress);
            bizStoreEntity.setValidatedUsingExternalAPI(true);
        }
    }

    private void populateBizStore(String address, BizStoreEntity bizStoreEntity) {
        try {
            GeocodingResult[] results =  GeocodingApi.geocode(context, address).await();
            String formattedAddress = results[0].formattedAddress;
            bizStoreEntity.setValidatedUsingExternalAPI(true);
        } catch (Exception e) {
            LOG.error("Failed to get address from google maps service, reason={}",
                    e.getLocalizedMessage(), e);
        }
    }
}
