/**
 * 
 */
package com.tholix.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserEntity;
import com.tholix.service.ItemFeatureManager;
import com.tholix.service.ItemManager;
import com.tholix.service.ReceiptManager;
import com.tholix.service.UserManager;

/**
 * @author hitender 
 * @when Jan 1, 2013 11:55:19 AM
 *
 */
@Controller
@RequestMapping(value = "/showreceipt")
public class ShowReceiptFormController {
	
	private final Log log = LogFactory.getLog(getClass());
	
	private String nextPageIsCalledShowReceipt = "/showreceipt";
	
	@Autowired
	private UserManager userManager;
	
	@Autowired
	private ReceiptManager receiptManager;
	
	@Autowired
	private ItemManager itemManager;
	
	@Autowired
	private ItemFeatureManager itemFeatureManager;		
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(HttpServletRequest request) {
		log.info("Loading Receipt Item with id: " + request.getParameter("id"));
		
		UserEntity user = userManager.getObject(request.getParameter("uid"));
		ReceiptEntity receipt = receiptManager.getObject(request.getParameter("id"));
		List<ItemEntity> items = itemManager.getObjectWithRecipt(receipt);
		
		ModelAndView modelAndView = new ModelAndView(nextPageIsCalledShowReceipt);
		modelAndView.addObject("items", items);
		modelAndView.addObject("receipt", receipt);
		
		return modelAndView;
	}

}
