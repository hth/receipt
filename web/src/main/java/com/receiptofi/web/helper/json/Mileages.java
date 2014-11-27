package com.receiptofi.web.helper.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.receiptofi.domain.MileageEntity;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 1/5/14 11:24 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
//TODO(hth) rename this class to Driven or something
public final class Mileages {
    private static final Logger LOG = LoggerFactory.getLogger(Mileages.class);

    @JsonProperty ("ms")
    private List<Drove> miles = new LinkedList<>();

    @JsonProperty ("mm")
    private int monthlyMileage;

    public void setMiles(List<MileageEntity> miles) {
        for (MileageEntity mileageEntity : miles) {
            this.setMileages(mileageEntity);
        }
    }

    public void setMileages(MileageEntity mileageEntity) {
        this.miles.add(Drove.newInstance(mileageEntity.getId(),
                        mileageEntity.getStart(),
                        mileageEntity.getEnd(),
                        mileageEntity.getStartDate(),
                        mileageEntity.getEndDate(),
                        null == mileageEntity.getMileageNotes() ? StringUtils.EMPTY : mileageEntity.getMileageNotes().getText(),
                        mileageEntity.getTotal(), mileageEntity.isComplete())
        );
    }

    public int getMonthlyMileage() {
        return monthlyMileage;
    }

    public void setMonthlyMileage(int monthlyMileage) {
        this.monthlyMileage = monthlyMileage;
    }

    //Converts this object to JSON representation
    public String asJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Writer writer = new StringWriter();
            mapper.writeValue(writer, this);
            return writer.toString();
        } catch (IOException e) {
            LOG.error("Json to String parsing reason={}", e.getLocalizedMessage(), e);
            return "{}";
        }
    }
}
