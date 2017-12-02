/**
 *
 */
package com.receiptofi.web.form;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ItemEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hitender
 * @since Jan 4, 2013 4:41:01 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class UserLoginForm {

    private String emailId;
    private String password;

    private DocumentEntity receiptDocument;
    private List<ItemEntity> items;

    private UserLoginForm() {
        receiptDocument = new DocumentEntity();

        items = new ArrayList<>();
        ItemEntity itemEntity = new ItemEntity();
        items.add(itemEntity);
    }

    public static UserLoginForm newInstance() {
        return new UserLoginForm();
    }

    public String getEmailId() {
        return StringUtils.lowerCase(emailId);
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DocumentEntity getReceiptDocument() {
        return receiptDocument;
    }

    public UserLoginForm setReceiptDocument(DocumentEntity receiptDocument) {
        this.receiptDocument = receiptDocument;
        return this;
    }

    public List<ItemEntity> getItems() {
        return items;
    }

    public UserLoginForm setItems(List<ItemEntity> items) {
        this.items = items;
        return this;
    }
}
