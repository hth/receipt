package com.receiptofi.domain.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.annotation.Mobile;

/**
 * User: hitender
 * Date: 12/30/14 1:51 AM
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
public class JsonExpenseTag {

    @JsonProperty ("tag")
    private String tag;

    private JsonExpenseTag(String tag) {
        this.tag = tag;
    }

    public static JsonExpenseTag newInstance(ExpenseTagEntity expenseTagEntity) {
        return new JsonExpenseTag(expenseTagEntity.getTagName());
    }
}
