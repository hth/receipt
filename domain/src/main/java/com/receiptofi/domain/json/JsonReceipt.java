package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.annotation.Mobile;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Collection;
import java.util.LinkedList;

/**
 * User: hitender
 * Date: 8/24/14 9:35 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable",
        "unused"
})
@JsonAutoDetect (
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonPropertyOrder (alphabetic = true)
@JsonIgnoreProperties (ignoreUnknown = true)
//@JsonInclude (JsonInclude.Include.NON_NULL)
@Mobile
public class JsonReceipt {
    private static final DateTimeFormatter FMT = ISODateTimeFormat.dateTime();

    @JsonProperty ("id")
    private String id;

    @JsonProperty ("total")
    private Double total;

    @JsonProperty ("bizName")
    private JsonBizName jsonBizName;

    @JsonProperty ("bizStore")
    private JsonBizStore jsonBizStore;

    @JsonProperty ("notes")
    private JsonComment jsonNotes;

    @JsonProperty ("files")
    private Collection<JsonFileSystem> jsonFileSystems = new LinkedList<>();

    @JsonProperty ("date")
    private String receiptDate;

    @JsonProperty ("ptax")
    private String percentTax;

    @JsonProperty ("rid")
    private String receiptUserId;

    @JsonProperty ("expenseReport")
    private String expenseReportInFS;

    public JsonReceipt() {
    }

    public JsonReceipt(ReceiptEntity receiptEntity) {
        this.id = receiptEntity.getId();
        this.total = receiptEntity.getTotal();
        this.jsonBizName = JsonBizName.newInstance(receiptEntity.getBizName());
        this.jsonBizStore = JsonBizStore.newInstance(receiptEntity.getBizStore());
        this.jsonNotes = JsonComment.newInstance(receiptEntity.getNotes());

        for (FileSystemEntity fileSystemEntity : receiptEntity.getFileSystemEntities()) {
            this.jsonFileSystems.add(JsonFileSystem.newInstance(fileSystemEntity));
        }

        this.receiptDate = FMT.print(new DateTime(receiptEntity.getReceiptDate()));
        this.percentTax = receiptEntity.getPercentTax();
        this.receiptUserId = receiptEntity.getReceiptUserId();
        this.expenseReportInFS = receiptEntity.getExpenseReportInFS();
    }

    public String getId() {
        return id;
    }

    public Double getTotal() {
        return total;
    }

    public JsonBizName getJsonBizName() {
        return jsonBizName;
    }

    public JsonBizStore getJsonBizStore() {
        return jsonBizStore;
    }

    public JsonComment getJsonNotes() {
        return jsonNotes;
    }

    public Collection<JsonFileSystem> getJsonFileSystems() {
        return jsonFileSystems;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public String getPercentTax() {
        return percentTax;
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public String getExpenseReportInFS() {
        return expenseReportInFS;
    }
}
