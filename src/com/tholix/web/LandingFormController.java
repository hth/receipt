/**
 * 
 */
package com.tholix.web;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.UploadReceiptImage;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.domain.types.TaxEnum;
import com.tholix.service.ItemFeatureManager;
import com.tholix.service.ItemManager;
import com.tholix.service.ItemOCRManager;
import com.tholix.service.ReceiptManager;
import com.tholix.service.ReceiptOCRManager;
import com.tholix.service.StorageManager;
import com.tholix.service.UserProfileManager;
import com.tholix.service.validator.UploadReceiptImageValidator;
import com.tholix.utils.DateUtil;
import com.tholix.utils.ReceiptParser;

/**
 * @author hitender
 * @when Dec 17, 2012 3:19:01 PM
 */
@SuppressWarnings("unused")
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
	private ReceiptOCRManager receiptOCRManager;

	@Autowired
	private ItemManager itemManager;

	@Autowired
	private ItemOCRManager itemOCRManager;

	@Autowired
	private ItemFeatureManager itemFeatureManager;

	@Autowired
	private StorageManager storageManager;
	
	@Autowired
	private UploadReceiptImageValidator uploadReceiptImageValidator;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession, @ModelAttribute("uploadReceiptImage") UploadReceiptImage uploadReceiptImage, HttpSession session) {
		log.info("LandingFormController loadForm: " + userSession.getEmailId());
		
		long pendingCount = receiptOCRManager.numberOfPendingReceipts(userSession.getUserProfileId());
		userSession.setPendingCount(pendingCount);
		session.setAttribute("userSession", userSession);

		// TODO remove the following two lines
//		receiptManager.dropCollection();
//		itemManager.dropCollection();

		UserProfileEntity userProfileEntity = userProfileManager.getObject(userSession.getUserProfileId());
		//populate(userProfileEntity);

		ModelAndView modelAndView = new ModelAndView(nextPageIsCalledLanding);
		List<ReceiptEntity> receipts = receiptManager.getAllObjectsForUser(userSession.getUserProfileId());
		modelAndView.addObject("receipts", receipts);

		log.info(userProfileEntity.getName());
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView create(@ModelAttribute("uploadReceiptImage") UploadReceiptImage uploadReceiptImage, BindingResult result, HttpSession session) {
		UserSession userSession = (UserSession) session.getAttribute("userSession");
		uploadReceiptImageValidator.validate(uploadReceiptImage, result);
		
		/** Check if the uploaded file is of type image. */
		if (result.hasErrors()) {
			for (ObjectError error : result.getAllErrors()) {
				log.error("Error: " + error.getCode() + " - " + error.getDefaultMessage());
			}			
			
			ModelAndView modelAndView = new ModelAndView(nextPageIsCalledLanding);
			List<ReceiptEntity> receipts = receiptManager.getAllObjectsForUser(userSession.getUserProfileId());
			modelAndView.addObject("receipts", receipts);
			modelAndView.addObject("uploadItem", UploadReceiptImage.newInstance());
			
			return modelAndView;
		}

		// Some type of file processing...
		log.info("-------------------------------------------");
		log.info("Test upload: " + uploadReceiptImage.getDescription());
		log.info("Test upload: " + uploadReceiptImage.getFileData().getOriginalFilename());
		log.info("Test upload: " + uploadReceiptImage.getFileData().getContentType());
		log.info("-------------------------------------------");

		try {
			// String receiptOCRTranslation = ABBYYCloudService.instance().performRecognition(uploadReceiptImage.getFileData().getBytes());

			//TODO remove Temp Code
			String receiptOCRTranslation = FileUtils.readFileToString(new File("/Users/hitender/Documents/workspace-sts-3.1.0.RELEASE/BB.txt"));
			String receiptBlobId = storageManager.save(uploadReceiptImage);
			log.info("BolbId: " + receiptBlobId);

			ReceiptEntityOCR receiptOCR = ReceiptEntityOCR.newInstance(uploadReceiptImage.getDescription(), ReceiptStatusEnum.OCR_PROCESSED, receiptBlobId, userSession.getUserProfileId(), receiptOCRTranslation);
			receiptOCRManager.saveObject(receiptOCR);
			
			List<ItemEntityOCR> items = new LinkedList<ItemEntityOCR>();
			ReceiptParser.read(receiptOCRTranslation, receiptOCR, items);

			receiptOCRManager.saveObject(receiptOCR);
			itemOCRManager.saveObjects(items);
		} catch (IOException e) {
			log.error("IOException occured during saving receipt : " + e.getLocalizedMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Exception occured during saving receipt : " + e.getLocalizedMessage());
			e.printStackTrace();
		}

		long pendingCount = receiptOCRManager.numberOfPendingReceipts(userSession.getUserProfileId());
		userSession.setPendingCount(pendingCount);

		ModelAndView modelAndView = new ModelAndView(nextPageIsCalledLanding);
		List<ReceiptEntity> receipts = receiptManager.getAllObjectsForUser(userSession.getUserProfileId());
		modelAndView.addObject("receipts", receipts);
		modelAndView.addObject("uploadItem", new UploadReceiptImage());
		modelAndView.addObject("userSession", userSession);

		return modelAndView;
	}

	private void populate(UserProfileEntity userProfile) {

		try {
			// Item from Barnes and Noble
			ReceiptEntity receipt = ReceiptEntity.updateInstance("Barnes & Noble Booksellers #1944", DateUtil.getDateFromString("12/15/2012 02:13PM"), 8.13, 0.63);
			receipt.setDescription("Item from Barnes and Noble");
			receipt.setUserProfileId(userProfile.getId());
			receipt.setReceiptBlobId("1");
			receipt.setReceiptStatus(ReceiptStatusEnum.TURK_PROCESSED);
			receiptManager.saveObject(receipt);
			log.info("Receipt Id: " + receipt.getId());

			ItemEntity item1 = ItemEntity.newInstance("1 Marble Moc Macchia Tall", 3.75, TaxEnum.TAXED, 1, receipt, userProfile.getId());
			itemManager.saveObject(item1);
			ItemEntity item2 = ItemEntity.newInstance("1 Car Brulee Latte Tall", 3.75, TaxEnum.TAXED, 2, receipt, userProfile.getId());
			itemManager.saveObject(item2);

			// Item from Lucky
			receipt = ReceiptEntity.updateInstance("Lucky", DateUtil.getDateFromString("12/25/12 16:54:57"), 14.61, .34);
			receipt.setDescription("Item from Lucky");
			receipt.setUserProfileId(userProfile.getId());
			receipt.setReceiptBlobId("2");
			receipt.setReceiptStatus(ReceiptStatusEnum.TURK_PROCESSED);
			receiptManager.saveObject(receipt);
			log.info("Receipt Id: " + receipt.getId());

			item1 = ItemEntity.newInstance("1 SANTA HT LEOPARD", 4.00, TaxEnum.TAXED, 1, receipt, userProfile.getId());
			itemManager.saveObject(item1);
			item2 = ItemEntity.newInstance("1 CUPCAKES 6C UNICED", 2.99, TaxEnum.NOT_TAXED, 2, receipt, userProfile.getId());
			itemManager.saveObject(item2);
			ItemEntity item3 = ItemEntity.newInstance("1 DBK CNMN STRSL SLC", 3.99, TaxEnum.NOT_TAXED, 3, receipt, userProfile.getId());
			itemManager.saveObject(item3);
			ItemEntity item4 = ItemEntity.newInstance("1 GRACE WHL CLV GRLC", 3.29, TaxEnum.NOT_TAXED, 4, receipt, userProfile.getId());
			itemManager.saveObject(item4);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}

	}
}
