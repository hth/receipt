/**
 *
 */
package com.receiptofi.web.controller.access;

import com.google.gson.JsonObject;

import com.receiptofi.domain.BillingAccountEntity;
import com.receiptofi.domain.EmailValidateEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.AdminService;
import com.receiptofi.service.BillingService;
import com.receiptofi.service.EmailValidateService;
import com.receiptofi.service.ExpensesService;
import com.receiptofi.service.FileSystemService;
import com.receiptofi.service.ItemService;
import com.receiptofi.service.MailService;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.service.UserProfilePreferenceService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.ParseJsonStringToMap;
import com.receiptofi.utils.ScrubbedInput;
import com.receiptofi.web.form.BillingForm;
import com.receiptofi.web.form.ExpenseTagForm;
import com.receiptofi.web.form.ProfileForm;
import com.receiptofi.web.validator.ExpenseTagValidator;
import com.receiptofi.web.validator.ProfileValidator;

import org.apache.commons.lang3.StringUtils;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * Note: Follow PRG model with support for result binding
 *
 * @author hitender
 * @since Jan 14, 2013 11:06:41 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access/userprofilepreference")
public class UserProfilePreferenceController {
    private static final Logger LOG = LoggerFactory.getLogger(UserProfilePreferenceController.class);

    @Value ("${UserProfilePreferenceController.nextPage:/userprofilepreference}")
    private String nextPage;

    @Value ("${UserProfilePreferenceController.ExpenseTagCountMax:5}")
    private int expenseTagCountMax;

    @Value ("${UserProfilePreferenceController.ExpenseTagSize:12}")
    private int expenseTagSize;

    @Value ("${mail.validation.timeout.period}")
    private int mailValidationTimeoutPeriod;

    @Autowired private UserProfilePreferenceService userProfilePreferenceService;
    @Autowired private AccountService accountService;
    @Autowired private ItemService itemService;
    @Autowired private ExpenseTagValidator expenseTagValidator;
    @Autowired private ProfileValidator profileValidator;
    @Autowired private MailService mailService;
    @Autowired private EmailValidateService emailValidateService;
    @Autowired private FileSystemService fileSystemService;
    @Autowired private BillingService billingService;
    @Autowired private ExpensesService expensesService;
    @Autowired private ReceiptService receiptService;
    @Autowired private AdminService adminService;

    @PreAuthorize ("hasAnyRole('ROLE_USER', 'ROLE_BUSINESS', 'ROLE_ENTERPRISE')")
    @RequestMapping (value = "/i", method = RequestMethod.GET)
    public String loadAccount(
            @ModelAttribute ("profileForm")
            ProfileForm profileForm,

            @ModelAttribute ("expenseTagForm")
            ExpenseTagForm expenseTagForm,

            @ModelAttribute ("billingForm")
            BillingForm billingForm,

            ModelMap model
    ) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        /** Gymnastic to show BindingResult errors if any. */
        if (model.containsKey("result")) {
            BeanPropertyBindingResult result = (BeanPropertyBindingResult) model.get("result");
            if ("expenseTagForm".equals(result.getObjectName())) {
                model.addAttribute("org.springframework.validation.BindingResult.expenseTagForm", result);

                populateProfile(profileForm, receiptUser.getRid());
                populateExpenseTag(profileForm, receiptUser.getRid());
                populateBilling(billingForm, receiptUser.getRid());

                model.addAttribute("profileForm", profileForm);
            }

            if ("profileForm".equals(result.getObjectName())) {
                model.addAttribute("org.springframework.validation.BindingResult.profileForm", result);

                profileForm = (ProfileForm) result.getTarget();

                /** Since we do not plan to lose profileForm from result we need to set some other values for tab 3. */
                UserProfileEntity userProfile = userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(receiptUser.getRid());
                profileForm.setLevel(userProfile.getLevel());
                profileForm.setActive(userProfile.isActive());

                populateExpenseTag(profileForm, receiptUser.getRid());
                populateBilling(billingForm, receiptUser.getRid());

                model.addAttribute("profileForm", profileForm);
                model.addAttribute("expenseTagForm", expenseTagForm);
            }
        } else {
            populateProfile(profileForm, receiptUser.getRid());
            populateExpenseTag(profileForm, receiptUser.getRid());
            populateBilling(billingForm, receiptUser.getRid());
        }

        setAccountValidationInfo(receiptUser.getRid(), profileForm);
        return nextPage;
    }

    @PreAuthorize ("hasAnyRole('ROLE_USER', 'ROLE_BUSINESS', 'ROLE_ENTERPRISE')")
    @RequestMapping (value = "/i", method = RequestMethod.POST, params = "profile_update")
    public String updateProfile(
            @ModelAttribute ("expenseTagForm")
            ExpenseTagForm expenseTagForm,

            @ModelAttribute ("profileForm")
            ProfileForm profileForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        /** There is UI logic based on this. Set the right to be active when responding. */
        redirectAttrs.addFlashAttribute("showTab", "#tabs-1");

        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        profileValidator.validate(profileForm, result);
        if (result.hasErrors()) {
            LOG.warn("validation fail");
            redirectAttrs.addFlashAttribute("result", result);
            /** Re-direct to prevent resubmit. */
            return "redirect:/access" + nextPage + "/i" + ".htm";
        }

        try {
            UserProfileEntity userProfile = userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(receiptUser.getRid());
            if (null == userProfile.getProviderId()) {

                if (receiptUser.getPid() == null) {
                    /** Can incorporate condition in profileForm if its dirty object instead. */
                    changeProfileDetails(profileForm, receiptUser, userProfile);
                    changeEmail(profileForm, receiptUser, userProfile);
                    profileForm.setSuccessMessage("Profile updated successfully.");
                } else {
                    /**
                     * Should not be able to change mail id or profile information for accounts registered through
                     * social account. Final fall back. Should be taken care on front end.
                     */
                    LOG.error("Social user={} rid={} tried changing profile information. This should never happen.",
                            receiptUser.getRid(),
                            receiptUser.getUsername());
                    profileForm.setErrorMessage("Cannot change email for social login.");
                }

                redirectAttrs.addFlashAttribute("profileForm", profileForm);
            }
        } catch (Exception e) {
            LOG.error("Error updating profile={} reason={}", receiptUser.getRid(), e.getLocalizedMessage(), e);
            result.rejectValue("tagName", "", e.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("result", result);
        }

        /** Re-direct to prevent resubmit. */
        return "redirect:/access" + nextPage + "/i" + ".htm";
    }

    /**
     * Used for adding Expense Type
     * Note: Gymnastic : The form that is being posted should be the last in order. Or else validation fails to work
     *
     * @param expenseTagForm
     * @param result
     * @return
     */
    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (value = "/i", method = RequestMethod.POST, params = "expense_tag_save_update")
    public String addExpenseTag(
            @ModelAttribute ("profileForm")
            ProfileForm profileForm,

            @ModelAttribute ("billingForm")
            BillingForm billingForm,

            @ModelAttribute ("expenseTagForm")
            ExpenseTagForm expenseTagForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        /** There is UI logic based on this. Set the right to be active when responding. */
        redirectAttrs.addFlashAttribute("showTab", "#tabs-2");

        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        expenseTagValidator.validate(expenseTagForm, result);
        if (result.hasErrors()) {
            LOG.warn("validation fail");
            redirectAttrs.addFlashAttribute("result", result);
            /** Re-direct to prevent resubmit. */
            return "redirect:/access" + nextPage + "/i" + ".htm";
        }

        try {
            if (StringUtils.isBlank(expenseTagForm.getTagId())) {
                if (expenseTagCountMax > expensesService.getExpenseTags(receiptUser.getRid()).size()) {
                    ExpenseTagEntity expenseTag = ExpenseTagEntity.newInstance(
                            expenseTagForm.getTagName(),
                            receiptUser.getRid(),
                            expenseTagForm.getTagColor());

                    expensesService.saveExpenseTag(expenseTag);
                } else {
                    result.rejectValue("tagName",
                            "",
                            "Maximum number of TAG(s) allowed " +
                                    expenseTagCountMax +
                                    ". Could not add " +
                                    expenseTagForm.getTagName() +
                                    "."
                    );
                    redirectAttrs.addFlashAttribute("result", result);
                }
            } else {
                expensesService.updateExpenseTag(
                        expenseTagForm.getTagId(),
                        expenseTagForm.getTagName(),
                        expenseTagForm.getTagColor(),
                        receiptUser.getRid()
                );
            }
        } catch (Exception e) {
            LOG.error("Error saving expenseTag={} reason={}", expenseTagForm.getTagName(), e.getLocalizedMessage(), e);
            result.rejectValue("tagName", "", e.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("result", result);
        }

        /** Re-direct to prevent resubmit. */
        return "redirect:/access" + nextPage + "/i" + ".htm";
    }

    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (
            value = "/deleteExpenseTag",
            method = RequestMethod.POST,
            headers = "Accept=" + MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    @ResponseBody
    public String deleteExpenseTag(
            @RequestBody
            String expenseTagDetail
    ) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, ScrubbedInput> map = ParseJsonStringToMap.jsonStringToMap(expenseTagDetail);
        String tagId = map.get("tagId").getText();
        String tagName = map.get("tagName").getText();

        boolean action = false;
        StringBuilder message = new StringBuilder();
        ExpenseTagEntity expenseTag = expensesService.getExpenseTag(receiptUser.getRid(), tagId);
        if (expenseTag != null) {
            long tagItemCount = itemService.countItemsUsingExpenseType(
                    tagId,
                    receiptUser.getRid());

            long tagReceiptCount = receiptService.countReceiptsUsingExpenseType(
                    tagId,
                    receiptUser.getRid());

            action = expensesService.softDeleteExpenseTag(
                    tagId,
                    tagName,
                    receiptUser.getRid()
            );

            if (action) {
                message.append("Deleted Expense Tag: ").append(tagName).append(" successfully. ");
                if (tagReceiptCount > 0) {
                    message.append("Removed expense tag from ").append(tagReceiptCount).append(" receipt(s). ");
                    if (tagItemCount > 0) {
                        message.append("And, removed expense tag from ").append(tagItemCount).append(" item(s). ");
                    }
                } else if (tagItemCount > 0) {
                    message.append("Removed expense tag from ").append(tagItemCount).append(" item(s). ");
                }
            } else {
                message.append("Failed to delete Expense Tag: ").append(tagName);
            }
        } else {
            message.append("Expense Tag ").append(tagName).append(" not found. ");
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("result", action);
        jsonObject.addProperty("message", message.toString());
        return jsonObject.toString();
    }

    /**
     * Only admin has access to this link. Others get 403 error.
     *
     * @param rid
     * @param profileForm
     * @param expenseTagForm
     * @param billingForm
     * @param model
     * @return
     * @throws IOException
     */
    @PreAuthorize ("hasRole('ROLE_ADMIN')")
    @RequestMapping (value = "/their", method = RequestMethod.GET)
    public String adminGetUserStatus(
            @RequestParam ("id")
            ScrubbedInput rid,

            @ModelAttribute ("profileForm")
            ProfileForm profileForm,

            @ModelAttribute ("expenseTagForm")
            ExpenseTagForm expenseTagForm,

            @ModelAttribute ("billingForm")
            BillingForm billingForm,

            Model model
    ) throws IOException {
        /** Since we do not plan to lose profileForm from result we need to set some other values for tab 1. */
        populateProfile(profileForm, rid.getText());
        populateExpenseTag(profileForm, rid.getText());
        populateBilling(billingForm, rid.getText());
        setAccountValidationInfo(rid.getText(), profileForm);

        //There is UI logic based on this. Set the right to be active when responding.
        model.addAttribute("showTab", "#tabs-4");
        return nextPage;
    }

    /**
     * Only Admin can update the user level. Others get 403 error. If the user cannot access /their, then its highly
     * unlikely to perform the action below.
     *
     * @param expenseTagForm
     * @param profileForm
     * @return
     * @throws IOException
     */
    @PreAuthorize ("hasRole('ROLE_ADMIN')")
    @RequestMapping (value = "/update", method = RequestMethod.POST)
    public String adminUpdateUserStatus(
            @ModelAttribute ("expenseTagForm")
            ExpenseTagForm expenseTagForm,

            @ModelAttribute ("profileForm")
            ProfileForm profileForm,

            @ModelAttribute ("billingForm")
            BillingForm billingForm
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean changed = adminService.changeUserLevel(
                receiptUser.getRid(),
                profileForm.getRid(),
                profileForm.getLevel(),
                profileForm.isActive()
        );

        if (changed) {
            profileForm.setSuccessMessage("Updated profile " + profileForm.getRid() + " successfully.");
        } else {
            profileForm.setErrorMessage("Failed updating profile " + profileForm.getRid() + ", reason: Failed");
        }
        return "redirect:/access" + nextPage + "/their" + ".htm?id=" + profileForm.getRid();
    }

    /**
     * Set Expense Tag information.
     *
     * @param profileForm
     * @param rid
     */
    private void populateExpenseTag(ProfileForm profileForm, String rid) {
        List<ExpenseTagEntity> expenseTypes = expensesService.getExpenseTags(rid);
        profileForm.setExpenseTags(expenseTypes);

        Map<String, Long> expenseTagWithCount = new HashMap<>();
        for (ExpenseTagEntity expenseType : expenseTypes) {
            expenseTagWithCount.put(
                    expenseType.getTagName(),
                    itemService.countItemsUsingExpenseType(expenseType.getId(), rid)
            );
        }

        profileForm.setExpenseTagCount(expenseTagWithCount);
    }

    private void populateProfile(ProfileForm profileForm, String rid) {
        UserProfileEntity userProfile = userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(rid);

        profileForm.setFirstName(new ScrubbedInput(userProfile.getFirstName()));
        profileForm.setLastName(new ScrubbedInput(userProfile.getLastName()));
        profileForm.setMail(new ScrubbedInput(userProfile.getEmail()));
        profileForm.setUpdated(userProfile.getUpdated());
        profileForm.setLevel(userProfile.getLevel());
        profileForm.setActive(userProfile.isActive());
        profileForm.setRid(userProfile.getReceiptUserId());
    }

    /**
     * Sets account with validation info if account has not be validated.
     *
     * @param rid
     * @param profileForm not null
     */
    private void setAccountValidationInfo(
            String rid,

            @NotNull
            ProfileForm profileForm
    ) {
        Assert.notNull(profileForm);
        UserAccountEntity userAccount = accountService.findByReceiptUserId(rid);
        if (null != userAccount) {
            if (!userAccount.isAccountValidated()) {
                profileForm.setAccountValidationExpireDay(
                        DateUtil.toDateTime(userAccount.getAccountValidatedBeginDate())
                                .plusDays(mailValidationTimeoutPeriod).toDate());

                profileForm.setAccountValidationExpired(
                        Days.daysBetween(
                                new LocalDate(userAccount.getAccountValidatedBeginDate()),
                                new LocalDate(new Date())
                        ).isGreaterThan(Days.days(mailValidationTimeoutPeriod)));
            } else {
                profileForm.setAccountValidated(userAccount.isAccountValidated());
            }

            profileForm.setProfileImage(userAccount.getImageUrl());
        }
    }

    private BillingForm populateBilling(BillingForm billingForm, String rid) {
        billingForm.setDiskUsage(fileSystemService.diskUsage(rid));
        billingForm.setPendingDiskUsage(fileSystemService.filesPendingDiskUsage(rid));
        billingForm.setBillings(billingService.getHistory(rid));

        BillingAccountEntity billingAccount = billingService.getBillingAccount(rid);
        billingForm.setBillingPlan(billingAccount.getBillingPlan());
        return billingForm;
    }

    private void changeProfileDetails(ProfileForm profileForm, ReceiptUser receiptUser, UserProfileEntity userProfile) {
        if (!profileForm.getFirstName().getText().equals(userProfile.getFirstName()) ||
                !profileForm.getLastName().getText().equals(userProfile.getLastName())) {
            accountService.updateName(
                    profileForm.getFirstName().getText(),
                    profileForm.getLastName().getText(),
                    receiptUser.getRid());
        }
    }

    private void changeEmail(ProfileForm profileForm, ReceiptUser receiptUser, UserProfileEntity userProfile) {
        if (!userProfile.getEmail().equalsIgnoreCase(profileForm.getMail().getText())) {
            UserAccountEntity userAccount = accountService.updateUID(
                    receiptUser.getUsername(),
                    profileForm.getMail().getText(),
                    receiptUser.getRid());

            if (userAccount != null) {

                EmailValidateEntity accountValidate = emailValidateService.saveAccountValidate(
                        userAccount.getReceiptUserId(),
                        userAccount.getUserId());

                mailService.accountValidationMail(
                        userAccount.getUserId(),
                        userAccount.getName(),
                        accountValidate.getAuthenticationKey());

                profileForm.setSuccessMessage(
                        "Email updated successfully. " +
                                "Sent validation email at your new email address " + profileForm.getMail() + ". " +
                                "Please validate by clicking on link in email otherwise account will disable in " +
                                mailValidationTimeoutPeriod + " days. After logout, you will need your new email " +
                                "address to log back in.");
                profileForm.setUpdated(userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(receiptUser.getRid()).getUpdated());
            } else {
                profileForm.setErrorMessage("Account with similar email address already exists. " +
                        "Submitted address " + profileForm.getMail() + ". " +
                        "If you have lost your password, then please try password recovery option.");
                profileForm.setMail(new ScrubbedInput(userProfile.getEmail()));
            }
        }
    }
}
