package com.tholix.web.controller.admin;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.joda.time.DateTime;

import com.tholix.domain.BizNameEntity;
import com.tholix.domain.BizStoreEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserSession;
import com.tholix.domain.types.UserLevelEnum;
import com.tholix.service.BizService;
import com.tholix.service.ExternalService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.LoginController;
import com.tholix.web.form.BizForm;
import com.tholix.web.validator.BizSearchValidator;
import com.tholix.web.validator.BizValidator;

/**
 * User: hitender
 * Date: 7/30/13
 * Time: 4:22 PM
 */
@Controller
@RequestMapping(value = "/admin")
@SessionAttributes({"userSession"})
public class BusinessController {
    private static final Logger log = Logger.getLogger(BusinessController.class);
    private static final String NEXT_PAGE = "/admin/business";
    private static final String EDIT_PAGE = "/admin/businessEdit";

    @Autowired private ExternalService externalService;
    @Autowired private BizService bizService;

    @Autowired private BizValidator bizValidator;
    @Autowired private BizSearchValidator bizSearchValidator;

    @RequestMapping(value = "/business", method = RequestMethod.GET)
    public ModelAndView loadSearchForm(@ModelAttribute("userSession") UserSession userSession, @ModelAttribute("bizForm") BizForm bizForm,
                                       final Model model) {

        //Gymnastic to show BindingResult errors if any
        if (model.asMap().containsKey("result")) {
            model.addAttribute("org.springframework.validation.BindingResult.bizForm", model.asMap().get("result"));
        }

        if(userSession.getLevel() == UserLevelEnum.ADMIN) {
            ModelAndView modelAndView = new ModelAndView(NEXT_PAGE);
            return modelAndView;
        }

        //Re-direct user to his home page because user tried accessing Un-Authorized page
        log.warn("Re-direct user to his home page because user tried accessing Un-Authorized page: User: " + userSession.getUserProfileId());
        return new ModelAndView(LoginController.landingHomePage(userSession.getLevel()));
    }

    @RequestMapping(value = "/business/edit", method = RequestMethod.GET)
    public ModelAndView editStore(@ModelAttribute("userSession") UserSession userSession,
                                  @RequestParam("nameId") String nameId,
                                  @RequestParam("storeId") String storeId,
                                  @ModelAttribute("bizForm") BizForm bizForm) {

        if(userSession.getLevel() == UserLevelEnum.ADMIN) {
            ModelAndView modelAndView = new ModelAndView(EDIT_PAGE);
            BizNameEntity bizNameEntity = bizService.findName(nameId);
            bizForm.setBizName(bizNameEntity);

            if(StringUtils.isNotEmpty(storeId)) {
                BizStoreEntity bizStoreEntity = bizService.findStore(storeId);
                bizForm.setBizStore(bizStoreEntity);
            }

            return modelAndView;
        }

        //Re-direct user to his home page because user tried accessing Un-Authorized page
        log.warn("Re-direct user to his home page because user tried accessing Un-Authorized page: User: " + userSession.getUserProfileId());
        return new ModelAndView(LoginController.landingHomePage(userSession.getLevel()));
    }

    /**
     * Edit Biz Name and or Biz Address, Phone.
     *
     * No validation is performed.
     *
     * @param bizForm
     * @param userSession - Required when user try to refresh page after log out
     * @return
     */
    @RequestMapping(value = "/business", method = RequestMethod.POST, params = "edit")
    public String editBiz(@ModelAttribute("userSession") UserSession userSession, @ModelAttribute("bizForm") BizForm bizForm,
                          final RedirectAttributes redirectAttrs) {

        DateTime time = DateUtil.now();
        redirectAttrs.addFlashAttribute("bizForm", bizForm);

        BizStoreEntity bizStoreEntity;
        if(StringUtils.isNotEmpty(bizForm.getAddressId())) {
            bizStoreEntity = bizService.findStore(bizForm.getAddressId());
            bizStoreEntity.setAddress(bizForm.getAddress());
            bizStoreEntity.setPhone(bizForm.getPhone());
            try {
                externalService.decodeAddress(bizStoreEntity);
                bizService.saveStore(bizStoreEntity);
            } catch (Exception e) {
                log.error("Failed to edit address/phone: " + bizForm.getAddress() + ", " + bizForm.getPhone() + ", :" + e.getLocalizedMessage());
                bizForm.setBizError("Failed to edit address/phone: " + bizForm.getAddress() + ", " + bizForm.getPhone() + ", :" + e.getLocalizedMessage());

                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
                //Re-direct to prevent resubmit
                return "redirect:" + "business/edit" + ".htm" + "?nameId=" + bizForm.getNameId() + "&storeId=" + bizForm.getAddressId();
            }
        }

        BizNameEntity bizNameEntity;
        if(StringUtils.isNotEmpty(bizForm.getNameId())) {
            bizNameEntity = bizService.findName(bizForm.getNameId());
            bizNameEntity.setName(bizForm.getName());
            try {
                bizService.saveName(bizNameEntity);
                log.info("Business '" + bizNameEntity.getName() + "' updated successfully");
            } catch(Exception e) {
                log.error("Failed to edit name: " + bizForm.getName() + ", " + e.getLocalizedMessage());
                bizForm.setBizError("Failed to edit name: " + bizForm.getName() + ", " + e.getLocalizedMessage());

                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
                //Re-direct to prevent resubmit
                return "redirect:" + "business/edit" + ".htm" + "?nameId=" + bizForm.getNameId() + "&storeId=" + bizForm.getAddressId();
            }
        }

        Set<BizStoreEntity> bizStoreEntities = searchBizStoreEntities(bizForm);
        redirectAttrs.addFlashAttribute("last10BizStore", bizStoreEntities);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), true);
        //Re-direct to prevent resubmit
        return "redirect:" + NEXT_PAGE + ".htm";
    }

    /**
     *
     * @param bizForm
     * @param result
     * @param userSession - Required when user try to refresh page after log out
     * @return
     */
    @RequestMapping(value = "/business", method = RequestMethod.POST, params = "add")
    public String addBiz(@ModelAttribute("userSession") UserSession userSession, @ModelAttribute("bizForm") BizForm bizForm,
                         BindingResult result,
                         final RedirectAttributes redirectAttrs) {

        DateTime time = DateUtil.now();
        redirectAttrs.addFlashAttribute("bizForm", bizForm);

        bizValidator.validate(bizForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
            //Re-direct to prevent resubmit
            return "redirect:" + NEXT_PAGE + ".htm";
        } else {
            BizStoreEntity bizStoreEntity = BizStoreEntity.newInstance();
            bizStoreEntity.setAddress(bizForm.getAddress());
            bizStoreEntity.setPhone(bizForm.getPhone());
            try {
                externalService.decodeAddress(bizStoreEntity);
            } catch (Exception e) {
                log.error("Failed to edit address/phone: " + bizForm.getAddress() + ", " + bizForm.getPhone() + ", :" + e.getLocalizedMessage());
                bizForm.setBizError("Failed to edit address/phone: " + bizForm.getAddress() + ", " + bizForm.getPhone() + ", :" + e.getLocalizedMessage());

                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
                //Re-direct to prevent resubmit
                return "redirect:" + NEXT_PAGE + ".htm";
            }

            ReceiptEntity receiptEntity = ReceiptEntity.newInstance();
            receiptEntity.setBizStore(bizStoreEntity);

            BizNameEntity bizNameEntity = BizNameEntity.newInstance();
            bizNameEntity.setName(bizForm.getName());
            receiptEntity.setBizName(bizNameEntity);
            try {
                bizService.saveNewBusinessAndOrStore(receiptEntity);
                bizForm.setBizSuccess("Business '" + receiptEntity.getBizName().getName() + "' added successfully");
            } catch(Exception e) {
                log.error("Failed to edit name: " + bizForm.getName() + ", " + e.getLocalizedMessage());
                bizForm.setBizError("Failed to edit name: " + bizForm.getName() + ", " + e.getLocalizedMessage());

                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
                //Re-direct to prevent resubmit
                return "redirect:" + NEXT_PAGE + ".htm";
            }

            if(receiptEntity.getBizName().getId().equals(receiptEntity.getBizStore().getBizName().getId())) {
                redirectAttrs.addFlashAttribute("bizStore", receiptEntity.getBizStore());
                redirectAttrs.addFlashAttribute("last10BizStore", bizService.getAllStoresForBusinessName(receiptEntity));
            } else {
                bizForm.setBizError("Address uniquely identified with another Biz Name: " + receiptEntity.getBizStore().getBizName().getName());
            }

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
            //Re-direct to prevent resubmit
            return "redirect:" + NEXT_PAGE + ".htm";
        }
    }

    /**
     * Search for Biz with either Name, Address, Phone or all or none
     *
     * @param bizForm
     * @param result
     * @param userSession
     * @return
     */
    @RequestMapping(value = "/business", method = RequestMethod.POST, params = "search")
    public String searchBiz(@ModelAttribute("userSession") UserSession userSession, @ModelAttribute("bizForm") BizForm bizForm,
                            BindingResult result,
                            final RedirectAttributes redirectAttrs) {

        DateTime time = DateUtil.now();
        redirectAttrs.addFlashAttribute("bizForm", bizForm);

        bizSearchValidator.validate(bizForm, result);
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("result", result);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
            //Re-direct to prevent resubmit
            return "redirect:" + NEXT_PAGE + ".htm";
        } else {

            Set<BizStoreEntity> bizStoreEntities = searchBizStoreEntities(bizForm);
            redirectAttrs.addFlashAttribute("last10BizStore", bizStoreEntities);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), true);
            //Re-direct to prevent resubmit
            return "redirect:" + NEXT_PAGE + ".htm";
        }
    }

    /**
     * Search for matching biz criteria
     *
     * @param bizForm
     * @return
     */
    private Set<BizStoreEntity> searchBizStoreEntities(BizForm bizForm) {
        String name     = StringUtils.trim(bizForm.getName());
        String address  = StringUtils.trim(bizForm.getAddress());
        String phone    = StringUtils.trim(BizStoreEntity.phoneCleanup(bizForm.getPhone()));
        Set<BizStoreEntity> bizStoreEntities = bizService.bizSearch(name, address, phone);
        bizForm.setBizSuccess("Found '" + bizStoreEntities.size() + "' matching business(es).");
        return bizStoreEntities;
    }
}
