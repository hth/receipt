/**
 *
 */
package com.tholix.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UploadReceiptImage;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.domain.value.ReceiptGrouped;
import com.tholix.service.FileDBService;
import com.tholix.service.LandingService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.Maths;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.LandingDonutChart;
import com.tholix.web.rest.Base;
import com.tholix.web.rest.Header;
import com.tholix.web.rest.LandingView;
import com.tholix.web.validator.UploadReceiptImageValidator;

/**
 * @author hitender
 * @since Dec 17, 2012 3:19:01 PM
 */
@Controller
@RequestMapping(value = "/landing")
@SessionAttributes({"userSession"})
public class LandingController extends BaseController {
	private static final Logger log = Logger.getLogger(LandingController.class);

    @Autowired LandingService landingService;
    @Autowired FileDBService fileDBService;
    @Autowired UploadReceiptImageValidator uploadReceiptImageValidator;

	/**
	 * Refers to landing.jsp
	 */
	private static final String NEXT_PAGE_IS_CALLED_LANDING = "/landing";
    private static final String RELOAD_PAGE = "redirect:/landing.htm";

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession, @ModelAttribute("uploadReceiptImage") UploadReceiptImage uploadReceiptImage) {
        DateTime time = DateUtil.now();
        log.info("LandingController loadForm: " + userSession.getEmailId());

		//TODO why pendingCount saved in session
		long pendingCount = landingService.pendingReceipt(userSession.getUserProfileId());
		userSession.setPendingCount(pendingCount);

		ModelAndView modelAndView = new ModelAndView(NEXT_PAGE_IS_CALLED_LANDING);
        modelAndView.addObject("userSession", userSession);

		List<ReceiptEntity> receipts = landingService.allReceipts(userSession.getUserProfileId());
		modelAndView.addObject("receipts", receipts);

        /** Receipt grouped by date */
        log.info("Calculating calendar grouped expense");
        Iterator<ReceiptGrouped> receiptGrouped = landingService.getReceiptGroupedByDate(userSession.getUserProfileId());
        modelAndView.addObject("receiptGrouped", receiptGrouped);

        /** Used for charting in Expense tab */
        log.info("Calculating Pie chart - item expense");
        Map<String, BigDecimal> itemExpenses = landingService.getAllItemExpense(userSession.getUserProfileId());
        modelAndView.addObject("itemExpenses", itemExpenses);

        /** Used for donut chart of each receipts with respect to expense types */
        log.info("Calculating Donut chart - receipt expense");
        populateReceiptExpenseDonutChartDetails(modelAndView, receipts);

        landingService.computeTotalExpense(receipts, modelAndView);

		PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
	}

    /**
     * Populate Receipt expense donut chart
     *
     * @param modelAndView
     * @param receipts
     */
    private void populateReceiptExpenseDonutChartDetails(ModelAndView modelAndView, List<ReceiptEntity> receipts) {
        List<LandingDonutChart> bizByExpenseTypes = new ArrayList<>();
        StringBuilder bizNames_sb = new StringBuilder();
        Map<String, Map<String, BigDecimal>> bizByExpenseTypeMap = landingService.allBusinessByExpenseType(receipts);
        for(String bizName : bizByExpenseTypeMap.keySet()) {
            bizNames_sb.append("'").append(bizName).append("',");

            LandingDonutChart landingDonutChart = LandingDonutChart.newInstance(bizName);

            BigDecimal sum = BigDecimal.ZERO;
            Map<String, BigDecimal> map = bizByExpenseTypeMap.get(bizName);
            for(BigDecimal value : map.values()) {
                sum = Maths.add(sum, value);
            }
            landingDonutChart.setTotal(sum);

            StringBuilder expenseTypes = new StringBuilder();
            StringBuilder expenseValues = new StringBuilder();
            for(String name : map.keySet()) {
                expenseTypes.append("'").append(name).append("',");
                expenseValues.append(map.get(name)).append(",");
            }
            landingDonutChart.setExpenseTypes(expenseTypes.toString().substring(0, expenseTypes.toString().length() - 1));
            landingDonutChart.setExpenseValues(expenseValues.toString().substring(0, expenseValues.toString().length() - 1));

            bizByExpenseTypes.add(landingDonutChart);
        }

        modelAndView.addObject("bizNames", bizNames_sb.toString().substring(0, bizNames_sb.toString().length() > 0 ? (bizNames_sb.toString().length() - 1) : 0));
        modelAndView.addObject("bizByExpenseTypes", bizByExpenseTypes);
    }

    //http://static.springsource.org/spring/docs/3.1.x/spring-framework-reference/html/mvc.html
    //16.3.3.16 Support for the 'Last-Modified' Response Header To Facilitate Content Caching
    //TODO make sure hitting refresh should not load the receipt again
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView create(@ModelAttribute UserSession userSession, @ModelAttribute("uploadReceiptImage") UploadReceiptImage uploadReceiptImage, BindingResult result) {
        DateTime time = DateUtil.now();
		uploadReceiptImageValidator.validate(uploadReceiptImage, result);
		if (result.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView(NEXT_PAGE_IS_CALLED_LANDING);
			List<ReceiptEntity> receipts = landingService.allReceipts(userSession.getUserProfileId());
			modelAndView.addObject("receipts", receipts);
			modelAndView.addObject("uploadItem", UploadReceiptImage.newInstance());

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result check");
            return modelAndView;
		}

        try {
            // Some type of file processing...
            log.info("-------------------------------------------");
            log.info("Test upload: " + uploadReceiptImage.getDescription());
            log.info("Test upload: " + uploadReceiptImage.getFileData().getOriginalFilename());
            log.info("Test upload: " + uploadReceiptImage.getFileData().getContentType());
            log.info("-------------------------------------------");

            landingService.uploadReceipt(userSession.getUserProfileId(), uploadReceiptImage);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
        } catch (Exception exce) {
            log.error(exce.getLocalizedMessage());
            result.rejectValue("fileData", "", exce.getLocalizedMessage());
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt save");
        }

        return new ModelAndView(RELOAD_PAGE);
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
            long pendingCount = landingService.pendingReceipt(profileId);
            List<ReceiptEntity> receipts = landingService.allReceipts(profileId);

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
//			// Item from Barnes and Noble
//			ReceiptEntity receipt = ReceiptEntity.updateInstance("Barnes & Noble Booksellers #1944", DateUtil.getDateFromString("12/15/2012 02:13PM"), 8.13, 0.63);
//			receipt.setDescription("Item from Barnes and Noble");
//			receipt.setUserProfileId(userProfile.getId());
//			receipt.setReceiptBlobId("1");
//			receipt.setReceiptStatus(ReceiptStatusEnum.TURK_PROCESSED);
//			receiptManager.save(receipt);
//			log.info("Receipt Id: " + receipt.getId());
//
//			ItemEntity item1 = ItemEntity.newInstance("1 Marble Moc Macchia Tall", 3.75, TaxEnum.TAXED, 1, receipt, userProfile.getId());
//			itemManager.save(item1);
//			ItemEntity item2 = ItemEntity.newInstance("1 Car Brulee Latte Tall", 3.75, TaxEnum.TAXED, 2, receipt, userProfile.getId());
//			itemManager.save(item2);
//
//			// Item from Lucky
//			receipt = ReceiptEntity.updateInstance("Lucky", DateUtil.getDateFromString("12/25/12 16:54:57"), 14.61, .34);
//			receipt.setDescription("Item from Lucky");
//			receipt.setUserProfileId(userProfile.getId());
//			receipt.setReceiptBlobId("2");
//			receipt.setReceiptStatus(ReceiptStatusEnum.TURK_PROCESSED);
//			receiptManager.save(receipt);
//			log.info("Receipt Id: " + receipt.getId());
//
//			item1 = ItemEntity.newInstance("1 SANTA HT LEOPARD", 4.00, TaxEnum.TAXED, 1, receipt, userProfile.getId());
//			itemManager.save(item1);
//			item2 = ItemEntity.newInstance("1 CUPCAKES 6C UNICED", 2.99, TaxEnum.NOT_TAXED, 2, receipt, userProfile.getId());
//			itemManager.save(item2);
//			ItemEntity item3 = ItemEntity.newInstance("1 DBK CNMN STRSL SLC", 3.99, TaxEnum.NOT_TAXED, 3, receipt, userProfile.getId());
//			itemManager.save(item3);
//			ItemEntity item4 = ItemEntity.newInstance("1 GRACE WHL CLV GRLC", 3.29, TaxEnum.NOT_TAXED, 4, receipt, userProfile.getId());
//			itemManager.save(item4);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}

	}
}
