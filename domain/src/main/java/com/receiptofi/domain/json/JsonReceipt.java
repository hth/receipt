package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.types.BilledStatusEnum;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.TimeZone;
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
    public static final String ISO8601_FMT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

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

    @JsonProperty ("referReceiptId")
    private String referReceiptId;

    @JsonProperty ("splitCount")
    private int splitCount;

    @JsonProperty ("splitTotal")
    private Double splitTotal;

    @JsonProperty ("splitTax")
    private Double splitTax;

    @JsonProperty ("cs")
    private String countryShortName;

    @JsonProperty ("a")
    private boolean active;

    @JsonProperty ("d")
    private boolean deleted;

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

        this.receiptDate = DateFormatUtils.format(receipt.getReceiptDate(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.tax = receipt.getTax();
        this.percentTax = receipt.getPercentTax();
        this.receiptUserId = receipt.getReceiptUserId();
        this.expenseReportInFS = receipt.getExpenseReportInFS();
        this.billedStatus = receipt.getBilledStatus().getName();
        this.expenseTagId = receipt.getExpenseTag() == null ? "" : receipt.getExpenseTag().getId();
        this.referReceiptId = receipt.getReferReceiptId() == null ? "" : receipt.getReferReceiptId();
        this.splitCount = receipt.getSplitCount();
        this.splitTotal = receipt.getSplitTotal();
        this.splitTax = receipt.getSplitTax();
        this.countryShortName = receipt.getCountryShortName();

        this.active = receipt.isActive();
        this.deleted = receipt.isDeleted();
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

    public String getReferReceiptId() {
        return referReceiptId;
    }

    public int getSplitCount() {
        return splitCount;
    }

    public Double getSplitTotal() {
        return splitTotal;
    }

    public Double getSplitTax() {
        return splitTax;
    }

    public String getCountryShortName() {
        return countryShortName;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
