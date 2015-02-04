package com.receiptofi.web.controller.access;

import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.service.MessageDocumentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Custom logout override spring logout.
 * User: hitender
 * Date: 7/2/13
 * Time: 10:41 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access/signoff")
public class SignOff extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SignOff.class);

    @Autowired MessageDocumentService messageDocumentService;

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        String receiptUserId = "Not Available";
        if (authentication.getPrincipal() != null) {
            ReceiptUser receiptUser = (ReceiptUser) authentication.getPrincipal();
            receiptUserId = receiptUser.getRid();

            /** Only UserLevelEnum.TECHNICIAN and UserLevelEnum.SUPERVISOR has access to update and modify documents. */
            if (receiptUser.getUserLevel() == UserLevelEnum.TECHNICIAN ||
                    receiptUser.getUserLevel() == UserLevelEnum.SUPERVISOR) {
                LOG.info("Reset document pending documents rid={}", receiptUser.getRid());
                messageDocumentService.resetDocumentsToInitialState(receiptUserId);
            }
        }

        LOG.info("Logout user={} from={}", receiptUserId, request.getServletPath());
        super.onLogoutSuccess(request, response, authentication);
    }
}
