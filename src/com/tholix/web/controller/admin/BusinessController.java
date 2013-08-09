package com.tholix.web.controller.admin;

import java.util.Set;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

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

    @Autowired private ExternalService externalService;
    @Autowired private BizService bizService;

    @Autowired private BizValidator bizValidator;
    @Autowired private BizSearchValidator bizSearchValidator;

    @RequestMapping(value = "/business", method = RequestMethod.GET)
    public ModelAndView loadSearchForm(@ModelAttribute("userSession") UserSession userSession, @ModelAttribute("bizForm") BizForm bizForm) {
        if(userSession.getLevel() == UserLevelEnum.ADMIN) {
            ModelAndView modelAndView = new ModelAndView(NEXT_PAGE);
            return modelAndView;
        }

        //Re-direct user to his home page because user tried accessing Un-Authorized page
        log.warn("Re-direct user to his home page because user tried accessing Un-Authorized page: User: " + userSession.getUserProfileId());
        return new ModelAndView(LoginController.landingHomePage(userSession.getLevel()));
    }

    /**
     *
     * @param bizForm
     * @param result
     * @param userSession - Required when user try to refresh page after log out
     * @return
     */
    @RequestMapping(value = "/business", method = RequestMethod.POST, params = "add")
    public ModelAndView addBiz(@ModelAttribute("bizForm") BizForm bizForm, BindingResult result, @ModelAttribute("userSession") UserSession userSession) {
        DateTime time = DateUtil.now();
        ModelAndView modelAndView = new ModelAndView(NEXT_PAGE);
        modelAndView.addObject("bizForm", bizForm);

        bizValidator.validate(bizForm, result);
        if (result.hasErrors()) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), " failure");
            return modelAndView;
        } else {
            ReceiptEntity receiptEntity = ReceiptEntity.newInstance();
            BizStoreEntity bizStoreEntity = bizForm.getBizStore();
            try {
                externalService.decodeAddress(bizStoreEntity);
            } catch (Exception e) {
                log.error("For Address: " + bizStoreEntity.getAddress() + ", " + e.getLocalizedMessage());
                result.rejectValue("bizError", "", e.getLocalizedMessage());
                return modelAndView;
            }

            receiptEntity.setBizStore(bizStoreEntity);
            receiptEntity.setBizName(bizForm.getBizName());
            try {
                bizService.saveNewBusinessAndOrStore(receiptEntity);
            } catch(Exception e) {
                result.rejectValue("bizError", "", e.getLocalizedMessage());
                return modelAndView;
            }

            if(receiptEntity.getBizName().getId().equals(receiptEntity.getBizStore().getBizName().getId())) {
                modelAndView.addObject("bizStore", receiptEntity.getBizStore());
                modelAndView.addObject("last10BizStore", bizService.getAllStoresForBusinessName(receiptEntity));
            } else {
                result.rejectValue("bizError", "", "Address uniquely identified with another Biz Name: " + receiptEntity.getBizStore().getBizName().getName());
            }

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
            return modelAndView;
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
    public ModelAndView searchBiz(@ModelAttribute("bizForm") BizForm bizForm, BindingResult result, @ModelAttribute("userSession") UserSession userSession) {
        DateTime time = DateUtil.now();
        ModelAndView modelAndView = new ModelAndView(NEXT_PAGE);
        modelAndView.addObject("bizForm", bizForm);

        bizSearchValidator.validate(bizForm, result);
        if (result.hasErrors()) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
            return modelAndView;
        } else {
            BizNameEntity bizNameEntity = bizForm.getBizName();
            BizStoreEntity bizStoreEntity = bizForm.getBizStore();

            String bizName = bizNameEntity.getName();
            String address = bizStoreEntity.getAddress();
            String phone = bizStoreEntity.getPhone();

            Set<BizStoreEntity> bizStoreEntities = bizService.bizSearch(bizName, address, phone);
            modelAndView.addObject("last10BizStore", bizStoreEntities);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), true);
            return modelAndView;
        }
    }
}
