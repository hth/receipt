/**
 * 
 */
package com.tholix.web;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.types.TaxEnum;
import com.tholix.service.ItemFeatureManager;
import com.tholix.service.ItemManager;
import com.tholix.service.ReceiptManager;
import com.tholix.service.UserProfileManager;
import com.tholix.utils.DateUtil;

/**
 * @author hitender
 * @when Dec 17, 2012 3:19:01 PM
 */
@Controller
@RequestMapping(value = "/landing")
public class LandingFormController {
	private final Log log = LogFactory.getLog(getClass());	
	
	/**
	 * Refers to landing.jsp
	 */
	private String nextPageIsCalledLanding = "/landing";

	@Autowired
	private UserProfileManager userProfileManager;
	
	@Autowired
	private ReceiptManager receiptManager;
	
	@Autowired
	private ItemManager itemManager;
	
	@Autowired
	private ItemFeatureManager itemFeatureManager;	

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("user") UserEntity user) {
		log.info("LandingFormController loadForm: " + user.getEmailId());
		
		//TODO remove the following two lines
		receiptManager.dropCollection();
		itemManager.dropCollection();
		
		UserProfileEntity userProfileEntity = userProfileManager.getObject(user);
		populate(user);
		
		ModelAndView modelAndView = new ModelAndView(nextPageIsCalledLanding);
		List<ReceiptEntity> receipts = receiptManager.getAllObjectsForUser(user);
		modelAndView.addObject("receipts", receipts);
		
		log.info(userProfileEntity.getName());
		return modelAndView;
	}
	
	private void populate(UserEntity user) {
		
		try {			
			//Item from Barnes and Noble
			ReceiptEntity receipt = ReceiptEntity.newInstance("Barnes & Noble Booksellers #1944", DateUtil.getDateFromString("12/15/2012 02:13PM"), 8.13, 0.63, user);
			receiptManager.saveObject(receipt);
			log.info("Receipt Id: " + receipt.getId());
			
			ItemEntity item1 = ItemEntity.newInstance(1, "Marble Moc Macchia Tall", 3.75, TaxEnum.TAXED, receipt, user);
			itemManager.saveObject(item1);
			ItemEntity item2 = ItemEntity.newInstance(1, "Car Brulee Latte Tall", 3.75, TaxEnum.TAXED, receipt, user);
			itemManager.saveObject(item2);
			
			//Item from Lucky
			receipt = ReceiptEntity.newInstance("Lucky", DateUtil.getDateFromString("12/25/12 16:54:57"), 14.61, .34, user);
			receiptManager.saveObject(receipt);
			log.info("Receipt Id: " + receipt.getId());
			
			item1 = ItemEntity.newInstance(1, "SANTA HT LEOPARD", 4.00, TaxEnum.TAXED, receipt, user);
			itemManager.saveObject(item1);
			item2 = ItemEntity.newInstance(1, "CUPCAKES 6C UNICED", 2.99, TaxEnum.NOT_TAXED, receipt, user);
			itemManager.saveObject(item2);
			ItemEntity item3 = ItemEntity.newInstance(1, "DBK CNMN STRSL SLC", 3.99, TaxEnum.NOT_TAXED, receipt, user);
			itemManager.saveObject(item3);
			ItemEntity item4 = ItemEntity.newInstance(1, "GRACE WHL CLV GRLC", 3.29, TaxEnum.NOT_TAXED, receipt, user);
			itemManager.saveObject(item4);
			
		} catch(Exception e) {
			log.error(e.getLocalizedMessage());
		}

	}
}
