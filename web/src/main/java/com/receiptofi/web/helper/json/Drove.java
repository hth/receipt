package com.receiptofi.web.helper.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * User: hitender
 * Date: 11/26/14 6:00 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@JsonAutoDetect (
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder (alphabetic = true)
@JsonIgnoreProperties (ignoreUnknown = true)
public class Drove {
    @JsonProperty ("i")
    private String id;

    @JsonProperty ("s")
    private int start;

    @JsonProperty ("e")
    private int end;

    @JsonProperty ("sd")
    private Date startDate;

    @JsonProperty ("ed")
    private Date endDate;

    @JsonProperty ("n")
    private String notes;

    @JsonProperty ("t")
    private int total;

    @JsonProperty ("c")
    private boolean complete;

    @JsonProperty ("na")
    private String notesAbbreviated;

    @SuppressWarnings ("unused")
    public Drove() {
    }

    private Drove(
            String id,
            int start,
            int end,
            Date startDate,
            Date endDate,
            String notes,
            int total,
            boolean complete
    ) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
        this.total = total;
        this.complete = complete;
        this.notesAbbreviated = StringUtils.abbreviate(notes, 22);
    }

    public static Drove newInstance(
            String id,
            int start,
            int end,
            Date startDate,
            Date endDate,
            String notes,
            int total,
            boolean complete
    ) {
        return new Drove(id, start, end, startDate, endDate, notes, total, complete);
    }
}
