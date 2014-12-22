<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="account.recover.title"/></title>

    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png"/>
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png"/>

    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/external/css/jquery/jquery-ui-1.10.4.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/css/receipt.css'>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/external/js/jquery/jquery-ui-1.10.4.custom.min.js"></script>
</head>
<body>
<div class="wrapper">
    <img src="${pageContext.request.contextPath}/static/images/receipt-o-fi.logo.jpg" alt="receipt-o-fi logo" height="40px"/>
    <p>&nbsp;</p>
    <h2>
        <fmt:message key="account.recover.title" />
    </h2>
    <form:form method="post" modelAttribute="forgotRecoverForm" action="password.htm">
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
                <td align="left"><input id="recoverAccountId" type="submit" value="Recover Account" name="forgot_password" class="btn btn-default" /></td>
            </tr>
        </table>
    </form:form>
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