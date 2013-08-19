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

    <script type="text/javascript" src="../jquery/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>

    <c:if test="${empty forgotAuthenticateForm}">
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
    <c:if test="${empty forgotAuthenticateForm}">
    <div class="error_page">
        <img alt="receipt-o-fi logo" src="../images/receipt-o-fi.logo.jpg" height="45px" />
        <h1>Invalid Link</h1>
        <p>We apologize, but we are unable to verify the link you used to access this page. <sup>(404)</sup></p>
        <p>&nbsp;</p>
        <p>Please <a href="../login.htm">click here</a> to return to the main page and start over.</p>
    </div>
    </c:if>

    <c:if test="${!empty forgotAuthenticateForm}">
    <img src="../images/receipt-o-fi.logo.jpg" alt="receipt-o-fi logo" height="40px"/>
    <p>&nbsp;</p>
    <h2>
        <fmt:message key="password.update.heading" />
    </h2>
    <form:form method="post" action="authenticate.htm" modelAttribute="forgotAuthenticateForm">
        <form:hidden path="userProfileId" />
        <form:hidden path="authenticationKey" />
        <table style="background-color:#f8f8ff ; border: 0; border-spacing: 5px 10px; width: 600px">
            <tr>
                <td style="text-align: right; width: 19%"><form:label for="password" path="password" cssErrorClass="error">Password:</form:label></td>
                <td style="width: 30%"><form:password class="tooltip" path="password" title="Please enter a password." /></td>
                <td style="width: 51%"><form:errors path="password" cssClass="error" /></td>
            </tr>
            <tr>
                <td style="text-align: right; width: 19%"><form:label for="passwordSecond" path="passwordSecond" cssErrorClass="error">Retype Password</form:label></td>
                <td style="width: 30%"><form:password class="tooltip" path="passwordSecond" title="Please re-enter the password." /></td>
                <td style="width: 51%"><form:errors path="passwordSecond" cssClass="error" /></td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>
                    <input id="signupId" type="submit" value="Update Password" name="update_password"/>
                </td>
                <td>&nbsp;</td>
            </tr>
        </table>
        </form:form>

        <p><b>* Please update Receiptofi Mobile App with new Authorization Code found on 'Profile And Preferences' page</b></p>
    </c:if>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2013 receipt-o-fi. All Rights Reserved.</p>
</div>

<script>
    $(function () {
        $('.tooltip').each(function () {
            var $this, id, t;

            $this = $(this);
            id = this.id;
            t = $('<span />', {
                title: $this.attr('title')
            }).appendTo($this.parent()).tooltip({
                position: {
                    of: '#' + id,
                    my: "left+190 center",
                    at: "left center",
                    collision: "fit"
                }
            });
            // remove the title from the real element.
            $this.attr('title', '');
            $('#' + id).focusin(function () {
                t.tooltip('open');
            }).focusout(function () {
                t.tooltip('close');
            });
        });
    });
</script>

</body>
</html>