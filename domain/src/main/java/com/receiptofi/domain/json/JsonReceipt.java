package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.types.BilledStatusEnum;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

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
    public static final DateTimeFormatter FMT = ISODateTimeFormat.dateTime();

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

    @JsonProperty ("receiptDate")
    private String receiptDate;

    @JsonProperty ("ptax")
    private String percentTax;

    @JsonProperty ("tax")
    private Double tax;

    @JsonProperty ("rid")
    private String receiptUserId;

    @JsonProperty ("expenseReport")
    private String expenseReportInFS;

    @JsonProperty ("bs")
    private String billedStatus = BilledStatusEnum.NB.getName();

    @JsonProperty ("expenseTagId")
    private String expenseTagId;

    public JsonReceipt() {
    }

    public JsonReceipt(ReceiptEntity receipt) {
        this.id = receipt.getId();
        this.total = receipt.getTotal();
        this.jsonBizName = JsonBizName.newInstance(receipt.getBizName());
        this.jsonBizStore = JsonBizStore.newInstance(receipt.getBizStore());
        this.jsonNotes = JsonComment.newInstance(receipt.getNotes());

        /**
         * The fancy line does this looping.
         *
         *  for (FileSystemEntity fileSystemEntity : receiptEntity.getFileSystemEntities()) {
         *      this.jsonFileSystems.add(JsonFileSystem.newInstance(fileSystemEntity));
         *  }
         */
        this.jsonFileSystems.addAll(receipt.getFileSystemEntities().stream().map(JsonFileSystem::newInstance).collect(Collectors.toList()));

        this.receiptDate = FMT.print(new DateTime(receipt.getReceiptDate()));
        this.tax = receipt.getTax();
        this.percentTax = receipt.getPercentTax();
        this.receiptUserId = receipt.getReceiptUserId();
        this.expenseReportInFS = receipt.getExpenseReportInFS();
        this.billedStatus = receipt.getBilledStatus().getName();
        this.expenseTagId = receipt.getExpenseTag() == null ? "" : receipt.getExpenseTag().getId();
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

    public Double getTax() {
        return tax;
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public String getExpenseReportInFS() {
        return expenseReportInFS;
    }

    public String getBilledStatus() {
        return billedStatus;
    }

    public String getExpenseTagId() {
        return expenseTagId;
    }
}
