package com.receiptofi.web.controller.access;

import com.google.gson.JsonObject;

import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.FriendService;
import com.receiptofi.web.form.SplitForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * User: hitender
 * Date: 9/14/15 6:40 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access/split")
public class SplitController {

    @Autowired
    private FriendService friendService;

    /**
     * Refers to split.jsp
     */
    @Value ("${nextPage:/split}")
    private String nextPage;

    @Timed
    @ExceptionMetered
    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (method = RequestMethod.GET)
    public String loadForm(@ModelAttribute ("splitForm") SplitForm splitForm) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, List<UserProfileEntity>> profiles = friendService.getProfileForAllFriends(receiptUser.getRid());
        splitForm.setActiveProfiles(profiles.get(FriendService.ACTIVE));
        splitForm.setPendingProfiles(profiles.get(FriendService.PENDING));

        splitForm.setAwaitingProfiles(friendService.getAwaitingConnections(receiptUser.getRid()));

        return nextPage;
    }

    @PreAuthorize ("hasRole('ROLE_USER')")
    @RequestMapping (
            value = "/friend",
            method = RequestMethod.POST,
            headers = "Accept=application/json",
            produces = "application/json"
    )
    @ResponseBody
    public String friendAccept(
            @RequestParam ("id")
            String id,

            @RequestParam ("auth")
            String auth,

            @RequestParam ("friend")
            boolean accept,

            HttpServletResponse httpServletResponse
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean response = friendService.updateResponse(id, auth, accept, receiptUser.getRid());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(LandingController.SUCCESS, response);
        return jsonObject.toString();
    }
}