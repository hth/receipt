/**
 *
 */
package com.tholix.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.tholix.domain.NotificationEntity;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.UserSession;
import com.tholix.domain.types.FileTypeEnum;
import com.tholix.domain.types.NotificationTypeEnum;
import com.tholix.domain.types.UserLevelEnum;
import com.tholix.domain.value.ReceiptGrouped;
import com.tholix.domain.value.ReceiptGroupedByBizLocation;
import com.tholix.service.AccountService;
import com.tholix.service.FileDBService;
import com.tholix.service.LandingService;
import com.tholix.service.MailService;
import com.tholix.service.NotificationService;
import com.tholix.service.ReportService;
import com.tholix.service.mobile.LandingViewService;
import com.tholix.utils.DateUtil;
import com.tholix.utils.Maths;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.LandingDonutChart;
import com.tholix.web.form.LandingForm;
import com.tholix.web.form.UploadReceiptImage;
import com.tholix.web.helper.ReceiptForMonth;
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
    @Autowired NotificationService notificationService;
    @Autowired ReportService reportService;
    @Autowired LandingViewService landingViewService;

	/**
	 * Refers to landing.jsp
	 */
	private static final String NEXT_PAGE_IS_CALLED_LANDING = "/landing";

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@ModelAttribute("userSession") UserSession userSession,
                                 @ModelAttribute("uploadReceiptImage") UploadReceiptImage uploadReceiptImage,
                                 @ModelAttribute("landingForm") LandingForm landingForm) {

        DateTime time = DateUtil.now();
        log.info("LandingController loadForm: " + userSession.getEmailId());

		ModelAndView modelAndView = new ModelAndView(NEXT_PAGE_IS_CALLED_LANDING);
        modelAndView.addObject("userSession", userSession);

		List<ReceiptEntity> allReceiptsForThisMonth = landingService.getAllReceiptsForThisMonth(userSession.getUserProfileId(), DateUtil.now());
        ReceiptForMonth receiptForMonth = landingService.getReceiptForMonth(allReceiptsForThisMonth, DateUtil.now());
        modelAndView.addObject("receiptForMonth", receiptForMonth);
        landingForm.setReceiptForMonth(receiptForMonth);

        long pendingCount = landingService.pendingReceipt(userSession.getUserProfileId());
        modelAndView.addObject("pendingCount", pendingCount);

        /** Receipt grouped by date */
        log.info("Calculating calendar grouped expense");
        Iterator<ReceiptGrouped> receiptGrouped = landingService.getReceiptGroupedByDate(userSession.getUserProfileId());
        landingForm.setReceiptGrouped(receiptGrouped);

        /** Lists all the receipt grouped by months */
        List<ReceiptGrouped> receiptGroupedByMonth = landingService.getAllObjectsGroupedByMonth(userSession.getUserProfileId());
        modelAndView.addObject("months", landingService.addMonthsIfLessThanThree(receiptGroupedByMonth));
        landingForm.setReceiptGroupedByMonths(receiptGroupedByMonth);

        if(userSession.getLevel().value >= UserLevelEnum.USER_COMMUNITY.value) {
            List<ReceiptGroupedByBizLocation> receiptGroupedByBizLocations = landingService.getAllObjectsGroupedByBizLocation(userSession.getUserProfileId());
            landingForm.setReceiptGroupedByBizLocations(receiptGroupedByBizLocations);
        }

        /** Used for charting in Expense Analysis tab */
        log.info("Calculating Pie chart - item expense");
        Map<String, BigDecimal> itemExpenses = landingService.getAllItemExpense(userSession.getUserProfileId());
        modelAndView.addObject("itemExpenses", itemExpenses);

        /** Used for donut chart of each receipts with respect to expense types in TAB 1 */
        log.info("Calculating Donut chart - receipt expense");
        /** bizNames and bizByExpenseTypes added below to landingForm*/
        populateReceiptExpenseDonutChartDetails(landingForm, allReceiptsForThisMonth);

        landingService.computeTotalExpense(userSession.getUserProfileId(), modelAndView);

        /** Notification */
        List<NotificationEntity> notifications = landingService.notifications(userSession.getUserProfileId());
        landingForm.setNotifications(notifications);

		PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
        return modelAndView;
	}

    /**
     * Loads monthly data for the selected month in the calendar
     *
     * @param monthView
     * @param previousOrNext
     * @param userSession
     * @param httpServletResponse
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/monthly_expenses", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView monthlyExpenses(@RequestParam("monthView") String monthView,
                                 @RequestParam("buttonClick") String previousOrNext,
                                 @ModelAttribute("userSession") UserSession userSession,
                                 @ModelAttribute("landingForm") LandingForm landingForm,
                                 HttpServletResponse httpServletResponse) throws IOException {

        ModelAndView modelAndView = new ModelAndView("/z/landingTabs");

        if(userSession != null) {
            String pattern = "MMM, yyyy";
            DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
            DateTime monthYear = dtf.parseDateTime(monthView);
            if(previousOrNext.equalsIgnoreCase("next")) {
                monthYear = monthYear.minusMonths(1);
            }

            List<ReceiptEntity> allReceiptsForThisMonth = landingService.getAllReceiptsForThisMonth(userSession.getUserProfileId(), monthYear);
            ReceiptForMonth receiptForMonth = landingService.getReceiptForMonth(allReceiptsForThisMonth, monthYear);
            landingForm.setReceiptForMonth(receiptForMonth);

            /** Used for donut chart of each receipts with respect to expense types in TAB 1*/
            log.info("Calculating Donut chart - receipt expense");
            populateReceiptExpenseDonutChartDetails(landingForm, allReceiptsForThisMonth);
        } else {
            httpServletResponse.sendError(SC_FORBIDDEN, "Cannot access directly");
        }

        return modelAndView;
    }

    /**
     * Populate Receipt expense donut chart
     *
     * @param landingForm
     * @param receipts
     */
    private void populateReceiptExpenseDonutChartDetails(LandingForm landingForm, List<ReceiptEntity> receipts) {
        List<LandingDonutChart> bizByExpenseTypes = new ArrayList<>();
        StringBuilder bizNames_sb = new StringBuilder();
        Map<String, Map<String, BigDecimal>> bizByExpenseTypeMap = landingService.allBusinessByExpenseType(receipts);
        for(String bizName : bizByExpenseTypeMap.keySet()) {
            //bizNames_sb.append("'").append(StringUtils.abbreviate(bizName, LandingDonutChart.OFF_SET, LandingDonutChart.MAX_WIDTH)).append("',");
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

        landingForm.setBizNames(bizNames_sb.toString().substring(0, bizNames_sb.toString().length() > 0 ? (bizNames_sb.toString().length() - 1) : 0));
        landingForm.setBizByExpenseTypes(bizByExpenseTypes);
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
        String outcome = "{\"success\" : false}";

        boolean isMultipart = ServletFileUpload.isMultipartContent(httpServletRequest);
        if(isMultipart) {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) httpServletRequest;
            final List<MultipartFile> files = multipartHttpServletRequest.getFiles("qqfile");
            Assert.state(files.size() > 0, "0 files exist");

            /*
             * process files
             */
            for (MultipartFile multipartFile : files) {
                UploadReceiptImage uploadReceiptImage = UploadReceiptImage.newInstance();
                uploadReceiptImage.setFileData(multipartFile);
                uploadReceiptImage.setEmailId(userSession.getEmailId());
                uploadReceiptImage.setUserProfileId(userSession.getUserProfileId());
                uploadReceiptImage.setFileType(FileTypeEnum.RECEIPT);
                try {
                    landingService.uploadReceipt(userSession.getUserProfileId(), uploadReceiptImage);
                    outcome = "{\"success\" : true, \"uploadMessage\" : \"File uploaded successfully\"}";
                    PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
                } catch (Exception exce) {
                    outcome = "{\"success\" : false, \"uploadMessage\" : \"" + exce.getLocalizedMessage() + "\"}";
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
    @RequestMapping(value = "/user/{profileId}/auth/{authKey}.xml", method = RequestMethod.GET, produces="application/xml")
    public @ResponseBody
    LandingView loadRest(@PathVariable String profileId, @PathVariable String authKey) {
        DateTime time = DateUtil.now();
        log.info("Web Service : " + profileId);
        return landingView(profileId, authKey, time);
    }

    /**
     * Provides user information of home page through a JSON URL
     *
     * @param profileId
     * @param authKey
     * @return
     */
    @RequestMapping(value = "/user/{profileId}/auth/{authKey}.json", method = RequestMethod.GET, produces="application/json")
    public @ResponseBody
    String loadJSON(@PathVariable String profileId, @PathVariable String authKey) {
        DateTime time = DateUtil.now();
        log.info("JSON : " + profileId);
        Base landingView = landingView(profileId, authKey, time);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();

        String json = "";
        try {
            json = ow.writeValueAsString(landingView);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }

        return json;
    }

    /**
     * Provides user information of home page through a JSON URL
     *
     * @param profileId
     * @param authKey
     * @return
     */
    @RequestMapping(value = "/user/{profileId}/auth/{authKey}.htm", method = RequestMethod.GET, produces="text/html")
    public @ResponseBody
    String loadHTML(@PathVariable String profileId, @PathVariable String authKey) {
        DateTime time = DateUtil.now();
        log.info("HTML : " + profileId);
        LandingView landingView = landingView(profileId, authKey, time);
        String html = landingViewService.landingViewHTMLString(landingView);
        return html;
    }

    /**
     * Populate Landing View object
     *
     * @param profileId
     * @param authKey
     * @param time
     * @return
     */
    private LandingView landingView(String profileId, String authKey, DateTime time) {
        UserProfileEntity userProfile = authenticate(profileId, authKey);
        if(userProfile != null) {
            long pendingCount = landingService.pendingReceipt(profileId);
            List<ReceiptEntity> receipts = landingService.getAllReceiptsForThisMonth(profileId, DateUtil.now());

            LandingView landingView = LandingView.newInstance(userProfile.getId(), userProfile.getEmailId(), Header.newInstance(getAuth(profileId)));
            landingView.setPendingCount(pendingCount);
            landingView.setReceipts(receipts);
            landingView.setStatus(Header.RESULT.SUCCESS);

            log.info("Rest/JSON Service returned : " + profileId + ", Email ID: " + userProfile.getEmailId());

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), true);
            return landingView;
        } else {
            Header header = getHeaderForProfileOrAuthFailure();
            LandingView landingView = LandingView.newInstance("", "", header);

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), false);
            return landingView;
        }
    }

    @RequestMapping(value = "/report/{monthYear}", method = RequestMethod.GET)
    public @ResponseBody
    String generateReport(@PathVariable String monthYear, @ModelAttribute UserSession userSession) {
        Header header = Header.newInstance(getAuth(userSession.getUserProfileId()));
        String pattern = "MMM, yyyy";
        try {
            DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
            DateTime dateTime = dtf.parseDateTime(monthYear);
            dateTime = dateTime.plusMonths(1).minusDays(1);

            header.setStatus(Header.RESULT.SUCCESS);
            return reportService.monthlyReport(dateTime,
                    userSession.getUserProfileId(),
                    userSession.getEmailId(),
                    header
            );
        } catch(IllegalArgumentException iae) {
            header.setMessage("Invalid parameter. Correct format - " + pattern + " [Please provide parameter shown without quotes - 'Jan, 2013']");
            header.setStatus(Header.RESULT.FAILURE);

            return reportService.monthlyReport(DateTime.now().minusYears(40),
                    userSession.getUserProfileId(),
                    userSession.getEmailId(),
                    header
            );
        }
    }

    /* http://stackoverflow.com/questions/12117799/spring-mvc-ajax-form-post-handling-possible-methods-and-their-pros-and-cons */
    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    public @ResponseBody
    String invite(@RequestParam(value="emailId") String emailId, @ModelAttribute UserSession userSession) {
        //Always lower case the email address
        emailId = StringUtils.lowerCase(emailId);

        log.info("Invitation being sent to: " + emailId);

        boolean isValid = EmailValidator.getInstance().isValid(emailId);
        if(isValid) {
            UserProfileEntity userProfileEntity = accountService.findIfUserExists(emailId);
            /**
             * Condition when the user does not exists then invite. Also allow re-invite if the user is not active and
             * is not deleted. The second condition could result in a bug when administrator has made the user inactive.
             * Best solution is to add automated re-invite using quartz/cron job. Make sure there is a count kept to limit
             * the number of invite.
             */
            if(userProfileEntity == null || (!userProfileEntity.isActive() && !userProfileEntity.isDeleted())) {
                boolean status;
                if(userProfileEntity == null) {
                    status = mailService.sendInvitation(emailId, userSession.getEmailId());
                } else {
                    status = mailService.reSendInvitation(emailId, userSession.getEmailId());
                }
                if(status) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invitation sent to '").append(emailId).append("'");
                    notificationService.addNotification(sb.toString(), NotificationTypeEnum.MESSAGE, userSession.getUserProfileId());
                    return "Invitation Sent to: " + emailId;
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Unsuccessful in sending invitation to '").append(emailId).append("'");
                    notificationService.addNotification(sb.toString(), NotificationTypeEnum.MESSAGE, userSession.getUserProfileId());
                    return "Unsuccessful in sending invitation: " + emailId;
                }
            } else if(userProfileEntity.isActive() && !userProfileEntity.isDeleted()) {
                log.info(emailId + ", already registered. Thanks!");
                return emailId + ", already registered. Thanks!";
            } else if(userProfileEntity.isDeleted()) {
                log.info(emailId + ", already registered but no longer with us. Appreciate!");

                //Have to send a positive message
                return emailId + ", already invited. Appreciate!";
            } else {
                log.info(emailId + ", already invited. Thanks!");
                // TODO can put a condition to check or if user is still in invitation mode or has completed registration
                // TODO Based on either condition we can let user recover password or re-send invitation
                return emailId + ", already invited. Thanks!";
            }
        } else {
            return "Invalid Email: " + emailId;
        }
    }
}
