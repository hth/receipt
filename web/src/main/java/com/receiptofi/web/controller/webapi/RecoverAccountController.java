package com.receiptofi.web.controller.webapi;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Account recovery by invoking email.
 * User: hitender
 * Date: 12/8/14 2:59 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal"
})
@RestController
@RequestMapping (value = "/webapi/mobile/recover")
public class RecoverAccountController {
}
