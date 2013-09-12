<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="account.recover.title" /></title>

    <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <link rel="icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />

    <link rel='stylesheet' type='text/css' href='../jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>

    <script type="text/javascript" src="../jquery/js/jquery-1.10.1.min.js"></script>
    <script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
</head>
<body>
<div class="wrapper">
    <img src="../images/receipt-o-fi.logo.jpg" alt="receipt-o-fi logo" height="40px"/>
    <p>&nbsp;</p>
    <h2>
        <fmt:message key="account.recover.title" />
    </h2>
    <form:form method="post" modelAttribute="forgotRecoverForm" action="recover.htm">
        <span style="display:none;visibility:hidden;">
            <form:label for="captcha" path="captcha" cssErrorClass="error">Captcha:</form:label>
            <form:input path="captcha" disabled="true"/>
            <form:errors path="captcha" cssClass="error" />
        </span>

        <form:hidden path="emailId" />
        <table style="background-color:#f8f8ff ; border: 0; border-spacing: 5px 10px; width: 600px">
            <tr>
                <td style="text-align: right; width: 19%"><form:label for="emailId" path="emailId" cssErrorClass="error">Email ID:</form:label></td>
                <td style="width: 30%"><b>${forgotRecoverForm.emailId}</b></td>
                <td style="width: 51%"><form:errors path="emailId" cssClass="error" /></td>
            </tr>
            <tr>
                <td colspan="3">&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td align="left"><input id="recoverAccountId" type="submit" value="Recover Account" name="forgot_recover"></td>
            </tr>
        </table>
    </form:form>
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