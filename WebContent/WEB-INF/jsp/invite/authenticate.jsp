<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="account.invitation.title" /></title>

    <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <link rel="icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />

    <link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>

    <c:if test="${empty inviteAuthenticateForm.forgotAuthenticateForm}">
        <style>
            body {background: #e6e6e6;margin: 0; padding: 20px; text-align:center; font-family:Arial, Helvetica, sans-serif; font-size:14px; color:#666666;}
            .error_page {width: 600px; padding: 50px; margin: auto;}
            .error_page h1 {margin: 20px 0 0;}
            .error_page p {margin: 10px 0; padding: 0;}
            a {color: #9caa6d; text-decoration:none;}
            a:hover {color: #9caa6d; text-decoration:underline;}
        </style>
    </c:if>
</head>
<body>
<div class="wrapper">
    <c:if test="${empty inviteAuthenticateForm.forgotAuthenticateForm}">
        <div class="error_page">
            <img alt="receipt-o-fi logo" src="../images/receipt-o-fi.logo.jpg" height="45px" />
            <h1>Invalid Link</h1>
            <p>We apologize, but we are unable to verify the link you used to access this page. <sup>(404)</sup></p>
            <p>&nbsp;</p>
            <p></p>Please <a href="../login.htm">click here</a> to return to the main page and start over.</p>
        </div>
    </c:if>

    <c:if test="${!empty inviteAuthenticateForm.forgotAuthenticateForm}">
        <img src="../images/receipt-o-fi.logo.jpg" alt="receipt-o-fi logo" height="40px">
        <p>&nbsp;</p>
        <h2>
            <fmt:message key="invite.heading" />
        </h2>
        <form:form method="post" action="authenticate.htm" modelAttribute="inviteAuthenticateForm">
            <form:hidden path="forgotAuthenticateForm.userProfileId" />
            <form:hidden path="forgotAuthenticateForm.authenticationKey" />
            <table bgcolor="f8f8ff" border="0" cellspacing="0" cellpadding="5" width="600px">
                <tr>
                    <td align="right" width="19%"><form:label for="firstName" path="firstName" cssErrorClass="error">First Name:</form:label></td>
                    <td width="30%"><form:input path="firstName" /></td>
                    <td width="51%"><form:errors path="firstName" cssClass="error" /></td>
                </tr>
                <tr>
                    <td align="right" width="19%"><form:label for="lastName" path="lastName" cssErrorClass="error">Last Name:</form:label></td>
                    <td width="30%"><form:input path="lastName" /></td>
                    <td width="51%"><form:errors path="lastName" cssClass="error" /></td>
                </tr>
                <tr>
                    <td align="right" width="19%"><form:label for="forgotAuthenticateForm.password" path="forgotAuthenticateForm.password" cssErrorClass="error">Password:</form:label></td>
                    <td width="30%"><form:input path="forgotAuthenticateForm.password" /></td>
                    <td width="51%"><form:errors path="forgotAuthenticateForm.password" cssClass="error" /></td>
                </tr>
                <tr>
                    <td align="right" width="19%"><form:label for="forgotAuthenticateForm.passwordSecond" path="forgotAuthenticateForm.passwordSecond" cssErrorClass="error">Retype Password</form:label></td>
                    <td width="30%"><form:input path="forgotAuthenticateForm.passwordSecond" /></td>
                    <td width="51%"><form:errors path="forgotAuthenticateForm.passwordSecond" cssClass="error" /></td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td>
                        <input id="signupId" type="submit" value="Complete Invitation" name="confirm_invitation"/>
                    </td>
                    <td>&nbsp;</td>
                </tr>
            </table>
        </form:form>
    </c:if>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2013 receipt-o-fi. All Rights Reserved.</p>
</div>

</body>
</html>