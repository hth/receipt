/**
 *
 */
package com.tholix.web;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.BizStoreEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserSession;
import com.tholix.domain.types.UserLevelEnum;
import com.tholix.repository.BizStoreManager;
import com.tholix.service.AdminLandingService;
import com.tholix.service.ExternalService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.BizForm;
import com.tholix.web.form.UserSearchForm;
import com.tholix.web.validator.BizFormValidator;

/**
 * @author hitender
 * @since Mar 26, 2013 1:14:24 AM
 */
@Controller
@RequestMapping(value = "/admin")
@SessionAttributes({"userSession"})
public class AdminLandingController {
	private static final Logger log = Logger.getLogger(AdminLandingController.class);
	private static final String nextPage = "/admin/landing";

    @Autowired private AdminLandingService adminLandingService;
    @Autowired private ExternalService externalService;
    @Autowired private BizFormValidator bizFormValidator;
    @Autowired private BizStoreManager bizStoreManager;

	@RequestMapping(value = "/landing", method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession) {
        if(userSession.getLevel() == UserLevelEnum.ADMIN) {
            ModelAndView modelAndView = new ModelAndView(nextPage);
            modelAndView.addObject("userSearchForm", UserSearchForm.newInstance());
            modelAndView.addObject("bizForm", BizForm.newInstance());

            return modelAndView;
        }

        //Re-direct user to his home page because user tried accessing Un-Authorized page
        log.warn("Re-direct user to his home page because user tried accessing Un-Authorized page: User: " + userSession.getUserProfileId());
        return new ModelAndView(LoginController.landingHomePage(userSession.getLevel()));
	}

    /**
     * Note: UserSession parameter is to make sure no outside get requests are made.
     *        The error message returned is HTTP ERROR CODE - 403 in case the users is not of a particular level but
     *        method fails on invalid request without User Session and user sees 500 error message.
     *
     * @param name Search for user name
     * @param userSession
     * @param httpServletResponse
     * @return
     */
	@RequestMapping(value = "/find_user", method = RequestMethod.GET)
	public @ResponseBody
    List<String> findUser(@RequestParam("term") String name, @ModelAttribute("userSession") UserSession userSession,
                          HttpServletResponse httpServletResponse) throws IOException {

        if(userSession != null) {
            if(userSession.getLevel() == UserLevelEnum.ADMIN) {
		        return adminLandingService.findMatchingUsers(name);
            } else {
                httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
                return null;
            }
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return null;
        }
	}

    /**
     *
     * @param userSearchForm
     * @param userSession - Required when user try to refresh page after log out
     * @return
     */
	@RequestMapping(value = "/landing", method = RequestMethod.POST)
	public ModelAndView loadUser(@ModelAttribute("userLoginForm") UserSearchForm userSearchForm, @ModelAttribute("userSession") UserSession userSession) {
        DateTime time = DateUtil.now();
        List<UserSearchForm> userSearchForms = adminLandingService.findAllUsers(userSearchForm.getUserName());

        ModelAndView modelAndView = new ModelAndView(nextPage);
        modelAndView.addObject("users", userSearchForms);
        modelAndView.addObject("userSearchForm", UserSearchForm.newInstance());
        modelAndView.addObject("bizForm", BizForm.newInstance());

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
	}

    /**
     *
     * @param bizForm
     * @param result
     * @param userSession - Required when user try to refresh page after log out
     * @return
     */
    @RequestMapping(value = "/addBusiness", method = RequestMethod.POST)
    public ModelAndView addBiz(@ModelAttribute("bizForm") BizForm bizForm, BindingResult result, @ModelAttribute("userSession") UserSession userSession) {
        DateTime time = DateUtil.now();
        ModelAndView modelAndView = new ModelAndView(nextPage);
        modelAndView.addObject("userSearchForm", UserSearchForm.newInstance());
        modelAndView.addObject("bizForm", bizForm);

        bizFormValidator.validate(bizForm, result);
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
                adminLandingService.saveNewBusinessAndOrStore(receiptEntity);
            } catch(Exception e) {
                result.rejectValue("bizError", "", e.getLocalizedMessage());
                return modelAndView;
            }

            if(receiptEntity.getBizName().getId().equals(receiptEntity.getBizStore().getBizName().getId())) {
                modelAndView.addObject("bizStore", receiptEntity.getBizStore());
                modelAndView.addObject("last10BizStore", adminLandingService.getAllStoresForBusinessName(receiptEntity));
            } else {
                result.rejectValue("bizError", "", "Address uniquely identified with another Biz Name: " + receiptEntity.getBizStore().getBizName().getName());
            }

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
            return modelAndView;
        }
    }
}
