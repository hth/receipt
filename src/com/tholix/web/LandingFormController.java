/**
 * 
 */
package com.tholix.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.gridfs.GridFSFile;
import com.tholix.domain.ItemEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UploadReceiptImage;
import com.tholix.domain.UserEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.types.TaxEnum;
import com.tholix.service.ItemFeatureManager;
import com.tholix.service.ItemManager;
import com.tholix.service.ReceiptManager;
import com.tholix.service.StorageManager;
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
	
	@Autowired
	private StorageManager storageManager;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("user") UserEntity user, HttpSession session) {
		log.info("LandingFormController loadForm: " + user.getEmailId());
		session.setAttribute("user", user);
		
		//TODO remove the following two lines
		receiptManager.dropCollection();
		itemManager.dropCollection();
		
		UserProfileEntity userProfileEntity = userProfileManager.getObject(user);
		populate(user);
		
		ModelAndView modelAndView = new ModelAndView(nextPageIsCalledLanding);
		List<ReceiptEntity> receipts = receiptManager.getAllObjectsForUser(user);
		modelAndView.addObject("receipts", receipts);
		modelAndView.addObject("uploadItem", new UploadReceiptImage());
		
		log.info(userProfileEntity.getName());
		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView create(UploadReceiptImage uploadReceiptImage, BindingResult result, HttpSession session) {
		UserEntity user = (UserEntity) session.getAttribute("user");
		if (result.hasErrors()) {
			for (ObjectError error : result.getAllErrors()) {
				System.err.println("Error: " + error.getCode() + " - " + error.getDefaultMessage());
			}
			
			ModelAndView modelAndView = new ModelAndView(nextPageIsCalledLanding);
			List<ReceiptEntity> receipts = receiptManager.getAllObjectsForUser(user);
			modelAndView.addObject("receipts", receipts);
			modelAndView.addObject("uploadItem", new UploadReceiptImage());
			modelAndView.addObject("user", user);
			
			return modelAndView;
		}

		// Some type of file processing...
		log.info("-------------------------------------------");
		log.info("Test upload: " + uploadReceiptImage.getDescription());
		log.info("Test upload: " + uploadReceiptImage.getFileData().getOriginalFilename());
		log.info("Test upload: " + uploadReceiptImage.getFileData().getContentType());
		log.info("-------------------------------------------");
		
		try {
			String receiptBlobId = storageManager.save(uploadReceiptImage);
		    log.info("BolbId: " + receiptBlobId);
		    
		    ReceiptEntity receiptEntity = ReceiptEntity.newInstance(uploadReceiptImage.getDescription(), receiptBlobId, user);
		    receiptManager.saveObject(receiptEntity);
		} catch (IOException e) {
			log.error("IOException occured during saving receipt : " + e.getLocalizedMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Exception occured during saving receipt : " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		ModelAndView modelAndView = new ModelAndView(nextPageIsCalledLanding);
		List<ReceiptEntity> receipts = receiptManager.getAllObjectsForUser(user);
		modelAndView.addObject("receipts", receipts);
		modelAndView.addObject("uploadItem", new UploadReceiptImage());
		modelAndView.addObject("user", user);

		return modelAndView;
	}
	
	private void populate(UserEntity user) {
		
		try {			
			//Item from Barnes and Noble
			ReceiptEntity receipt = ReceiptEntity.updateInstance("Barnes & Noble Booksellers #1944", DateUtil.getDateFromString("12/15/2012 02:13PM"), 8.13, 0.63);
			receipt.setDescription("Item from Barnes and Noble");
			receipt.setUser(user);
			receipt.setReceiptBlobId("1");
			receiptManager.saveObject(receipt);
			log.info("Receipt Id: " + receipt.getId());
			
			ItemEntity item1 = ItemEntity.newInstance(1, "Marble Moc Macchia Tall", 3.75, TaxEnum.TAXED, receipt, user);
			itemManager.saveObject(item1);
			ItemEntity item2 = ItemEntity.newInstance(1, "Car Brulee Latte Tall", 3.75, TaxEnum.TAXED, receipt, user);
			itemManager.saveObject(item2);
			
			//Item from Lucky
			receipt = ReceiptEntity.updateInstance("Lucky", DateUtil.getDateFromString("12/25/12 16:54:57"), 14.61, .34);
			receipt.setDescription("Item from Lucky");
			receipt.setUser(user);
			receipt.setReceiptBlobId("2");
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
