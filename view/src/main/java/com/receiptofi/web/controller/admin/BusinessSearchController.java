package com.receiptofi.web.controller.admin;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.BizService;
import com.receiptofi.service.ExternalService;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.utils.CommonUtil;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.form.admin.BizForm;
import com.receiptofi.web.validator.admin.BizSearchValidator;
import com.receiptofi.web.validator.admin.BizValidator;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Set;

/**
 * User: hitender
 * Date: 7/30/13
 * Time: 4:22 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/admin")
public class BusinessSearchController {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessSearchController.class);

    @Value ("${nextPage:/admin/businessSearch}")
    private String nextPage;

    @Value ("${editPage:/admin/businessEdit}")
    private String editPage;

    private final ExternalService externalService;
    private final BizService bizService;
    private final ReceiptService receiptService;
    private final BizValidator bizValidator;
    private final BizSearchValidator bizSearchValidator;

    @Autowired
    public BusinessSearchController(
            ReceiptService receiptService,
            BizService bizService,
            ExternalService externalService,
            BizValidator bizValidator,
            BizSearchValidator bizSearchValidator
    ) {
        this.receiptService = receiptService;
        this.bizService = bizService;
        this.externalService = externalService;
        this.bizValidator = bizValidator;
        this.bizSearchValidator = bizSearchValidator;
    }

    @RequestMapping (value = "/businessSearch", method = RequestMethod.GET)
    public String loadSearchForm(
            @ModelAttribute ("bizForm")
            BizForm bizForm,

            Model model
    ) {
        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.bizForm", model.asMap().get("result"));
        }

        return nextPage;
    }

    @RequestMapping (value = "/businessSearch/edit", method = RequestMethod.GET)
    public String editStore(
            @RequestParam ("bizNameId")
            ScrubbedInput bizNameId,

            @RequestParam ("bizStoreId")
            ScrubbedInput bizStoreId,

            @ModelAttribute ("bizForm")
            BizForm bizForm
    ) {
        BizNameEntity bizName = bizService.getByBizNameId(bizNameId.getText());
        Assert.notNull(bizName, "BizName null for nameId=" + bizNameId);
        bizForm.setBizNameEntity(bizName);

        if (StringUtils.isNotEmpty(bizStoreId.getText())) {
            BizStoreEntity bizStore = bizService.getByStoreId(bizStoreId.getText());
            bizForm.setBizStore(bizStore);
        }

        return editPage;
    }

    /**
     * Reset the form.
     *
     * @param redirectAttrs
     * @return
     */
    @RequestMapping (value = "/businessSearch", method = RequestMethod.POST, params = "reset")
    public String reset(
            @ModelAttribute ("bizForm")
            BizForm bizForm,

            RedirectAttributes redirectAttrs
    ) {
        redirectAttrs.addFlashAttribute("bizForm", bizForm);
        //Re-direct to prevent resubmit
        return "redirect:" + nextPage + ".htm";
    }

    /**
     * Edit Biz Name and or Biz Address, Phone.
     * No validation is performed.
     *
     * @param bizForm
     * @return
     */
    @RequestMapping (value = "/businessSearch", method = RequestMethod.POST, params = "edit")
    public String editBiz(
            @ModelAttribute ("bizForm")
            BizForm bizForm,

            RedirectAttributes redirectAttrs
    ) {
        redirectAttrs.addFlashAttribute("bizForm", bizForm);

        BizStoreEntity bizStore;
        if (StringUtils.isNotEmpty(bizForm.getBizStoreId())) {
            //TODO verify this getBizStoreId()
            bizStore = bizService.getByStoreId(bizForm.getBizStoreId());
            bizStore.setAddress(bizForm.getAddress());
            bizStore.setPhone(bizForm.getPhone());
            try {
                externalService.decodeAddress(bizStore);
                bizService.saveStore(bizStore);

                /* Update all the CS for receipts where store address has updated. */
                receiptService.updateReceiptCSWhenStoreUpdated(bizStore.getCountryShortName(), bizStore.getId());
            } catch (Exception e) {
                LOG.error("Failed to edit address/phone: {} {} reason={}", bizForm.getAddress(), bizForm.getPhone(), e.getLocalizedMessage(), e);
                bizForm.setErrorMessage("Failed to edit address/phone: " + bizForm.getAddress() + ", " + bizForm.getPhone() + ", :" + e.getLocalizedMessage());
                //Re-direct to prevent resubmit
                return "redirect:" + "business/edit" + ".htm" + "?bizNameId=" + bizForm.getBizNameId() + "&bizStoreId=" + bizForm.getBizStoreId();
            }
        }

        BizNameEntity bizName;
        if (StringUtils.isNotEmpty(bizForm.getBizNameId())) {
            bizName = bizService.getByBizNameId(bizForm.getBizNameId());
            bizName.setBusinessName(bizForm.getBusinessName());
            try {
                bizService.saveName(bizName);
                LOG.info("Business '" + bizName.getBusinessName() + "' updated successfully");
            } catch (Exception e) {
                LOG.error("Failed to edit name: " + bizForm.getBusinessName() + ", " + e.getLocalizedMessage());
                bizForm.setErrorMessage("Failed to edit name: " + bizForm.getBusinessName() + ", " + e.getLocalizedMessage());
                //Re-direct to prevent resubmit
                return "redirect:" + "business/edit" + ".htm" + "?bizNameId=" + bizForm.getBizNameId() + "&bizStoreId=" + bizForm.getBizStoreId();
            }
        }

        Set<BizStoreEntity> bizStores = searchBizStoreEntities(bizForm);
        bizForm.setLast10BizStore(bizStores);
        //Re-direct to prevent resubmit
        return "redirect:" + nextPage + ".htm";
    }

    /**
     * Delete Biz Name and or Biz Address, Phone. when there are no active or inactive receipts referring to any of the
     * stores.
     * No validation is performed.
     *
     * @param bizForm
     * @return
     */
    @RequestMapping (value = "/businessSearch", method = RequestMethod.POST, params = "delete_store")
    public String deleteBizStore(
            @ModelAttribute ("bizForm")
            BizForm bizForm,

            RedirectAttributes redirectAttrs
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        redirectAttrs.addFlashAttribute("bizForm", bizForm);

        BizStoreEntity bizStore;
        if (StringUtils.isNotEmpty(bizForm.getBizStoreId())) {
            //TODO verify this getBizStoreId()
            bizStore = bizService.getByStoreId(bizForm.getBizStoreId());
            BizNameEntity bizName = bizStore.getBizName();

            Set<BizStoreEntity> bizStoreEntities = new HashSet<>();
            bizStoreEntities.add(bizStore);
            bizForm.setReceiptCount(receiptService.countReceiptForBizStore(bizStoreEntities));
            if (bizForm.getReceiptCount().get(bizStore.getId()) == 0) {
                bizService.deleteBizStore(bizStore);
                bizForm.setSuccessMessage("Deleted store successfully");
                LOG.info("Deleted stored: " + bizStore.getAddress() + ", id: " + bizStore.getId() + ", by user={}", receiptUser.getRid());

                //To make sure no orphan biz name are lingering around
                if (receiptService.countAllReceiptForABizName(bizName) == 0) {
                    bizService.deleteBizName(bizName);
                    bizForm.setSuccessMessage("Deleted biz name successfully");
                    LOG.info("Deleted biz name: " + bizName.getBusinessName() + ", id: " + bizName.getId() + ", by user={}", receiptUser.getRid());
                }
            } else {
                bizForm.setErrorMessage("Could not delete the store as its currently being referred by a receipt");
            }
        }

        Set<BizStoreEntity> bizStores = searchBizStoreEntities(bizForm);
        bizForm.setLast10BizStore(bizStores);
        //Re-direct to prevent resubmit
        return "redirect:" + nextPage + ".htm";
    }

    /**
     * @param bizForm
     * @param result
     * @return
     */
    @RequestMapping (value = "/businessSearch", method = RequestMethod.POST, params = "add")
    public String addBiz(
            @ModelAttribute ("bizForm")
            BizForm bizForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        redirectAttrs.addFlashAttribute("bizForm", bizForm);

        bizValidator.validate(bizForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            /** Re-direct to prevent resubmit. */
            return "redirect:" + nextPage + ".htm";
        } else {
            BizStoreEntity bizStore = BizStoreEntity.newInstance();
            bizStore.setAddress(bizForm.getAddress());
            bizStore.setPhone(bizForm.getPhone());
            try {
                externalService.decodeAddress(bizStore);
            } catch (Exception e) {
                LOG.error("Failed to edit address/phone={} {} reason={}",
                        bizForm.getAddress(),
                        bizForm.getPhone(),
                        e.getLocalizedMessage(),
                        e);

                bizForm.setErrorMessage("Failed to edit address/phone: " +
                        bizForm.getAddress() +
                        ", " +
                        bizForm.getPhone() +
                        ", :" +
                        e.getLocalizedMessage());
                /** Re-direct to prevent resubmit. */
                return "redirect:" + nextPage + ".htm";
            }

            ReceiptEntity receipt = ReceiptEntity.newInstance();
            receipt.setBizStore(bizStore);

            BizNameEntity bizName = BizNameEntity.newInstance();
            bizName.setBusinessName(bizForm.getBusinessName());
            receipt.setBizName(bizName);
            try {
                bizService.saveNewBusinessAndOrStore(receipt);
                bizForm.setSuccessMessage("Business '" + receipt.getBizName().getBusinessName() + "' added successfully");
            } catch (Exception e) {
                LOG.error("Failed to edit name={} reason={}", bizForm.getBusinessName(), e.getLocalizedMessage(), e);
                bizForm.setErrorMessage("Failed to edit name: " + bizForm.getBusinessName() + ", " + e.getLocalizedMessage());
                /** Re-direct to prevent resubmit . */
                return "redirect:" + nextPage + ".htm";
            }

            if (receipt.getBizName().getId().equals(receipt.getBizStore().getBizName().getId())) {
                bizForm.setAddedBizStore(receipt.getBizStore());
                bizForm.setLast10BizStore(bizService.getAllStoresForSameBusinessNameId(receipt));
                redirectAttrs.addFlashAttribute("bizForm", bizForm);
            } else {
                bizForm.setErrorMessage(
                        "Address uniquely identified with another Biz Name: " +
                                receipt.getBizStore().getBizName().getBusinessName());
            }
            /** Re-direct to prevent resubmit. */
            return "redirect:" + nextPage + ".htm";
        }
    }

    /**
     * Search for Biz with either Name, Address, Phone or all or none.
     *
     * @param bizForm
     * @param result
     * @return
     */
    @RequestMapping (value = "/businessSearch", method = RequestMethod.POST, params = "search")
    public String searchBiz(
            @ModelAttribute ("bizForm")
            BizForm bizForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        bizSearchValidator.validate(bizForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);
            //Re-direct to prevent resubmit
            return "redirect:" + nextPage + ".htm";
        }

        Set<BizStoreEntity> bizStores = searchBizStoreEntities(bizForm);
        bizForm.setLast10BizStore(bizStores);
        redirectAttrs.addFlashAttribute("bizForm", bizForm);
        //Re-direct to prevent resubmit
        return "redirect:" + nextPage + ".htm";
    }

    /**
     * Search for matching biz criteria.
     *
     * @param bizForm
     * @return
     */
    private Set<BizStoreEntity> searchBizStoreEntities(BizForm bizForm) {
        String businessName = StringUtils.trim(bizForm.getBusinessName());
        String address = StringUtils.trim(bizForm.getAddress());
        String phone = StringUtils.trim(CommonUtil.phoneCleanup(bizForm.getPhone()));
        Set<BizStoreEntity> bizStores = bizService.bizSearch(businessName, address, phone);
        bizForm.setSuccessMessage("Found '" + bizStores.size() + "' matching business(es).");

        bizForm.setReceiptCount(receiptService.countReceiptForBizStore(bizStores));
        return bizStores;
    }
}
