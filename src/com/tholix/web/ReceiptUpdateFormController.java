/**
 * 
 */
package com.tholix.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.GrowthList;
import org.apache.commons.collections.list.LazyList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.AutoPopulatingList;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.UserRegistrationWrapper;
import com.tholix.domain.UserSession;
import com.tholix.service.ItemOCRManager;
import com.tholix.service.ReceiptOCRManager;
import com.tholix.web.form.ReceiptForm;

/**
 * @author hitender 
 * @when Jan 7, 2013 2:13:22 AM
 *
 */
@Controller
@RequestMapping(value = "/receiptupdate")
public class ReceiptUpdateFormController {
	@SuppressWarnings("unused")
	private final Log log = LogFactory.getLog(getClass());

	private String nextPageIsCalledReceiptUpdate = "receiptupdate";

	@Autowired
	private ReceiptOCRManager receiptOCRManager;
	
	@Autowired
	private ItemOCRManager itemOCRManager;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(HttpServletRequest request, HttpSession session) {
		String id = request.getParameter("id");

		ReceiptEntityOCR receipt = receiptOCRManager.getObject(id);
		
		//TODO so far not able to understand why there is a need for AutoPopulatingList. Same issue when using a list.
		AutoPopulatingList<ItemEntityOCR> items = new  AutoPopulatingList<ItemEntityOCR>(ItemEntityOCR.class);
		items.addAll(itemOCRManager.getObjectWithRecipt(receipt));
		
		ReceiptForm receiptForm = ReceiptForm.newInstance(receipt, items);
		
		ModelAndView modelAndView = new ModelAndView(nextPageIsCalledReceiptUpdate, "receiptForm", receiptForm);
		//modelAndView.addObject("receiptUpdate", receiptForm);
		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute("receiptForm") ReceiptForm receiptForm, BindingResult result, final RedirectAttributes redirectAttrs) {
		log.info(receiptForm.getReceipt().getTitle());
		
		
		return "";
	}
	
}
