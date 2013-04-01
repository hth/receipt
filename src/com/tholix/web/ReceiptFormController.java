/**
 * 
 */
package com.tholix.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.ItemFeatureManager;
import com.tholix.service.ItemManager;
import com.tholix.service.ReceiptManager;
import com.tholix.service.StorageManager;
import com.tholix.service.UserAuthenticationManager;

/**
 * @author hitender
 * @when Jan 1, 2013 11:55:19 AM
 * 
 */
@Controller
@RequestMapping(value = "/receipt")
public class ReceiptFormController {
	private static final Logger log = Logger.getLogger(ReceiptFormController.class);

	private static String nextPage = "/receipt";

	@Autowired private UserAuthenticationManager userAuthenticationManager;
	@Autowired private ReceiptManager receiptManager;
	@Autowired private StorageManager storageManager;
	@Autowired private ItemManager itemManager;
	@Autowired private ItemFeatureManager itemFeatureManager;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@RequestParam("id") String id, @ModelAttribute("receiptForm") ReceiptEntity receiptForm) {
		log.info("Loading Receipt Item with id: " + id);

		ReceiptEntity receipt = receiptManager.findOne(id);
		List<ItemEntity> items = itemManager.getWhereReceipt(receipt);

		ModelAndView modelAndView = new ModelAndView(nextPage);
		modelAndView.addObject("items", items);
		modelAndView.addObject("receipt", receipt);
		
		receiptForm.setId(receipt.getId());

		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String delete(@ModelAttribute("receiptForm") ReceiptEntity receiptForm, HttpSession session, final RedirectAttributes redirectAttrs) {
		log.info("Delete receipt " + receiptForm.getId());
		
		receiptForm = receiptManager.findOne(receiptForm.getId());
		itemManager.deleteWhereReceipt(receiptForm);
		receiptManager.delete(receiptForm);
		storageManager.deleteObject(receiptForm.getReceiptBlobId());
		
		UserSession userSession = (UserSession) session.getAttribute("userSession");
		redirectAttrs.addFlashAttribute("userSession", userSession);

		return "redirect:/landing.htm";
	}

	public void setUserAuthenticationManager(UserAuthenticationManager userAuthenticationManager) {
		this.userAuthenticationManager = userAuthenticationManager;
	}

	public void setReceiptManager(ReceiptManager receiptManager) {
		this.receiptManager = receiptManager;
	}

	public void setStorageManager(StorageManager storageManager) {
		this.storageManager = storageManager;
	}

	public void setItemManager(ItemManager itemManager) {
		this.itemManager = itemManager;
	}

	public void setItemFeatureManager(ItemFeatureManager itemFeatureManager) {
		this.itemFeatureManager = itemFeatureManager;
	}	
}
