package com.receiptofi.web.controller.ajax;

import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.MileageService;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.utils.ParseJsonStringToMap;
import com.receiptofi.utils.ScrubbedInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

/**
 * Update for all Ajax Calls.
 * User: hitender
 * Date: 7/22/13
 * Time: 8:57 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@RestController
@RequestMapping (value = "/ws/nc")
public class NotesAndCommentsWebService {
    private static final Logger LOG = LoggerFactory.getLogger(NotesAndCommentsWebService.class);

    private ReceiptService receiptService;
    private MileageService mileageService;

    @Autowired
    public NotesAndCommentsWebService(ReceiptService receiptService, MileageService mileageService) {
        this.receiptService = receiptService;
        this.mileageService = mileageService;
    }

    @RequestMapping (
            value = "/rn",
            method = RequestMethod.POST,
            headers = "Accept=application/json")
    public boolean saveReceiptNotes(@RequestBody String body) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Receipt notes updated by userProfileId={}", receiptUser.getRid());
        Map<String, ScrubbedInput> map = ParseJsonStringToMap.jsonStringToMap(body);
        return receiptService.updateReceiptNotes(
                map.get("notes").getText(),
                map.get("receiptId").getText(),
                receiptUser.getRid());
    }

    @RequestMapping (
            value = "/mn",
            method = RequestMethod.POST,
            headers = "Accept=application/json")
    public boolean saveMileageNotes(@RequestBody String body) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Note updated by userProfileId={}", receiptUser.getRid());
        Map<String, ScrubbedInput> map = ParseJsonStringToMap.jsonStringToMap(body);
        return mileageService.updateMileageNotes(
                map.get("notes").getText(),
                map.get("mileageId").getText(),
                receiptUser.getRid());
    }

    @RequestMapping (
            value = "/rc",
            method = RequestMethod.POST,
            headers = "Accept=application/json")
    public boolean saveReceiptRecheckComment(@RequestBody String body) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Receipt recheck comment updated by userProfileId={}", receiptUser.getRid());
        Map<String, ScrubbedInput> map = ParseJsonStringToMap.jsonStringToMap(body);
        return receiptService.updateReceiptComment(
                map.get("notes").getText(),
                map.get("receiptId").getText(),
                receiptUser.getRid());
    }

    @RequestMapping (
            value = "/dc",
            method = RequestMethod.POST,
            headers = "Accept=application/json")
    public boolean saveDocumentRecheckComment(@RequestBody String body) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Document recheck comment updated by userProfileId={}", receiptUser.getRid());
        Map<String, ScrubbedInput> map = ParseJsonStringToMap.jsonStringToMap(body);
        return receiptService.updateDocumentComment(
                map.get("notes").getText(),
                map.get("documentId").getText(),
                receiptUser.getRid());
    }
}
