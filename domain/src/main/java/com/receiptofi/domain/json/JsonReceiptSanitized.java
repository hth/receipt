package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.util.LocaleUtil;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.TimeZone;

/**
 * User: hitender
 * Date: 2/12/16 12:32 AM
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
public class JsonReceiptSanitized {
    public static final String ISO8601_FMT = "yyyy-MM-dd'T'HH:mm:ss.sssZZZ";

    @JsonProperty ("code")
    private String currencyCode;

    @JsonProperty ("total")
    private Double total;

    @JsonProperty ("bizName")
    private JsonBizName jsonBizName;

    @JsonProperty ("bizStore")
    private JsonBizStore jsonBizStore;

    @JsonProperty ("receiptDate")
    private String receiptDate;

    @JsonProperty ("ptax")
    private String percentTax;

    @JsonProperty ("tax")
    private Double tax;

    public JsonReceiptSanitized() {
    }

    public JsonReceiptSanitized(ReceiptEntity receipt) {
        this.currencyCode = LocaleUtil.getCurrencySymbol(receipt.getBizStore().getCountryShortName());
        this.total = receipt.getTotal();
        this.jsonBizName = JsonBizName.newInstance(receipt.getBizName());
        this.jsonBizStore = JsonBizStore.newInstance(receipt.getBizStore());
        this.receiptDate = DateFormatUtils.format(receipt.getReceiptDate(), ISO8601_FMT, TimeZone.getTimeZone("UTC"));
        this.tax = receipt.getTax();
        this.percentTax = receipt.getPercentTax();
    }
}
