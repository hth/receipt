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
            <h1 class="h1 spacing"><fmt:message key="account.recover.title" /></h1>

            <form:form method="post" modelAttribute="forgotRecoverForm" action="password.htm">
                <form:hidden path="mail" />

                <p style="display:none;visibility:hidden;">
                    <form:label for="captcha" path="captcha" cssErrorClass="error">Captcha:</form:label>
                    <form:input path="captcha" disabled="true"/>
                    <form:errors path="captcha" cssClass="error" />
                </p>

                <spring:hasBindErrors name="forgotRecoverForm">
                <div class="r-validation" style="width: 100%; margin: 0 0 0 0;">
                    <ul>
                        <c:if test="${errors.hasFieldErrors('mail')}">
                        <li><form:errors path="mail" /></li>
                        </c:if>
                    </ul>
                </div>
                </spring:hasBindErrors>

                <form:label for="mail" path="mail" cssClass="signup_label signup_label_text"
                        cssErrorClass="signup_label signup_label_text lb_error">Email address</form:label>
                <form:input path="mail" cssClass="text" readonly="true" disabled="true" />
                <div class="clear" style="padding-bottom: 10%"></div>

                <input type="submit" value="SEND ME VERIFICATION EMAIL" name="forgot_password" class="right submit_btn" style="width: 289px" />
            </form:form>
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