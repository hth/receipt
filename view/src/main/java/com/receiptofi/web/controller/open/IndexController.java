package com.receiptofi.web.controller.open;

import com.receiptofi.service.RegistrationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * User: hitender
 * Date: 4/21/14 8:00 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
public class IndexController {
    private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);

    private final RegistrationService registrationService;

    @Autowired
    public IndexController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * isEnabled() false exists when properties registration.turned.on is false and user is trying to gain access
     * or signup through one of the provider. This is last line of defense for user signing in through social provider.
     * <p>
     * During application start up a call is made to show index page. Hence this method and only this controller
     * contains support for request type HEAD.
     * <p>
     * We have added support for HEAD request in filter to prevent failing on HEAD request. As of now there is no valid
     * reason why filter contains this HEAD request as everything is secure after login and there are no bots or
     * crawlers when a valid user has logged in.
     * <p>
     * @see <a href="http://axelfontaine.com/blog/http-head.html">http://axelfontaine.com/blog/http-head.html</a>
     *
     * @param map
     * @return
     */
    @RequestMapping (value = "/open/index", method = {RequestMethod.GET, RequestMethod.HEAD})
    public String index(ModelMap map) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOG.info("Auth {}", authentication.getPrincipal().toString());
        if (authentication instanceof AnonymousAuthenticationToken) {
            return "index";
        }

        if (registrationService.validateIfRegistrationIsAllowed(map, authentication)) {
            return "index";
        }

//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        map.addAttribute("userDetails", userDetails);
        return "redirect:/access/landing.htm";
    }
}
