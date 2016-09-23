package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.annotation.Mobile;

/**
 * User: hitender
 * Date: 9/11/14 12:06 AM
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
public class JsonReceiptItem {

    @JsonProperty ("id")
    private String id;

    @JsonProperty ("seq")
    private String seq;

    @JsonProperty ("name")
    private String name;

    @JsonProperty ("quant")
    private String quantity;

    @JsonProperty ("price")
    private String price;

    @JsonProperty ("tax")
    private String tax;

    @JsonProperty ("cs")
    private String countryShortName;

    @JsonProperty ("receiptId")
    private String receiptId;

    @JsonProperty ("expenseTagId")
    private String expenseTagId;

    private JsonReceiptItem(ItemEntity item) {
        this.id = item.getId();
        this.seq = String.valueOf(item.getSequence());
        this.name = item.getName();
        this.quantity = String.valueOf(item.getQuantity());
        this.price = String.valueOf(item.getPrice());
        this.tax = String.valueOf(item.getTax());
        this.countryShortName = item.getReceipt().getCountryShortName();
        this.receiptId = item.getReceipt().getId();
        this.expenseTagId = item.getExpenseTag() == null ? "" : item.getExpenseTag().getId();
    }

    public static JsonReceiptItem newInstance(ItemEntity item) {
        return new JsonReceiptItem(item);
    }
}
