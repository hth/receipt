/**
 * 
 */
package com.tholix.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserSession;
import com.tholix.service.ItemFeatureManager;
import com.tholix.service.ItemManager;
import com.tholix.service.ReceiptManager;
import com.tholix.service.UserAuthenticationManager;

/**
 * @author hitender
 * @when Jan 1, 2013 11:55:19 AM
 * 
 */
@Controller
@RequestMapping(value = "/receipt")
public class ReceiptFormController {
	private final Log log = LogFactory.getLog(getClass());

	private String nextPageIsCalledReceipt = "/receipt";

	@Autowired
	private UserAuthenticationManager userAuthenticationManager;

	@Autowired
	private ReceiptManager receiptManager;

	@Autowired
	private ItemManager itemManager;

	@Autowired
	private ItemFeatureManager itemFeatureManager;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@RequestParam("id") String id) {
		log.info("Loading Receipt Item with id: " + id);

		ReceiptEntity receipt = receiptManager.getObject(id);
		List<ItemEntity> items = itemManager.getObjectWithRecipt(receipt);

		ModelAndView modelAndView = new ModelAndView(nextPageIsCalledReceipt);
		modelAndView.addObject("items", items);
		modelAndView.addObject("receipt", receipt);

		return modelAndView;
	}

}
