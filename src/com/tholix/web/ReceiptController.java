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
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.service.ReceiptService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.rest.Header;

/**
 * @author hitender
 * @when Jan 1, 2013 11:55:19 AM
 *
 */
@Controller
@RequestMapping(value = "/receipt")
public class ReceiptController extends BaseController {
	private static final Logger log = Logger.getLogger(ReceiptController.class);

	private static String nextPage = "/receipt";

    @Autowired private ReceiptService receiptService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@RequestParam("id") String receiptId, @ModelAttribute("receiptForm") ReceiptEntity receiptForm) {
        DateTime time = DateUtil.now();
        log.info("Loading Receipt Item with id: " + receiptId);

        ReceiptEntity receiptEntity = receiptService.findReceipt(receiptId);
        List<ItemEntity> items = receiptService.findItems(receiptEntity);

		ModelAndView modelAndView = new ModelAndView(nextPage);
		modelAndView.addObject("items", items);
		modelAndView.addObject("receipt", receiptEntity);

		receiptForm.setId(receiptEntity.getId());

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.POST, params="Delete")
	public String delete(@ModelAttribute("receiptForm") ReceiptEntity receiptForm) {
        DateTime time = DateUtil.now();
        log.info("Delete receipt " + receiptForm.getId());

        boolean task = receiptService.deleteReceipt(receiptForm.getId());
        if(task == false) {
            //TODO in case of failure to delete send message to USER
        }

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), task);
		return "redirect:/landing.htm";
	}

    @RequestMapping(method = RequestMethod.POST, params="Re-Check")
    public String recheck(@ModelAttribute("receiptForm") ReceiptEntity receiptForm) {
        DateTime time = DateUtil.now();
        log.info("Initiating re-check on receipt " + receiptForm.getId());

        receiptService.reopen(receiptForm.getId());

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return "redirect:/landing.htm";
    }

    /**
     * Delete receipt through REST request
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
        if(userProfile != null) {
            boolean task = receiptService.deleteReceipt(receiptId);
            Header header = Header.newInstance(authKey);
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
        } else {
            Header header = getHeaderForProfileOrAuthFailure();
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
            return header;
        }
    }
}
