<%@ include file="include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <script>var ctx = "${pageContext.request.contextPath}"</script>

    <title><fmt:message key="login.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/css/stylelogin.css'>
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
        <c:if test="${deniedSignup}">
        <div class="signup_label_text" style="padding-bottom: 10px;">
            You have been registered, but we are currently not accepting new users. <br/>
        </div>
        <div class="signup_label_text" style="padding-bottom: 10px;">
            Will notify you through email when we start accepting new users and will automatically grant access to you. <br/>
        </div>
        <div class="signup_label_text" style="padding-bottom: 10px;">
            User: ${user}
        </div>
        <div class="signup_label_text" style="padding-bottom: 10px;">
            Registration: ${pid}
        </div>
        </c:if>

        <div class="loginl" style="width: 450px;">
            <br><br>
            <c:if test="${!empty param.error and param.error eq 'provider'}">
                <div class="r-error" style="margin-left: 0; width: 100%">
                    Login not successful. Reason: You seems to be already registered with one of the other social provider.
                </div>
            </c:if>
            <!-- FACEBOOK SIGNIN -->
            <form:form name="fb_signin" id="fb_signin" action="${pageContext.request.contextPath}/signin/facebook.htm" method="POST" cssStyle="float: left;">
                <input type="hidden" name="scope" value="email,public_profile,user_friends,user_about_me,user_birthday" />
                <%--<input type="hidden" name="scope" value="email,public_profile,user_friends,user_activities,user_education_history,user_likes" />--%>
                <%--<button type="submit"><img src="${pageContext.request.contextPath}/static/jquery/css/social/facebook/sign-in-with-facebook.png" /></button>--%>
                <button type="submit" class="submit_btn" style="width: 173px;">FACEBOOK SIGN IN</button>
            </form:form>
            <!-- GOOGLE SIGNIN -->
            <form:form name="g_signin" id="g_signin" action="${pageContext.request.contextPath}/signin/google.htm" method="POST" cssStyle="float: right;">
                <button type="submit" class="submit_btn" style="width: 173px;">GOOGLE+ SIGN IN</button>
                <input type="hidden" name="scope" value="email https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/tasks https://www.googleapis.com/auth/drive https://www.googleapis.com/auth/latitude.all.best" />
                <input type="hidden" name="request_visible_actions" value="http://schemas.google.com/AddActivity http://schemas.google.com/BuyActivity http://schemas.google.com/CheckInActivity http://schemas.google.com/CommentActivity http://schemas.google.com/CreateActivity http://schemas.google.com/DiscoverActivity http://schemas.google.com/ListenActivity http://schemas.google.com/ReserveActivity http://schemas.google.com/ReviewActivity http://schemas.google.com/WantActivity"/>
                <input type="hidden" name="access_type" value="offline"/>
            </form:form>
            <br><br><br><br>

            <div class="separator">&nbsp;&nbsp;OR&nbsp;&nbsp;</div>

            <h1 class="h1 spacing"><fmt:message key="login.heading" /></h1>
            <c:if test="${!empty param.loginFailure and param.loginFailure eq '--' and !empty sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}">
                <div class="r-error" style="margin-left: 0; width: 100%">
                    Login not successful. Reason: ${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}
                </div>
                <c:remove var="SPRING_SECURITY_LAST_EXCEPTION" scope="session"/>
            </c:if>

            <form:form method="post" modelAttribute="userLoginForm" action="/login" autocomplete="on">
                <%--<form:label for="emailId" path="emailId" cssClass="sign_uplabel"><strong class="bold">Email Address</strong></form:label>--%>
                <form:input path="emailId" cssClass="text" placeholder="Email address"/>
                <%--<form:label for="password" path="password" cssClass="sign_uplabel"><strong class="bold">Password</strong></form:label>--%>
                <form:password path="password" cssClass="text" placeholder="Password"/>
                <span class="link"><a href="${pageContext.request.contextPath}/open/forgot/password.htm">Forgot your password?</a></span>
                <div class="checkbox">
                    <input class="chk" type='checkbox' name='remember-me' checked="checked"/>
                    <span class="checkbox_txt">Remember me on this device</span>
                </div>
                <input class="right submit_btn" id="login" type="submit" value="SIGN IN"/>
            </form:form>
            <div class="clear"></div>
        </div>
    </div>
</div>
<div class="maha_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="fotter_copy">&#169; 2015 RECEIPTOFI, INC. ALL RIGHTS RESERVED. (<fmt:message key="build.version" />)
    </div>
</div>
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/4.1.5/highcharts.js"></script>
<script src="${pageContext.request.contextPath}/static/external/js/cute-time/jquery.cuteTime.min.js"></script>
<script src="${pageContext.request.contextPath}/static/external/js/fineuploader/jquery.fine-uploader.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.9.0/moment.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/fullcalendar/2.3.1/fullcalendar.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/fineupload.js"></script>
<script>
    (function(b,o,i,l,e,r){b.GoogleAnalyticsObject=l;b[l]||(b[l]=
            function(){(b[l].q=b[l].q||[]).push(arguments)});b[l].l=+new Date;
        e=o.createElement(i);r=o.getElementsByTagName(i)[0];
        e.src='//www.google-analytics.com/analytics.js';
        r.parentNode.insertBefore(e,r)}(window,document,'script','ga'));
    ga('create','UA-65975717-1','auto');ga('send','pageview');
</script>
</body>
</html>