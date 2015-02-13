<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="login.title"/></title>
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
                <div class="sing_up"><a href="${pageContext.request.contextPath}/login.htm">Sign In</a></div>
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
            <h1 class="h1 spacing"><fmt:message key="login.heading" /></h1>

            <c:if test="${!empty param.loginFailure and param.loginFailure eq '--' and !empty sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}">
                <div class="first">
                    <strong>Login not successful, try again. Reason: ${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}</strong>
                </div>
                <c:remove var="SPRING_SECURITY_LAST_EXCEPTION" scope="session"/>
            </c:if>

            <form:form method="post" modelAttribute="userLoginForm" action="j_spring_security_check" autocomplete="on">
                <%--<form:label for="emailId" path="emailId" cssClass="sign_uplabel"><strong class="bold">Email Address</strong></form:label>--%>
                <form:input path="emailId" cssClass="text" placeholder="Email"/>
                <%--<form:label for="password" path="password" cssClass="sign_uplabel"><strong class="bold">Password</strong></form:label>--%>
                <form:password path="password" cssClass="text" placeholder="Password"/>
                <div class="checkbox">
                    <input class="chk" type='checkbox' name='_spring_security_remember_me'/>
                    <span class="checkbox_txt">Remember me on this computer</span>
                </div>
                <input class="right submit_btn" id="login" type="submit" value="SIGN IN"/>
            </form:form>
            <div class="clear"></div>
            <hr>
            <span><a href="${pageContext.request.contextPath}/open/forgot/password.htm">Forgot your password?</a></span>
        </div>
    </div>
</div>
</body>
</html>