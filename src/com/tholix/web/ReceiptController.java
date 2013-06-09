/**
 *
 */
package com.tholix.web;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.ExpenseTypeEntity;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.ReceiptService;
import com.tholix.service.UserProfilePreferenceService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.ReceiptForm;
import com.tholix.web.rest.Header;

/**
 * @author hitender
 * @since Jan 1, 2013 11:55:19 AM
 *
 */
@Controller
@RequestMapping(value = "/receipt")
@SessionAttributes({"userSession"})
public class ReceiptController extends BaseController {
	private static final Logger log = Logger.getLogger(ReceiptController.class);

	private static String nextPage = "/receipt";

    @Autowired private ReceiptService receiptService;
    @Autowired private UserProfilePreferenceService userProfilePreferenceService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@RequestParam("id") String receiptId, @ModelAttribute("receiptForm") ReceiptForm receiptForm, @ModelAttribute("userSession") UserSession userSession) {
        DateTime time = DateUtil.now();
        log.info("Loading Receipt Item with id: " + receiptId);

        ReceiptEntity receiptEntity = receiptService.findReceipt(receiptId);
        List<ItemEntity> items = receiptService.findItems(receiptEntity);
        List<ExpenseTypeEntity> expenseTypes = userProfilePreferenceService.activeExpenseTypes(userSession.getUserProfileId());

        receiptForm.setReceipt(receiptEntity);
        receiptForm.setItems(items);
        receiptForm.setExpenseTypes(expenseTypes);

        ModelAndView modelAndView = new ModelAndView(nextPage);
        modelAndView.addObject("receiptForm", receiptForm);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
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
            log.error(exce.getLocalizedMessage());
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), task);
		return "redirect:/landing.htm";
	}

    @RequestMapping(method = RequestMethod.POST, params="re-check")
    public String recheck(@ModelAttribute("receiptForm") ReceiptForm receiptForm) {
        DateTime time = DateUtil.now();
        log.info("Initiating re-check on receipt " + receiptForm.getReceipt().getId());

        try {
            receiptService.reopen(receiptForm.getReceipt().getId());
        } catch(Exception exce) {
            log.error(exce.getLocalizedMessage());
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return "redirect:/landing.htm";
    }

    @RequestMapping(method = RequestMethod.POST, params="update-expense-type")
    public String expenseUpdate(@ModelAttribute("receiptForm") ReceiptForm receiptForm) {
        DateTime time = DateUtil.now();
        log.info("Initiating Expense Type update on receipt " + receiptForm.getReceipt().getId());

        for(ItemEntity item : receiptForm.getItems()) {
            ExpenseTypeEntity expenseType = userProfilePreferenceService.getExpenseType(item.getExpenseType().getId());
            item.setExpenseType(expenseType);
            try {
                receiptService.updateItemWithExpenseType(item);
            } catch (Exception e) {
                log.error("Error updating ExpenseType '" + item.getExpenseType().getId() + "', " +
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
    @RequestMapping(value = "/d/{id}/user/{profileId}/auth/{authKey}", method=RequestMethod.GET)
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
}
