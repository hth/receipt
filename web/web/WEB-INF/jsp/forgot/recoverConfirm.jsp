<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="account.recover.title"/></title>

    <meta HTTP-EQUIV="Pragma" content="no-cache">
    <meta HTTP-EQUIV="Expires" content="-1">

    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png"/>
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png"/>

    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/css/receipt.css'>

    <style>
        body {background: #e6e6e6;margin: 0; padding: 20px; text-align:center; font-family:Arial, Helvetica, sans-serif; font-size:14px; color:#666666;}
        .error_page {width: 700px; padding: 50px; margin: auto;}
        .error_page h1 {margin: 20px 0 0;}
        .error_page p {margin: 10px 0; padding: 0;}
        a {color: #9caa6d; text-decoration:none;}
        a:hover {color: #9caa6d; text-decoration:underline;}
    </style>
</head>
<body>
<div class="wrapper">
    <spring:eval expression="success_email eq T(com.receiptofi.domain.types.MailTypeEnum).SUCCESS" var="mailSentType" />
    <c:if test="${mailSentType}">
        <div class="error_page">
            <img alt="receipt-o-fi logo" src="${pageContext.request.contextPath}/static/images/receipt-o-fi.logo.jpg" height="45px" />
            <h1>Confirmation Page</h1>
            <p>An email has been sent with information regarding recovering your account password</p>
            <p>&nbsp;</p>
            <p><a href="${pageContext.request.contextPath}/login.htm">Click to the login</a></p>
        </div>
    </c:if>

    <c:if test="${!mailSentType}">
        <div class="error_page">
            <img alt="receipt-o-fi logo" src="${pageContext.request.contextPath}/static/images/receipt-o-fi.logo.jpg" height="45px" />
            <h1>Confirmation Page</h1>
            <p>Since your email address has not being validate we have sent verification email.</p>
            <p>Follow directions in email to validated your account and then resubmit new password reset request.</p>
            <p>&nbsp;</p>
            <p>Click here for <a href="${pageContext.request.contextPath}/open/login.htm">Login</a> page</p>
        </div>
    </c:if>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2015 Receiptofi Inc. All Rights Reserved.</p>
</div>

</body>
</html>