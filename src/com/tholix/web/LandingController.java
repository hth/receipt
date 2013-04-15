/**
 *
 */
package com.tholix.web;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.UploadReceiptImage;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.domain.types.TaxEnum;
import com.tholix.service.ItemManager;
import com.tholix.service.ItemOCRManager;
import com.tholix.service.ReceiptManager;
import com.tholix.service.ReceiptOCRManager;
import com.tholix.service.StorageManager;
import com.tholix.service.UserProfileManager;
import com.tholix.service.validator.UploadReceiptImageValidator;
import com.tholix.utils.DateUtil;
import com.tholix.utils.Formatter;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.utils.ReceiptParser;
import com.tholix.web.rest.Base;
import com.tholix.web.rest.Header;
import com.tholix.web.rest.LandingView;

/**
 * @author hitender
 * @when Dec 17, 2012 3:19:01 PM
 */
@Controller
@RequestMapping(value = "/landing")
@SessionAttributes({"userSession"})
public class LandingController extends BaseController {
	private static final Logger log = Logger.getLogger(LandingController.class);

	/**
	 * Refers to landing.jsp
	 */
	private static final String NEXT_PAGE_IS_CALLED_LANDING = "/landing";
    private static final String RELOAD_PAGE = "redirect:/landing.htm";

	@Autowired private UserProfileManager userProfileManager;
	@Autowired private ReceiptManager receiptManager;
	@Autowired private ReceiptOCRManager receiptOCRManager;
	@Autowired private ItemManager itemManager;
	@Autowired private ItemOCRManager itemOCRManager;
	@Autowired private StorageManager storageManager;
	@Autowired private UploadReceiptImageValidator uploadReceiptImageValidator;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession, @ModelAttribute("uploadReceiptImage") UploadReceiptImage uploadReceiptImage) {
        DateTime time = DateUtil.now();
        log.info("LandingController loadForm: " + userSession.getEmailId());

		//TODO why pendingCount saved in session
		long pendingCount = receiptOCRManager.numberOfPendingReceipts(userSession.getUserProfileId());
		userSession.setPendingCount(pendingCount);

		ModelAndView modelAndView = new ModelAndView(NEXT_PAGE_IS_CALLED_LANDING);
        modelAndView.addObject("userSession", userSession);

		List<ReceiptEntity> receipts = receiptManager.getAllObjectsForUser(userSession.getUserProfileId());
		modelAndView.addObject("receipts", receipts);

		getTotalExpense(receipts, modelAndView);

		/** Receipt grouped by date */
		Map<Date, BigDecimal> receiptGrouped = receiptManager.getAllObjectsGroupedByDate(userSession.getUserProfileId());
		modelAndView.addObject("receiptGrouped", receiptGrouped);

		log.debug("Logged in user name: " + (userProfileManager.findOne(userSession.getUserProfileId())).getName());
        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
	}

	/**
	 * @param receipts
	 * @param modelAndView
	 */
	private void getTotalExpense(List<ReceiptEntity> receipts, ModelAndView modelAndView) {
		double tax = 0.00;
		double total = 0.00;
		for(ReceiptEntity receipt : receipts) {
			tax += receipt.getTax();
			total += receipt.getTotal();
		}

		modelAndView.addObject("tax", Formatter.df.format(tax));
		modelAndView.addObject("totalWithoutTax", Formatter.df.format(total - tax));
		modelAndView.addObject("total", Formatter.df.format(total));
	}

    //http://static.springsource.org/spring/docs/3.1.x/spring-framework-reference/html/mvc.html
    //16.3.3.16 Support for the 'Last-Modified' Response Header To Facilitate Content Caching
    //TODO make sure hitting refresh should not load the receipt again
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView create(@ModelAttribute("uploadReceiptImage") UploadReceiptImage uploadReceiptImage, @ModelAttribute UserSession userSession, BindingResult result) {
        DateTime time = DateUtil.now();
		uploadReceiptImageValidator.validate(uploadReceiptImage, result);

		/** Check if the uploaded file is of type image. */
		if (result.hasErrors()) {
			for (ObjectError error : result.getAllErrors()) {
				log.error("Error: " + error.getCode() + " - " + error.getDefaultMessage());
			}

			ModelAndView modelAndView = new ModelAndView(NEXT_PAGE_IS_CALLED_LANDING);
			List<ReceiptEntity> receipts = receiptManager.getAllObjectsForUser(userSession.getUserProfileId());
			modelAndView.addObject("receipts", receipts);
			modelAndView.addObject("uploadItem", UploadReceiptImage.newInstance());

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result check");
            return modelAndView;
		}

		// Some type of file processing...
		log.info("-------------------------------------------");
		log.info("Test upload: " + uploadReceiptImage.getDescription());
		log.info("Test upload: " + uploadReceiptImage.getFileData().getOriginalFilename());
		log.info("Test upload: " + uploadReceiptImage.getFileData().getContentType());
		log.info("-------------------------------------------");

		String receiptBlobId = null;
		ReceiptEntityOCR receiptOCR = null;
		List<ItemEntityOCR> items = null;
		try {
			//String receiptOCRTranslation = ABBYYCloudService.instance().performRecognition(uploadReceiptImage.getFileData().getBytes());
			//TODO remove Temp Code
			String receiptOCRTranslation = FileUtils.readFileToString(new File("/Users/hitender/Documents/workspace-sts-3.1.0.RELEASE/Target.txt"));
			log.info(receiptOCRTranslation);

			receiptBlobId = storageManager.saveFile(uploadReceiptImage);
			log.info("BolbId: " + receiptBlobId);

			receiptOCR = ReceiptEntityOCR.newInstance(uploadReceiptImage.getDescription(), ReceiptStatusEnum.OCR_PROCESSED, receiptBlobId, userSession.getUserProfileId(), receiptOCRTranslation);
			items = new LinkedList<>();
			ReceiptParser.read(receiptOCRTranslation, receiptOCR, items);

			receiptOCRManager.save(receiptOCR);
			itemOCRManager.saveObjects(items);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success saved receipt");
        } catch (Exception e) {
			log.error("Exception occurred during saving receipt: " + e.getLocalizedMessage());
			e.printStackTrace();

			int sizeFSInitial = storageManager.getSize();
			log.error("Undo all the saves");
			if(receiptBlobId != null) {
				storageManager.deleteObject(receiptBlobId);
			}
			int sizeFSFinal = storageManager.getSize();
            log.info("Storage File: Initial size: " + sizeFSInitial + ", Final size: " + sizeFSFinal);

			int sizeReceiptInitial = receiptOCRManager.getAllObjects().size();
			if(receiptOCR != null) {
				receiptOCRManager.delete(receiptOCR);
				itemOCRManager.deleteWhereReceipt(receiptOCR);
			}
			int sizeReceiptFinal = receiptOCRManager.getAllObjects().size();
            log.info("Initial size: " + sizeReceiptInitial + ", Final size: " + sizeReceiptFinal);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "failure saving receipt");
            //TODO throw a message to let user know the upload has failed to process
		}

        //No need to reload the UserSession because the loading of the page has changed
		//long pendingCount = receiptOCRManager.numberOfPendingReceipts(userSession.getUserProfileId());
		//userSession.setPendingCount(pendingCount);

        //No need to reload the UserSession because the loading of the page has changed
        //This will force a reload of the page after upload a file. This should prevent upload on refresh.
		ModelAndView modelAndView = new ModelAndView(RELOAD_PAGE);
		//List<ReceiptEntity> receipts = receiptManager.getAllObjectsForUser(userSession.getUserProfileId());
		//modelAndView.addObject("receipts", receipts);
		//modelAndView.addObject("uploadItem", UploadReceiptImage.newInstance());
		//modelAndView.addObject("userSession", userSession);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
        return modelAndView;
	}

    /**
     * Provides user information of home page through a web service URL
     *
     * @param profileId
     * @return
     */
    @RequestMapping(value = "/user/{profileId}/auth/{authKey}", method=RequestMethod.GET)
    public @ResponseBody
    Base loadRest(@PathVariable String profileId, @PathVariable String authKey) {
        DateTime time = DateUtil.now();
        log.info("Web Service : " + profileId);

        UserProfileEntity userProfile = authenticate(profileId, authKey);
        if(userProfile != null) {
            long pendingCount = receiptOCRManager.numberOfPendingReceipts(profileId);
            List<ReceiptEntity> receipts = receiptManager.getAllObjectsForUser(profileId);

            LandingView landingView = LandingView.newInstance(userProfile.getId(), userProfile.getEmailId(), Header.newInstance(getAuth(profileId)));
            landingView.setPendingCount(pendingCount);
            landingView.setReceipts(receipts);
            landingView.setStatus(Header.RESULT.SUCCESS);

            log.info("Web Service returned : " + profileId + ", Email ID: " + userProfile.getEmailId());

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), true);
            return landingView;
        } else {
            Header header = getHeaderForProfileOrAuthFailure();
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
            return header;
        }
    }

    private void populate(UserProfileEntity userProfile) {

		try {
			// Item from Barnes and Noble
			ReceiptEntity receipt = ReceiptEntity.updateInstance("Barnes & Noble Booksellers #1944", DateUtil.getDateFromString("12/15/2012 02:13PM"), 8.13, 0.63);
			receipt.setDescription("Item from Barnes and Noble");
			receipt.setUserProfileId(userProfile.getId());
			receipt.setReceiptBlobId("1");
			receipt.setReceiptStatus(ReceiptStatusEnum.TURK_PROCESSED);
			receiptManager.save(receipt);
			log.info("Receipt Id: " + receipt.getId());

			ItemEntity item1 = ItemEntity.newInstance("1 Marble Moc Macchia Tall", 3.75, TaxEnum.TAXED, 1, receipt, userProfile.getId());
			itemManager.save(item1);
			ItemEntity item2 = ItemEntity.newInstance("1 Car Brulee Latte Tall", 3.75, TaxEnum.TAXED, 2, receipt, userProfile.getId());
			itemManager.save(item2);

			// Item from Lucky
			receipt = ReceiptEntity.updateInstance("Lucky", DateUtil.getDateFromString("12/25/12 16:54:57"), 14.61, .34);
			receipt.setDescription("Item from Lucky");
			receipt.setUserProfileId(userProfile.getId());
			receipt.setReceiptBlobId("2");
			receipt.setReceiptStatus(ReceiptStatusEnum.TURK_PROCESSED);
			receiptManager.save(receipt);
			log.info("Receipt Id: " + receipt.getId());

			item1 = ItemEntity.newInstance("1 SANTA HT LEOPARD", 4.00, TaxEnum.TAXED, 1, receipt, userProfile.getId());
			itemManager.save(item1);
			item2 = ItemEntity.newInstance("1 CUPCAKES 6C UNICED", 2.99, TaxEnum.NOT_TAXED, 2, receipt, userProfile.getId());
			itemManager.save(item2);
			ItemEntity item3 = ItemEntity.newInstance("1 DBK CNMN STRSL SLC", 3.99, TaxEnum.NOT_TAXED, 3, receipt, userProfile.getId());
			itemManager.save(item3);
			ItemEntity item4 = ItemEntity.newInstance("1 GRACE WHL CLV GRLC", 3.29, TaxEnum.NOT_TAXED, 4, receipt, userProfile.getId());
			itemManager.save(item4);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}

	}
}
