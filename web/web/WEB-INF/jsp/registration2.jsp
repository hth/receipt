<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="login.title"/></title>
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
                <div class="sing_up"><a href="${pageContext.request.contextPath}/login.htm">Login</a></div>
                <div class="sing_up"><a href="${pageContext.request.contextPath}/open/registration.htm">Register</a></div>
            </div>
        </div>
    </div>
</div>
<div class="clear"></div>
<div class="signup_containerl">
    <div class="signup_mainl">
        <div class="loginl">
            <h2 class="bold">Sign up, it's free</h2>
            <form:form method="post" modelAttribute="userRegistrationForm" action="registration.htm">
                <label class="signup_label"><strong class="bold">First name</strong></label>
                <input class="text" type="text" placeholder="Name"></input>
                <label class="signup_label"><strong class="bold">Last name</strong></label>
                <input class="text" type="text" placeholder="Last name"></input>
                <label class="signup_label"><strong class="bold">Email</strong></label>
                <%--<input class="text" maxlength="80" id="email" type="text" placeholder="name@address.com"></input>--%>
                <form:input path="mail" maxlength="80" placeholder="name@address.com" cssClass="text" />
                <label class="signup_label"><strong class="bold">Password</strong></label>
                <input class="text" name="password" value="" id="password" required="" placeholder="password" type="password">

                <div class="checkbox">
                    <span class="checkbox_txt"><input class="chk" type="checkbox" />I agree to the Receiptofi terms</span>
                </div>
                <input class="right submit_btn" id="login" type="submit" value="SIGN UP"></input>
            </form:form>
            <div class="clear"></div>
        </div>
    </div>
</div>
</body>
</html>