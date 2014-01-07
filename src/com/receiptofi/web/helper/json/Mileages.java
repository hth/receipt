package com.receiptofi.web.helper.json;

import com.receiptofi.domain.MileageEntity;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 1/5/14 11:24 PM
 */
public final class Mileages {

    @JsonProperty("ms")
    private List<Mileage> mileages = new LinkedList<>();

    public List<Mileage> getMileages() {
        return mileages;
    }

    public void setMileages(List<MileageEntity> mileages) {
        for(MileageEntity mileageEntity : mileages) {
            this.mileages.add(Mileage.newInstance(mileageEntity.getId(), mileageEntity.getTotal(), mileageEntity.isComplete()));
        }
    }

    //Converts this object to JSON representation
    public String asJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Writer writer = new StringWriter();
            mapper.writeValue(writer, this);
            return writer.toString();
        } catch (IOException e) {
            return "{}";
        }
    }
}

class Mileage {

    @JsonProperty("i")
    private String id;

    @JsonProperty("t")
    private int total;

    @JsonProperty("c")
    private boolean complete;

    @SuppressWarnings("unused")
    public Mileage() {}

    private Mileage(String id, int total, boolean complete) {
        this.id = id;
        this.total = total;
        this.complete = complete;
    }

    public static Mileage newInstance(String id, int total, boolean valid) {
        return new Mileage(id, total, valid);
    }

    public String getId() {
        return id;
    }

    public int getTotal() {
        return total;
    }

    public boolean isComplete() {
        return complete;
    }
}



