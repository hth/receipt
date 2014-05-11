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
</div>
</body>
</html>