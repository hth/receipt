package com.receiptofi.web.helper.json;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    public String getId() {
        return id;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    @JsonProperty ("na")
    public String getNotesAbbreviated() {
        return StringUtils.abbreviate(notes, 22);
    }

    public String getNotes() {
        return notes;
    }

    public int getTotal() {
        return total;
    }

    public boolean isComplete() {
        return complete;
    }
}
