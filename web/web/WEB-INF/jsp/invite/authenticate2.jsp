<%@ include file="../include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="account.invitation.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/css/stylelogin.css'>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>

    <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/login.js"></script>
</head>
<body>
<div class="main_wrapper">
    <div class="header">
        <div class="header_wrapper">
            <div class="header_left_content">
                <div id="logo">
                    <h1 style="font-weight: 500;margin-top: 0.55em;">Receiptofi</h1>
                </div>
            </div>
            <div class="header_right_login">
                <div class="sing_up"><a href="${pageContext.request.contextPath}/open/login.htm">Login</a></div>
                <div class="sing_up"><a href="${pageContext.request.contextPath}/open/registration.htm">Register</a></div>
            </div>
        </div>
    </div>
</div>
<div class="clear"></div>
<div class="signup_containerl">
    <div class="signup_mainl">
        <div class="loginl">
            <h1 class="h1 spacing" style="margin-bottom: 15px;">Sign up</h1>
            <h4 class="h4" style="margin-bottom: 40px;"><fmt:message key="invite.heading" /> and start analyzing your receipts in 30 seconds or less</h4>

            <form:form method="post" modelAttribute="inviteAuthenticateForm" action="authenticate.htm" autocomplete="true">
                <form:hidden path="forgotAuthenticateForm.receiptUserId" />
                <form:hidden path="forgotAuthenticateForm.authenticationKey" />

                <spring:hasBindErrors name="inviteAuthenticateForm">
                <div class="r-validation" style="width: 100%; margin: 0 0 0 0;">
                    <ul>
                    <c:if test="${errors.hasFieldErrors('firstName')}">
                        <li><form:errors path="firstName" /></li>
                    </c:if>
                    <c:if test="${errors.hasFieldErrors('lastName')}">
                        <li><form:errors path="lastName" /></li>
                    </c:if>
                    <c:if test="${errors.hasFieldErrors('mail')}">
                        <li><form:errors path="mail" /></li>
                    </c:if>
                    <c:if test="${errors.hasFieldErrors('forgotAuthenticateForm.password')}">
                        <li><form:errors path="forgotAuthenticateForm.password" /></li>
                    </c:if>
                    <c:if test="${errors.hasFieldErrors('acceptsAgreement')}">
                        <li><form:errors path="acceptsAgreement" /></li>
                    </c:if>
                    </ul>
                </div>
                </spring:hasBindErrors>

                <form:label for="firstName" path="firstName" cssClass="signup_label signup_label_text"
                        cssErrorClass="signup_label signup_label_text lb_error">I like to call myself as:</form:label>
                <form:input path="firstName" maxlength="80" placeholder="First name" cssClass="text" />

                <form:label for="lastName" path="lastName" cssClass="signup_label signup_label_text"
                        cssErrorClass="signup_label signup_label_text lb_error">And last name is:</form:label>
                <form:input path="lastName" maxlength="80" placeholder="Last name" cssClass="text" />

                <form:label for="birthday" path="birthday" cssClass="signup_label signup_label_text"
                        cssErrorClass="signup_label signup_label_text lb_error">How old are you?</form:label>
                <form:input path="birthday" maxlength="80" placeholder="Age" cssClass="text" />

                <form:label for="mail" path="mail" cssClass="signup_label signup_label_text"
                        cssErrorClass="signup_label signup_label_text lb_error">Valid email address as your login:</form:label>
                <form:input path="mail" maxlength="80" placeholder="Email address" cssClass="text" readonly="true"/>

                <form:label for="password" path="forgotAuthenticateForm.password" cssClass="signup_label signup_label_text"
                        cssErrorClass="signup_label signup_label_text lb_error">Password:</form:label>
                <form:password path="forgotAuthenticateForm.password" maxlength="80" placeholder="Password" cssClass="text" />

                <div class="checkbox">
                    <form:checkbox path="acceptsAgreement" id="acceptsAgreement" cssClass="chk" />
                    <form:label for="acceptsAgreement" path="acceptsAgreement" cssClass="checkbox_txt"
                            cssErrorClass="checkbox_txt lb_error">I fully agree to the Receiptofi terms</form:label>
                </div>
                <div class="clear"></div>

                <input id="submit_btn_id" type="submit" value="Complete Invitation" name="confirm_invitation" class="submit_btn" style="width: 175px;" />

                <c:if test="${!registrationTurnedOn}">
                    <div class="registrationWhenTurnedOff">
                        Registration is open, but site is not accepting new users. When site starts accepting new users,
                        you will be notified through email and your account would be turned active.
                    </div>
                </c:if>
            </form:form>
            <div class="clear"></div>
        </div>
    </div>
</div>
<div class="maha_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="fotter_copy">&#169; 2015 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
</html>