<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="account.recover.title" /></title>

    <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <meta HTTP-EQUIV="Pragma" content="no-cache">
    <meta HTTP-EQUIV="Expires" content="-1">

    <link rel="icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />

    <link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>

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
    <c:if test="${success eq false}">
        <div class="error_page">
            <img alt="receipt-o-fi logo" src="../images/receipt-o-fi.logo.jpg" height="45px" />
            <h1>Password update failed</h1>
            <p>We apologize, but we were unable to update your account with new password.</p>
            <p>&nbsp;</p>
            <p></p>Please <a href="../login.htm">click here</a> to return to the main page and start over.</p>
        </div>
    </c:if>

    <c:if test="${success eq true}">
        <div class="error_page">
            <img alt="receipt-o-fi logo" src="../images/receipt-o-fi.logo.jpg" height="45px" />
            <h1>Password successfully changed</h1>
            <p>Please log in with your new password.</p>
            <p>&nbsp;</p>
            <p></p><a href="../login.htm">Click here</a> to return to the login page.</p>
        </div>
    </c:if>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2013 Receiptofi Inc. All Rights Reserved.</p>
</div>

</body>
</html>