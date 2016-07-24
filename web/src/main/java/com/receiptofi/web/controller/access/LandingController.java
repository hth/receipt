/**
 *
 */
package com.receiptofi.web.controller.access;

import com.google.gson.JsonObject;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.shared.UploadDocumentImage;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.DocumentOfTypeEnum;
import com.receiptofi.domain.types.DocumentRejectReasonEnum;
import com.receiptofi.domain.types.FileTypeEnum;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.domain.value.ReceiptGrouped;
import com.receiptofi.domain.value.ReceiptGroupedByBizLocation;
import com.receiptofi.service.DocumentUpdateService;
import com.receiptofi.service.FileSystemService;
import com.receiptofi.service.LandingService;
import com.receiptofi.service.MailService;
import com.receiptofi.service.MessageDocumentService;
import com.receiptofi.service.NotificationService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.Maths;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.form.DocumentStatsForm;
import com.receiptofi.web.form.LandingDonutChart;
import com.receiptofi.web.form.LandingForm;
import com.receiptofi.web.form.NotificationForm;
import com.receiptofi.web.helper.ReceiptForMonth;
import com.receiptofi.web.helper.ReceiptLandingView;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hitender
 * @since Dec 17, 2012 3:19:01 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access")
public class LandingController {
    private static final Logger LOG = LoggerFactory.getLogger(LandingController.class);

    public static final String SUCCESS = "success";
    private static final String UPLOAD_MESSAGE = "uploadMessage";
    private static final String FILE_UPLOADED_SUCCESSFULLY = "File uploaded successfully";

    @Value ("${LandingController.calendar.nextPage:/z/landingTabs}")
    private String calendarNextPage;

    @Autowired private LandingService landingService;
    @Autowired private NotificationService notificationService;
    @Autowired private MailService mailService;
    @Autowired private FileSystemService fileSystemService;
    @Autowired private DocumentUpdateService documentUpdateService;
    @Autowired private MessageDocumentService messageDocumentService;

    private static final DateTimeFormatter DTF = DateTimeFormat.forPattern("MMM, yyyy");

    /**
     * Refers to landing.jsp
     */
    @Value ("${nextPage:/landing}")
    private String nextPage;

    @Value ("${duplicate.document.reject.user}")
    private String documentRejectUserId;

    @Value ("${duplicate.document.reject.rid}")
    private String documentRejectRid;

    @Timed
    @ExceptionMetered
    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (
            value = "/landing",
            method = RequestMethod.GET
    )
    public ModelAndView loadForm(
            @ModelAttribute ("uploadDocumentImage")
            UploadDocumentImage uploadReceiptImage,

            @ModelAttribute ("landingForm")
            LandingForm landingForm,

            @ModelAttribute ("documentStatsForm")
            DocumentStatsForm documentStatsForm,

            @ModelAttribute ("notificationForm")
            NotificationForm notificationForm
    ) {
        DateTime time = DateUtil.now();
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LOG.info("LandingController loadForm user={}, rid={}", receiptUser.getUsername(), receiptUser.getRid());

        ModelAndView modelAndView = new ModelAndView(nextPage);

        List<ReceiptEntity> allReceiptsForThisMonth = landingService.getAllReceiptsForThisMonth(receiptUser.getRid(), time);
        ReceiptForMonth receiptForMonth = getReceiptForMonth(allReceiptsForThisMonth, time);
//        modelAndView.addObject("receiptForMonth", receiptForMonth);
        landingForm.setReceiptForMonth(receiptForMonth);

        documentStatsForm.setPendingCount(landingService.pendingReceipt(receiptUser.getRid()));
        documentStatsForm.setRejectedCount(landingService.rejectedReceipt(receiptUser.getRid()));

        /** Receipt grouped by date. */
        LOG.info("Calculating calendar grouped expense");
        Iterator<ReceiptGrouped> receiptGrouped = landingService.getReceiptGroupedByDate(receiptUser.getRid());
        landingForm.setReceiptGrouped(receiptGrouped);

//        /** Lists all the receipt grouped by months */
//        List<ReceiptGrouped> receiptGroupedByMonth = landingService.getReceiptGroupedByMonth(receiptUser.getRid());
//        modelAndView.addObject("months", landingService.addMonthsIfLessThanThree(receiptGroupedByMonth));
//        landingForm.setReceiptGroupedByMonths(receiptGroupedByMonth);

        /** Make Map available to all the users. */
        List<ReceiptGroupedByBizLocation> receiptGroupedByBizLocations = landingService.getAllObjectsGroupedByBizLocation(receiptUser.getRid());
        landingForm.setReceiptGroupedByBizLocations(receiptGroupedByBizLocations);

        /** Used for donut chart of each receipts with respect to expense types in TAB 1. */
        LOG.info("Calculating Donut chart - receipt expense");
        /** bizNames and bizByExpenseTypes added below to landingForm. */
        populateReceiptExpenseDonutChartDetails(landingForm, allReceiptsForThisMonth);

        /** Notification. */
        notificationForm.setNotifications(notificationService.getNotifications(receiptUser.getRid()));
        notificationForm.setCount(Long.toString(notificationService.notificationCount(receiptUser.getRid())));
        return modelAndView;
    }

    /**
     * Loads monthly data for the selected month in the calendar.
     *
     * @param monthView
     * @return
     * @throws IOException
     */
    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (
            value = "/landing/monthly_expenses",
            method = RequestMethod.POST
    )
    @ResponseBody
    public ModelAndView monthlyExpenses(
            @RequestParam ("monthView")
            ScrubbedInput monthView,

            @ModelAttribute ("landingForm")
            LandingForm landingForm
    ) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        DateTime monthYear = DTF.parseDateTime(monthView.getText());
        List<ReceiptEntity> allReceiptsForThisMonth = landingService.getAllReceiptsForThisMonth(receiptUser.getRid(), monthYear);
        ReceiptForMonth receiptForMonth = getReceiptForMonth(allReceiptsForThisMonth, monthYear);
        landingForm.setReceiptForMonth(receiptForMonth);

        /** Used for donut chart of each receipts with respect to expense types in TAB 1. */
        LOG.info("Calculating Donut chart - receipt expense");
        /** bizNames and bizByExpenseTypes added below to landingForm. */
        populateReceiptExpenseDonutChartDetails(landingForm, allReceiptsForThisMonth);
        /** Need to return ModelAndView as returning just string does not execute JSP. */
        return new ModelAndView(calendarNextPage);
    }

    /**
     * Populate Receipt expense donut chart.
     *
     * @param landingForm
     * @param receipts
     */
    private void populateReceiptExpenseDonutChartDetails(LandingForm landingForm, List<ReceiptEntity> receipts) {
        List<LandingDonutChart> bizByExpenseTypes = new ArrayList<>();
        StringBuilder bizNames = new StringBuilder();
        Map<String, Map<String, BigDecimal>> bizByExpenseTypeMap = landingService.allBusinessByExpenseType(receipts);
        for (String bizName : bizByExpenseTypeMap.keySet()) {
            //bizNames_sb.append("'").append(StringUtils.abbreviate(bizName, LandingDonutChart.OFF_SET, LandingDonutChart.MAX_WIDTH)).append("',");
            bizNames.append("'").append(bizName).append("',");

            LandingDonutChart landingDonutChart = LandingDonutChart.newInstance(bizName);

            BigDecimal sum = BigDecimal.ZERO;
            Map<String, BigDecimal> map = bizByExpenseTypeMap.get(bizName);
            for (BigDecimal value : map.values()) {
                sum = Maths.add(sum, value);
            }
            landingDonutChart.setTotal(sum);

            StringBuilder expenseTypes = new StringBuilder();
            StringBuilder expenseValues = new StringBuilder();
            for (String name : map.keySet()) {
                expenseTypes.append("'").append(name).append("',");
                expenseValues.append(map.get(name)).append(",");
            }
            landingDonutChart.setExpenseTags(expenseTypes.toString().substring(0, expenseTypes.toString().length() - 1));
            landingDonutChart.setExpenseValues(expenseValues.toString().substring(0, expenseValues.toString().length() - 1));

            bizByExpenseTypes.add(landingDonutChart);
        }

        landingForm.setBizNames(bizNames.toString().substring(0, bizNames.toString().length() > 0 ? bizNames.toString().length() - 1 : 0));
        landingForm.setBizByExpenseTypes(bizByExpenseTypes);
    }

    /**
     * For uploading Receipts.
     *
     * @param httpServletRequest
     * @return
     */
    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (
            method = RequestMethod.POST,
            value = "/landing/upload")
    @ResponseBody
    public String upload(HttpServletRequest httpServletRequest) throws IOException {
        LOG.info("uploading document");

        String rid = ((ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRid();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(SUCCESS, false);

        boolean isMultipart = ServletFileUpload.isMultipartContent(httpServletRequest);
        if (isMultipart) {
            MultipartHttpServletRequest multipartHttpRequest = WebUtils.getNativeRequest(httpServletRequest, MultipartHttpServletRequest.class);
            final List<MultipartFile> files = getMultipartFiles(multipartHttpRequest);

            for (MultipartFile multipartFile : files) {
                UploadDocumentImage image = UploadDocumentImage.newInstance(FileTypeEnum.R)
                        .setFileData(multipartFile)
                        .setRid(rid);
                try {
                    boolean duplicateFile = fileSystemService.fileWithSimilarNameDoesNotExists(rid, image.getOriginalFileName());
                    DocumentEntity document = landingService.uploadDocument(image);

                    if (!duplicateFile) {
                        LOG.info("{} receipt found, delete, name={} rid={}",
                                DocumentRejectReasonEnum.D.getDescription(),
                                image.getOriginalFileName(),
                                rid);

                        messageDocumentService.markMessageForReceiptAsDuplicate(
                                document.getId(),
                                documentRejectUserId,
                                documentRejectRid);

                        documentUpdateService.processDocumentForReject(
                                documentRejectRid,
                                document.getId(),
                                DocumentOfTypeEnum.RECEIPT,
                                DocumentRejectReasonEnum.D);
                    }

                    jsonObject.addProperty(SUCCESS, true);
                    jsonObject.addProperty(UPLOAD_MESSAGE, FILE_UPLOADED_SUCCESSFULLY);
                } catch (Exception exce) {
                    jsonObject.addProperty(SUCCESS, false);
                    jsonObject.addProperty(UPLOAD_MESSAGE, exce.getLocalizedMessage());
                    LOG.error("document upload failed reason={} rid={}", exce.getLocalizedMessage(), rid, exce);
                }
            }
        } else {
            //TODO(hth) test with IE
            //http://skillshared.blogspot.com/2012/08/java-class-for-valums-ajax-file.html
            LOG.warn("Look like IE file upload");
            String filename = httpServletRequest.getHeader("X-File-Name");
            InputStream is = httpServletRequest.getInputStream();
        }
        return jsonObject.toString();
    }

    private List<MultipartFile> getMultipartFiles(MultipartHttpServletRequest multipartHttpRequest) {
        final List<MultipartFile> files = multipartHttpRequest.getFiles("qqfile");

        if (files.isEmpty()) {
            LOG.error("Empty or no document uploaded");
            throw new RuntimeException("Empty or no document uploaded");
        }
        return files;
    }

    /* http://stackoverflow.com/questions/12117799/spring-mvc-ajax-form-post-handling-possible-methods-and-their-pros-and-cons */
    @RequestMapping (
            value = "/landing/invite",
            method = RequestMethod.POST
    )
    @ResponseBody
    public String invite(
            @RequestParam (value = "mail")
            ScrubbedInput mail
    ) {
        //Always lower case the email address
        String invitedUserEmail = mail.getText().toLowerCase();
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LOG.info("Invitation being sent to mail={}", invitedUserEmail);
        return mailService.sendInvite(invitedUserEmail, receiptUser.getRid(), receiptUser.getUsername());
    }

    /**
     * @param allReceiptsForThisMonth
     * @param monthYear
     * @return
     */
    private ReceiptForMonth getReceiptForMonth(List<ReceiptEntity> allReceiptsForThisMonth, DateTime monthYear) {
        ReceiptForMonth receiptForMonth = ReceiptForMonth.newInstance();
        receiptForMonth.setMonthYearDateTime(monthYear);
        for (ReceiptEntity receiptEntity : allReceiptsForThisMonth) {
            receiptForMonth.addReceipt(ReceiptLandingView.newInstance(receiptEntity));
        }
        return receiptForMonth;
    }
}
