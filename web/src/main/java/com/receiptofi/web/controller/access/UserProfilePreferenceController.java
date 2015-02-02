/**
 *
 */
package com.receiptofi.web.controller.access;

import com.receiptofi.domain.EmailValidateEntity;
import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.EmailValidateService;
import com.receiptofi.service.ItemService;
import com.receiptofi.service.MailService;
import com.receiptofi.service.UserProfilePreferenceService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.web.form.ExpenseTypeForm;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Value ("${mail.validation.fail.period}")
    private int mailValidationFailPeriod;

    @Autowired private UserProfilePreferenceService userProfilePreferenceService;
    @Autowired private AccountService accountService;
    @Autowired private ItemService itemService;
    @Autowired private ExpenseTagValidator expenseTagValidator;
    @Autowired private ProfileValidator profileValidator;
    @Autowired private MailService mailService;
    @Autowired private EmailValidateService emailValidateService;

    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (value = "/i", method = RequestMethod.GET)
    public ModelAndView loadForm(
            @ModelAttribute ("expenseTypeForm")
            ExpenseTypeForm expenseTypeForm,

            Model model
    ) throws IOException {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ModelAndView modelAndView;
        ProfileForm profileForm = null;

        /** Gymnastic to show BindingResult errors if any. */
        if (model.asMap().containsKey("result")) {
            modelAndView = new ModelAndView(nextPage);

            BeanPropertyBindingResult result = (BeanPropertyBindingResult) model.asMap().get("result");
            if (result.getObjectName().equals("expenseTypeForm")) {
                model.addAttribute("org.springframework.validation.BindingResult.expenseTypeForm", result);

                profileForm = ProfileForm.newInstance(userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(receiptUser.getRid()));
                populateProfileForm(profileForm, receiptUser.getRid());

                modelAndView.addObject("profileForm", profileForm);
            }

            if (result.getObjectName().equals("profileForm")) {
                model.addAttribute("org.springframework.validation.BindingResult.profileForm", result);

                profileForm = (ProfileForm) result.getTarget();
                populateProfileForm(profileForm, receiptUser.getRid());

                /** Since we do not plan to lose profileForm from result we need to set some other values for tab 3. */
                ProfileForm profile = ProfileForm.newInstance(userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(receiptUser.getRid()));
                profileForm.setLevel(profile.getLevel());
                profileForm.setActive(profile.isActive());

                modelAndView.addObject("profileForm", profileForm);
            }
        } else {
            profileForm = (ProfileForm) model.asMap().get("profileForm");
            if (profileForm == null) {
                profileForm = ProfileForm.newInstance(userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(receiptUser.getRid()));
            }
            modelAndView = populateModel(nextPage, expenseTypeForm, profileForm, receiptUser.getRid());
        }

        setAccountValidationInfo(receiptUser, profileForm);
        return modelAndView;
    }

    /**
     * Sets account with validation info if account has not be validated.
     *
     * @param receiptUser
     * @param profileForm
     */
    private void setAccountValidationInfo(ReceiptUser receiptUser, ProfileForm profileForm) {
        if (null != profileForm && !receiptUser.isAccountValidated()) {
            UserAccountEntity userAccountEntity = accountService.findByReceiptUserId(receiptUser.getRid());

            profileForm.setAccountValidationExpireDay(
                    DateUtil.toDateTime(userAccountEntity.getAccountValidatedBeginDate())
                            .plusDays(mailValidationFailPeriod).toDate());

            profileForm.setAccountValidationExpired(
                    Days.daysBetween(
                            new LocalDate(userAccountEntity.getAccountValidatedBeginDate()),
                            new LocalDate(new Date())
                    ).isGreaterThan(Days.days(mailValidationFailPeriod)));
        }
    }

    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (value = "/i", method = RequestMethod.POST, params = "profile_update")
    public String updateProfile(
            @ModelAttribute ("expenseTypeForm")
            ExpenseTypeForm expenseTypeForm,

            //@Valid
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
            LOG.error("validation error");
            redirectAttrs.addFlashAttribute("result", result);
            /** Re-direct to prevent resubmit. */
            return "redirect:/access" + nextPage + "/i" + ".htm";
        }

        try {
            UserProfileEntity userProfile = userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(receiptUser.getRid());
            if (null == userProfile.getProviderId()) {

                /** Can incorporate condition in profileForm if its dirty object instead. */
                changeProfileDetails(profileForm, receiptUser, userProfile);
                changeEmail(profileForm, receiptUser, userProfile);

                redirectAttrs.addFlashAttribute("profileForm", profileForm);
            }
        } catch (Exception e) {
            LOG.error("Error updating profile={} reason={}", receiptUser.getRid(), e.getLocalizedMessage(), e);
            result.rejectValue("tagName", StringUtils.EMPTY, e.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("result", result);
        }

        /** Re-direct to prevent resubmit. */
        return "redirect:/access" + nextPage + "/i" + ".htm";
    }

    private void changeProfileDetails(ProfileForm profileForm, ReceiptUser receiptUser, UserProfileEntity userProfile) {
        if (!profileForm.getFirstName().equals(userProfile.getFirstName()) ||
                !profileForm.getLastName().equals(userProfile.getLastName())) {
            accountService.updateName(profileForm.getFirstName(), profileForm.getLastName(), receiptUser.getRid());
        }
    }

    private void changeEmail(ProfileForm profileForm, ReceiptUser receiptUser, UserProfileEntity userProfile) {
        if (!userProfile.getEmail().equalsIgnoreCase(profileForm.getMail())) {
            UserAccountEntity userAccount = accountService.updateUID(receiptUser.getUsername(), profileForm.getMail(), receiptUser.getRid());

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
                                mailValidationFailPeriod + " days. After logout, you will need your new email " +
                                "address to log back in.");
                profileForm.setUpdated(userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(receiptUser.getRid()).getUpdated());
            } else {
                profileForm.setErrorMessage("Account with similar email address already exists. " +
                        "Submitted address " + profileForm.getMail() + ". " +
                        "If you have lost your password, then please try password recovery option.");
                profileForm.setMail(userProfile.getEmail());
            }
        }
    }

    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (value = "/i", method = RequestMethod.POST, params = "expense_tag_delete")
    public String deleteExpenseTag(
            @ModelAttribute ("expenseTypeForm")
            ExpenseTypeForm expenseTypeForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        /** There is UI logic based on this. Set the right to be active when responding. */
        redirectAttrs.addFlashAttribute("showTab", "#tabs-2");

        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        expenseTagValidator.validate(expenseTypeForm, result);
        if (result.hasErrors()) {
            LOG.error("validation error");
            redirectAttrs.addFlashAttribute("result", result);
            /** Re-direct to prevent resubmit. */
            return "redirect:/access" + nextPage + "/i" + ".htm";
        }

        try {
            long count = itemService.countItemsUsingExpenseType(expenseTypeForm.getTagId(), receiptUser.getRid());
            if (0 == count) {
                userProfilePreferenceService.deleteExpenseTag(
                        expenseTypeForm.getTagId(),
                        expenseTypeForm.getTagName(),
                        expenseTypeForm.getTagColor(),
                        receiptUser.getRid()
                );
            } else {
                result.rejectValue(
                        "tagName",
                        StringUtils.EMPTY,
                        "Cannot delete " + expenseTypeForm.getTagName() + " as it is being used by at least " + count + " document(s)");

                redirectAttrs.addFlashAttribute("result", result);
            }
        } catch (Exception e) {
            LOG.error("Error saving expenseTag={} reason={}", expenseTypeForm.getTagName(), e.getLocalizedMessage(), e);
            result.rejectValue("tagName", StringUtils.EMPTY, e.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("result", result);
        }

        /** Re-direct to prevent resubmit. */
        return "redirect:/access" + nextPage + "/i" + ".htm";
    }

    /**
     * Used for adding Expense Type
     * Note: Gymnastic : The form that is being posted should be the last in order. Or else validation fails to work
     *
     * @param expenseTypeForm
     * @param result
     * @return
     */
    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (value = "/i", method = RequestMethod.POST, params = "expense_tag_save_update")
    public String addExpenseTag(
            @ModelAttribute ("profileForm")
            ProfileForm profileForm,

            @ModelAttribute ("expenseTypeForm")
            ExpenseTypeForm expenseTypeForm,

            BindingResult result,
            RedirectAttributes redirectAttrs
    ) {
        /** There is UI logic based on this. Set the right to be active when responding. */
        redirectAttrs.addFlashAttribute("showTab", "#tabs-2");

        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        expenseTagValidator.validate(expenseTypeForm, result);
        if (result.hasErrors()) {
            LOG.error("validation error");
            redirectAttrs.addFlashAttribute("result", result);
            /** Re-direct to prevent resubmit. */
            return "redirect:/access" + nextPage + "/i" + ".htm";
        }

        try {
            if (StringUtils.isBlank(expenseTypeForm.getTagId())) {
                if (expenseTagCountMax > userProfilePreferenceService.allExpenseTypes(receiptUser.getRid()).size()) {
                    ExpenseTagEntity expenseTag = ExpenseTagEntity.newInstance(
                            expenseTypeForm.getTagName(),
                            receiptUser.getRid(),
                            expenseTypeForm.getTagColor());

                    userProfilePreferenceService.saveExpenseTag(expenseTag);
                } else {
                    result.rejectValue("tagName",
                            StringUtils.EMPTY,
                            "Maximum number of TAG(s) allowed " +
                                    expenseTagCountMax +
                                    ". Could not add " +
                                    expenseTypeForm.getTagName() +
                                    "."
                    );
                    redirectAttrs.addFlashAttribute("result", result);
                }
            } else {
                userProfilePreferenceService.updateExpenseTag(
                        expenseTypeForm.getTagId(),
                        expenseTypeForm.getTagName(),
                        expenseTypeForm.getTagColor(),
                        receiptUser.getRid()
                );
            }
        } catch (Exception e) {
            LOG.error("Error saving expenseTag={} reason={}", expenseTypeForm.getTagName(), e.getLocalizedMessage(), e);
            result.rejectValue("tagName", StringUtils.EMPTY, e.getLocalizedMessage());
            redirectAttrs.addFlashAttribute("result", result);
        }

        /** Re-direct to prevent resubmit. */
        return "redirect:/access" + nextPage + "/i" + ".htm";
    }

    /**
     * Only admin has access to this link. Others get 403 error.
     *
     * @param rid
     * @param expenseTypeForm
     * @return
     * @throws IOException
     */
    @PreAuthorize ("hasRole('ROLE_ADMIN')")
    @RequestMapping (value = "/their", method = RequestMethod.GET)
    public ModelAndView userStatus(
            @RequestParam ("id")
            String rid,

            @ModelAttribute ("expenseTypeForm")
            ExpenseTypeForm expenseTypeForm,

            Model model
    ) throws IOException {
        ModelAndView modelAndView;
        ProfileForm profileForm = (ProfileForm) model.asMap().get("profileForm");
        if (null == profileForm) {
            profileForm = ProfileForm.newInstance(userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(rid));
            modelAndView = populateModel(nextPage, expenseTypeForm, profileForm, rid);
        } else {
            populateProfileForm(profileForm, rid);

            /** Since we do not plan to lose profileForm from result we need to set some other values for tab 1. */
            ProfileForm profile = ProfileForm.newInstance(userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(rid));
            profileForm.setFirstName(profile.getFirstName());
            profileForm.setLastName(profile.getLastName());
            profileForm.setMail(profile.getMail());
            profileForm.setUpdated(profile.getUpdated());

            modelAndView = populateModel(nextPage, expenseTypeForm, profileForm, rid);
        }

        //There is UI logic based on this. Set the right to be active when responding.
        modelAndView.addObject("showTab", "#tabs-3");
        return modelAndView;
    }

    /**
     * Only Admin can update the user level. Others get 403 error. If the user cannot access /their, then its highly
     * unlikely to perform the action below.
     *
     * @param expenseTypeForm
     * @return
     * @throws IOException
     */
    @PreAuthorize ("hasAnyRole('ROLE_ADMIN')")
    @RequestMapping (value = "/update", method = RequestMethod.POST)
    public String userStatusUpdate(
            @ModelAttribute ("expenseTypeForm")
            ExpenseTypeForm expenseTypeForm,

            @ModelAttribute ("profileForm")
            ProfileForm profileForm,

            RedirectAttributes redirectAttrs
    ) throws IOException {

        UserProfileEntity userProfile = userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(profileForm.getRid());
        userProfile.setLevel(profileForm.getLevel());
        if (!profileForm.isActive() || !userProfile.isActive()) {
            if (profileForm.isActive()) {
                userProfile.active();
            } else {
                userProfile.inActive();
            }
        }

        UserAccountEntity userAccount = accountService.changeAccountRolesToMatchUserLevel(
                userProfile.getReceiptUserId(),
                userProfile.getLevel()
        );

        try {
            accountService.saveUserAccount(userAccount);
            userProfilePreferenceService.updateProfile(userProfile);
            profileForm.setSuccessMessage("Updated profile " + userProfile.getReceiptUserId() + " successfully.");
        } catch (Exception exce) {
            //XXX todo should there be two phase commit
            LOG.error("Failed updating User Profile, rid={}", userProfile.getReceiptUserId(), exce);
            profileForm.setErrorMessage("Failed updating profile " + userProfile.getReceiptUserId() + ", reason: " + exce.getLocalizedMessage());
        }
        redirectAttrs.addFlashAttribute("profileForm", profileForm);
        return "redirect:/access" + nextPage + "/their" + ".htm?id=" + userProfile.getReceiptUserId();
    }

    /**
     * @param nextPage
     * @param expenseTypeForm
     * @param profileForm
     * @return
     */
    private ModelAndView populateModel(String nextPage, ExpenseTypeForm expenseTypeForm, ProfileForm profileForm, String rid) {
        ModelAndView modelAndView = new ModelAndView(nextPage);
        modelAndView.addObject("profileForm", profileForm);
        modelAndView.addObject("expenseTypeForm", expenseTypeForm);

        populateProfileForm(profileForm, rid);
        return modelAndView;
    }

    private void populateProfileForm(ProfileForm profileForm, String rid) {
        List<ExpenseTagEntity> expenseTypes = userProfilePreferenceService.allExpenseTypes(rid);
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
}
