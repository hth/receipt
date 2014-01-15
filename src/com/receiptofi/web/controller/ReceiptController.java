/**
 *
 */
package com.receiptofi.web.controller;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.ItemEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.UserSession;
import com.receiptofi.repository.BizNameManager;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.service.UserProfilePreferenceService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.PerformanceProfiling;
import com.receiptofi.web.form.ReceiptForm;
import com.receiptofi.web.helper.ReceiptLandingView;
import com.receiptofi.web.rest.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

/**
 * @author hitender
 * @since Jan 1, 2013 11:55:19 AM
 *
 */
@Controller
@RequestMapping(value = "/receipt")
@SessionAttributes({"userSession"})
public class ReceiptController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ReceiptController.class);

	private static String NEXT_PAGE = "/receipt";
    private static String NEXT_PAGE_BY_BIZ = "/receiptByBiz";

    @Autowired private ReceiptService receiptService;
    @Autowired private BizNameManager bizNameManager;
    @Autowired private UserProfilePreferenceService userProfilePreferenceService;

	@RequestMapping(value = "/{receiptId}", method = RequestMethod.GET)
	public ModelAndView loadForm(@PathVariable String receiptId, @ModelAttribute("receiptForm") ReceiptForm receiptForm, @ModelAttribute("userSession") UserSession userSession) {
        DateTime time = DateUtil.now();
        log.info("Loading Receipt Item with id: " + receiptId);

        ReceiptEntity receiptEntity = receiptService.findReceipt(receiptId, userSession.getUserProfileId());
        if(receiptEntity != null) {
            List<ItemEntity> items = receiptService.findItems(receiptEntity);
            List<ExpenseTagEntity> expenseTypes = userProfilePreferenceService.activeExpenseTypes(userSession.getUserProfileId());

            receiptForm.setReceipt(receiptEntity);
            receiptForm.setItems(items);
            receiptForm.setExpenseTags(expenseTypes);
        } else {
            //TODO check all get methods that can result in display sensitive data of other users to someone else fishing
            //Possible condition of bookmark or trying to gain access to some unknown receipt
            log.warn("User " + userSession.getUserProfileId() + ", tried submitting an invalid receipt id: " + receiptId);
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return new ModelAndView(NEXT_PAGE);
	}

	@RequestMapping(method = RequestMethod.POST, params="delete")
	public String delete(@ModelAttribute("receiptForm") ReceiptForm receiptForm) {
        DateTime time = DateUtil.now();
        log.info("Delete receipt " + receiptForm.getReceipt().getId());

        boolean task = false;
        try {
            task = receiptService.deleteReceipt(receiptForm.getReceipt().getId());
            if(task == false) {
                //TODO in case of failure to delete send message to USER
            }
        } catch(Exception exce) {
            log.error("Error occurred during receipt delete: Receipt Id: " + receiptForm.getReceipt().getId() + ", error message: " + exce.getLocalizedMessage());
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), task);
		return "redirect:/landing.htm";
	}

    @RequestMapping(method = RequestMethod.POST, params="re-check")
    public ModelAndView recheck(@ModelAttribute("receiptForm") ReceiptForm receiptForm, @ModelAttribute("userSession") UserSession userSession) {
        DateTime time = DateUtil.now();
        log.info("Initiating re-check on receipt " + receiptForm.getReceipt().getId());

        try {
            receiptService.reopen(receiptForm);
        } catch(Exception exce) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
            log.error(exce.getLocalizedMessage() + ", Receipt: " + receiptForm.getReceipt().getId());

            receiptForm.setErrorMessage(exce.getLocalizedMessage());
            return loadForm(receiptForm.getReceipt().getId(), receiptForm, userSession);
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return new ModelAndView("redirect:/landing.htm");
    }

    @RequestMapping(method = RequestMethod.POST, params="update-expense-type")
    public String expenseUpdate(@ModelAttribute("receiptForm") ReceiptForm receiptForm) {
        DateTime time = DateUtil.now();
        log.info("Initiating Expense Type update on receipt " + receiptForm.getReceipt().getId());

        for(ItemEntity item : receiptForm.getItems()) {
            ExpenseTagEntity expenseType = userProfilePreferenceService.getExpenseType(item.getExpenseTag().getId());
            item.setExpenseTag(expenseType);
            try {
                receiptService.updateItemWithExpenseType(item);
            } catch (Exception e) {
                log.error("Error updating ExpenseType '" + item.getExpenseTag().getId() + "', " +
                        "for ItemEntity '" + item.getId() + "'. Error Message: " + e.getLocalizedMessage());

                //TODO send error message back saying update unsuccessful.
            }
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return "redirect:/landing.htm";
    }

    /**
     * Delete receipt through REST URL
     *
     * @param receiptId receipt id to delete
     * @param profileId user id
     * @param authKey   auth key
     * @return Header
     */
    @RequestMapping(value = "/d/{receiptId}/user/{profileId}/auth/{authKey}.xml", method=RequestMethod.GET)
    public @ResponseBody
    Header deleteRest(@PathVariable String receiptId, @PathVariable String profileId, @PathVariable String authKey) {
        DateTime time = DateUtil.now();
        log.info("Delete receipt " + receiptId);

        UserProfileEntity userProfile = authenticate(profileId, authKey);
        Header header = Header.newInstance(authKey);
        if(userProfile != null) {
            try {
                boolean task = receiptService.deleteReceipt(receiptId);
                if(task) {
                    header.setStatus(Header.RESULT.SUCCESS);
                    header.setMessage("Deleted receipt successfully");
                    PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), true);
                    return header;
                } else {
                    header.setStatus(Header.RESULT.FAILURE);
                    header.setMessage("Delete receipt un-successful");
                    PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
                    return header;
                }
            } catch (Exception exce) {
                header.setStatus(Header.RESULT.FAILURE);
                header.setMessage("Delete receipt un-successful");
                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
                return header;
            }
        } else {
            header = getHeaderForProfileOrAuthFailure();
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
            return header;
        }
    }

    /**
     *
     * @param id
     * @param userSession
     * @return
     */
    @RequestMapping(value = "/biz/{id}", method = RequestMethod.GET)
    public ModelAndView receiptByBizName(@PathVariable String id,
                                         @ModelAttribute("userSession") UserSession userSession,
                                         HttpServletResponse httpServletResponse) throws IOException {

        DateTime time = DateUtil.now();
        log.info("Loading Receipts by Biz Name id: " + id);
        List<ReceiptLandingView> receiptLandingViews = new ArrayList<>();

        ModelAndView modelAndView = new ModelAndView(NEXT_PAGE_BY_BIZ);
        if(userSession != null) {

            List<BizNameEntity> bizNames = bizNameManager.findAllBiz(id);
            for(BizNameEntity bizNameEntity : bizNames) {
                List<ReceiptEntity> receipts = receiptService.findReceipt(bizNameEntity, userSession.getUserProfileId());
                for(ReceiptEntity receiptEntity : receipts) {
                    receiptLandingViews.add(ReceiptLandingView.newInstance(receiptEntity));
                }
            }

            modelAndView.addObject("receiptLandingViews", receiptLandingViews);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
            return modelAndView;
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
            return null;
        }
    }
}
