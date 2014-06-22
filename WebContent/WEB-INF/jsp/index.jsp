<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Home</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery/css/twitter/bootstrap.css"/>
</head>
<body>
<div>
    <h1>Home</h1>
    <sec:authorize access="isAnonymous()">
        <p>
            <a href="${pageContext.request.contextPath}/login.htm">Sign In</a>
            <%--<a href="${pageContext.request.contextPath}/spring_security_login">Sign In</a>--%>
        </p>
    </sec:authorize>

    <!-- FACEBOOK SIGNIN -->
    <form:form name="fb_signin" id="fb_signin" action="${pageContext.request.contextPath}/signin/facebook.htm" method="POST">
        <input type="hidden" name="scope" value="email,basic_info,user_activities,user_education_history,user_likes" />
        <button type="submit"><img src="${pageContext.request.contextPath}/static/jquery/css/social/facebook/sign-in-with-facebook.png" /></button>
    </form:form>

    <!-- GOOGLE SIGNIN -->
    <form:form name="g_signin" id="g_signin" action="${pageContext.request.contextPath}/signin/google.htm" method="POST">
        <button type="submit" class="btn btn-large btn-primary">Sign in with Google</button>
        <input type="hidden" name="scope" value="email https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/tasks https://www.googleapis.com/auth/drive https://www.googleapis.com/auth/latitude.all.best" />
        <input type="hidden" name="request_visible_actions" value="http://schemas.google.com/AddActivity http://schemas.google.com/BuyActivity http://schemas.google.com/CheckInActivity http://schemas.google.com/CommentActivity http://schemas.google.com/CreateActivity http://schemas.google.com/DiscoverActivity http://schemas.google.com/ListenActivity http://schemas.google.com/ReserveActivity http://schemas.google.com/ReviewActivity http://schemas.google.com/WantActivity"/>
        <input type="hidden" name="access_type" value="offline"/>
    </form:form>
</div>
</body>
</html>