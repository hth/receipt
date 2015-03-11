<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="account.recover.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/css/stylelogin.css'>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
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
                <div class="sing_up"><a href="${pageContext.request.contextPath}/open/login.htm">Sign In</a></div>
                <div class="sing_up"><a href="${pageContext.request.contextPath}/open/registration.htm">Register</a>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="clear"></div>
<div class="signup_containerl">
    <div class="signup_mainl">
        <div class="loginl">
            <c:choose>
                <c:when test="${!empty forgotAuthenticateForm.receiptUserId}">
                    <h1 class="h1 spacing"><fmt:message key="password.update.heading" /></h1>

                    <form:form method="post" action="authenticate.htm" modelAttribute="forgotAuthenticateForm">
                        <form:hidden path="receiptUserId" />
                        <form:hidden path="authenticationKey" />

                        <spring:hasBindErrors name="forgotAuthenticateForm">
                        <div class="r-validation" style="width: 100%; margin: 0 0 0 0;">
                            <ul>
                                <c:if test="${errors.hasFieldErrors('password')}">
                                <li><form:errors path="password" /></li>
                                </c:if>
                                <c:if test="${errors.hasFieldErrors('passwordSecond')}">
                                <li><form:errors path="passwordSecond" /></li>
                                </c:if>
                            </ul>
                        </div>
                        </spring:hasBindErrors>

                        <form:label for="password" path="password" cssClass="signup_label signup_label_text"
                                cssErrorClass="signup_label signup_label_text lb_error">Password</form:label>
                        <form:password path="password" cssClass="text" />

                        <form:label for="passwordSecond" path="passwordSecond" cssClass="signup_label signup_label_text"
                                cssErrorClass="signup_label signup_label_text lb_error">Retype Password</form:label>
                        <form:password path="passwordSecond" cssClass="text" />
                        <div class="clear" style="padding-bottom: 10%"></div>

                        <input type="submit" value="Reset Password" name="update_password" class="right submit_btn" style="width: 170px;" />
                    </form:form>
                </c:when>
                <c:otherwise>
                    <h1 class="h1 spacing">Invalid Link</h1>

                    <div class="r-error">
                        We apologize, but we are unable to verify the link you used to access this page. <sup>(404)</sup>
                    </div>

                    <p>&nbsp;</p>
                    <p>Please <a href="/open/login.htm">click here</a> to return to the main page and start over.</p>
                </c:otherwise>
            </c:choose>
            <div class="clear"></div>
        </div>
    </div>
</div>
<div class="maha_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="fotter_copy">&#64; 2015 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
</html>