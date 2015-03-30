/**
 *
 */
package com.receiptofi.web.controller.admin;

import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.service.AdminLandingService;
import com.receiptofi.web.form.UserSearchForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Redirect to prevent re-submit.
 *
 * @author hitender
 * @since Mar 26, 2013 1:14:24 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/admin")
public class AdminLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminLandingController.class);

    @Value ("${nextPage:/admin/landing}")
    private String nextPage;

    @Autowired private AdminLandingService adminLandingService;

    /**
     * Gymnastic for PRG example.
     *
     * @param userSearchForm
     * @return
     */
    @RequestMapping (value = "/landing", method = RequestMethod.GET)
    public String loadForm(
            @ModelAttribute ("userSearchForm")
            UserSearchForm userSearchForm
    ) {
        return nextPage;
    }

    /**
     * @param name Search for user name.
     * @return
     */
    @RequestMapping (value = "/find_user", method = RequestMethod.GET)
    @ResponseBody
    public List<String> findUser(
            @RequestParam ("term")
            String name
    ) {
        return adminLandingService.findMatchingUsers(name);
    }

    /**
     * Gymnastic for PRG example.
     *
     * @param userSearchForm
     * @return
     */
    @RequestMapping (value = "/landing", method = RequestMethod.POST)
    public String loadUser(
            @ModelAttribute ("userSearchForm")
            UserSearchForm userSearchForm,

            RedirectAttributes redirectAttrs
    ) {
        List<UserProfileEntity> userProfileEntities = adminLandingService.findAllUsers(userSearchForm.getUserName());
        userSearchForm.setUserProfiles(userProfileEntities);
        redirectAttrs.addFlashAttribute("userSearchForm", userSearchForm);
        /** Re-direct to prevent resubmit. */
        return "redirect:" + nextPage + ".htm";
    }
}
