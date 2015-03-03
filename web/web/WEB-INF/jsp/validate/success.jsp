<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="account.email.validated"/></title>

    <meta HTTP-EQUIV="Pragma" content="no-cache">
    <meta HTTP-EQUIV="Expires" content="-1">

    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png"/>
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png"/>

    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/css/receipt.css'>

    <style>
        body {background: #e6e6e6;margin: 0; padding: 20px; text-align:center; font-family:Arial, Helvetica, sans-serif; font-size:14px; color:#666666;}
        .error_page {width: 600px; padding: 50px; margin: auto;}
        .error_page h1 {margin: 20px 0 0;}
        .error_page p {margin: 10px 0; padding: 0;}
        a {color: #9caa6d; text-decoration:none;}
        a:hover {color: #9caa6d; text-decoration:underline;}
    </style>
</head>
<body>
<div class="wrapper">
    <div class="error_page">
        <img alt="receipt-o-fi logo" src="${pageContext.request.contextPath}/static/images/receipt-o-fi.logo.jpg" height="45px" />
        <h1>Account successfully validated</h1>
        <c:choose>
            <c:when test="${!registrationTurnedOn}">
                <p>Your email has been successfully validated.</p>
                <p>Currently we are not accepting new users. We will notify you on your registered email when we start accepting new users.</p>
            </c:when>
            <c:otherwise>
                <p>Your email has been successfully validate.</p>
                <p>Please log in with your email address and password entered during registration.</p>
            </c:otherwise>
        </c:choose>
        <p>&nbsp;</p>
        <p><a href="${pageContext.request.contextPath}/open/login.htm">Click here</a> to return to the login page.</p>
    </div>
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