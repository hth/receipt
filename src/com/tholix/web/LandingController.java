/**
 *
 */
package com.tholix.web;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UploadReceiptImage;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.domain.value.ReceiptGrouped;
import com.tholix.service.AccountService;
import com.tholix.service.FileDBService;
import com.tholix.service.LandingService;
import com.tholix.service.MailService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.Maths;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.LandingDonutChart;
import com.tholix.web.rest.Base;
import com.tholix.web.rest.Header;
import com.tholix.web.rest.LandingView;

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
    @Autowired MailService mailService;
    @Autowired AccountService accountService;

    private static final String FILE_UPLOAD_FAILURE = "{\"success\" : false}";
    private static final String FILE_UPLOAD_SUCCESS = "{\"success\" : true}";

	/**
	 * Refers to landing.jsp
	 */
	private static final String NEXT_PAGE_IS_CALLED_LANDING = "/landing";

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession, @ModelAttribute("uploadReceiptImage") UploadReceiptImage uploadReceiptImage) {
        DateTime time = DateUtil.now();
        log.info("LandingController loadForm: " + userSession.getEmailId());

		ModelAndView modelAndView = new ModelAndView(NEXT_PAGE_IS_CALLED_LANDING);
        modelAndView.addObject("userSession", userSession);

		List<ReceiptEntity> receipts = landingService.getAllReceiptsForThisMonth(userSession.getUserProfileId());
		modelAndView.addObject("receipts", receipts);

        long pendingCount = landingService.pendingReceipt(userSession.getUserProfileId());
        modelAndView.addObject("pendingCount", pendingCount);

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

    /**
     * For uploading Receipts
     *
     * @param userSession
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "upload")
    public @ResponseBody
    String upload(@ModelAttribute UserSession userSession, HttpServletRequest httpServletRequest) throws IOException {
        DateTime time = DateUtil.now();
        log.info("Upload a receipt");
        String outcome = FILE_UPLOAD_FAILURE;

        boolean isMultipart = ServletFileUpload.isMultipartContent(httpServletRequest);
        if(isMultipart) {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) httpServletRequest;
            final List<MultipartFile> files = multipartHttpServletRequest.getFiles("qqfile");
            Assert.state(files.size() > 0, "0 files exist");

            /*
             * process files
             */
            for (MultipartFile file : files) {
                UploadReceiptImage uploadReceiptImage = UploadReceiptImage.newInstance();
                CommonsMultipartFile commonsMultipartFile = (CommonsMultipartFile) file;
                uploadReceiptImage.setFileData(commonsMultipartFile);
                uploadReceiptImage.setEmailId(userSession.getEmailId());
                uploadReceiptImage.setUserProfileId(userSession.getUserProfileId());
                try {
                    landingService.uploadReceipt(userSession.getUserProfileId(), uploadReceiptImage);
                    outcome = FILE_UPLOAD_SUCCESS;
                    PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
                } catch (Exception exce) {
                    outcome = "{\"success\" : false, \"error\" : \"" + exce.getLocalizedMessage() + "\"}";
                    log.error("Receipt upload exception: " + exce.getLocalizedMessage() + ", for user: " + userSession.getUserProfileId());
                    PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt save");
                }
            }
        } else {
            //TODO test with IE
            //http://skillshared.blogspot.com/2012/08/java-class-for-valums-ajax-file.html
            log.warn("Look like IE file upload");
            String filename = httpServletRequest.getHeader("X-File-Name");
            InputStream is = httpServletRequest.getInputStream();
        }
        return outcome;
    }

    /**
     * Provides user information of home page through a REST URL
     *
     * @param profileId
     * @param authKey
     * @return
     */
    @RequestMapping(value = "/user/{profileId}/auth/{authKey}", method = RequestMethod.GET)
    public @ResponseBody
    Base loadRest(@PathVariable String profileId, @PathVariable String authKey) {
        DateTime time = DateUtil.now();
        log.info("Web Service : " + profileId);

        UserProfileEntity userProfile = authenticate(profileId, authKey);
        if(userProfile != null) {
            long pendingCount = landingService.pendingReceipt(profileId);
            List<ReceiptEntity> receipts = landingService.getAllReceiptsForThisMonth(profileId);

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

    /* http://stackoverflow.com/questions/12117799/spring-mvc-ajax-form-post-handling-possible-methods-and-their-pros-and-cons */
    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    public @ResponseBody
    String invite(@RequestParam(value="emailId") String emailId, @ModelAttribute UserSession userSession) {
        log.info("Invitation sent to: " + emailId);

        boolean isValid = EmailValidator.getInstance().isValid(emailId);
        if(isValid) {
            UserProfileEntity userProfileEntity = accountService.findIfUserExists(emailId);
            if(userProfileEntity == null) {
                boolean status = mailService.sendInvitation(emailId, userSession.getEmailId());
                if(status) {
                    return "Invitation Sent to: " + emailId;
                } else {
                    return "Unsuccessful in sending invitation: " + emailId;
                }
            } else {
                // TODO can put a condition to check or if user is still in invitation mode or has completed registration
                // TODO Based on either condition we can let user recover password or re-send invitation
                return emailId + ", already registered or invited";
            }
        } else {
            return "Invalid Email: " + emailId;
        }
    }
}
