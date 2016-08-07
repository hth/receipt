/**
 *
 */
package com.receiptofi.service;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ItemEntityOCR;
import com.receiptofi.domain.types.TaxEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the data from OCR
 *
 * @author hitender
 * @since Jan 6, 2013 9:49:59 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class ReceiptParserService {
    private static final Logger LOG = LoggerFactory.getLogger(ReceiptParserService.class);
    private static Pattern item = Pattern.compile("[-+]?[$]?[-+]?[0-9]*\\.[0-9]{2}[\\s]?[\\w{1}]?$"); // PP I $246456.99 $2.99
    private static Pattern date = Pattern.compile("[0-9]{1,2}[/|-|\\.][0-9]{1,2}[/|-|\\.][19|20]?[0-9]{2}"); // DATETIME: 12/26/2012 5:29:44 PM

    public void read(String receiptOCRTranslation, DocumentEntity documentEntity, List<ItemEntityOCR> items) {
        StringTokenizer st = new StringTokenizer(receiptOCRTranslation, "\n");
        String save = "";
        int sequence = 1;
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            Matcher itemMatcher = item.matcher(s);
            Matcher dateMatcher = date.matcher(s);
            if (itemMatcher.find()) {
                save = save + s;
                LOG.debug(save);
                items.add(processItem(save, sequence, documentEntity));
                s = "";
            } else if (dateMatcher.find()) {
                // http://stackoverflow.com/questions/600733/using-java-to-find-substring-of-a-bigger-string-using-regular-expression
                // String date = d.group(1);

                LOG.debug("Found date - ", s);
                documentEntity.setReceiptDate(s.trim());
            }
            save = s;
        }

        //At least have one item added for place holder. This will help is cloning for more items later.
        if (items.isEmpty()) {
            items.add(processItem("", 1, documentEntity));
        }
    }

    private static ItemEntityOCR processItem(String itemString, int sequence, DocumentEntity documentEntity) {
        String name = itemString.substring(0, itemString.lastIndexOf("\t") + 1);

        //Used for global name. This is hidden from user.
        //String globalName = name.replaceAll("[^A-Za-z ]", "").replaceAll("\\s+", " ");
        //LOG.debug("'" + globalName.trim() + "'");

        String price = itemString.substring(itemString.lastIndexOf("\t") + 1);
        LOG.debug("Item name : " + name + ", Item price : " + price);

        ItemEntityOCR itemOCR = ItemEntityOCR.newInstance();
        itemOCR.setName(name.trim());
        itemOCR.setPrice(price.trim());
        itemOCR.setTaxed(TaxEnum.NT);
        itemOCR.setSequence(sequence);
        itemOCR.setDocument(documentEntity);
        itemOCR.setReceiptUserId(documentEntity.getReceiptUserId());
        itemOCR.setBizName(documentEntity.getBizName());
        return itemOCR;
    }
}
