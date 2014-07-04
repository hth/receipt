<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="account.invitation.title" /></title>

    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/circle-leaf-sized_small.png" />

    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='${pageContext.request.contextPath}/static/jquery/css/receipt.css'>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery/js/jquery-ui-1.10.2.custom.min.js"></script>

    <c:if test="${empty inviteAuthenticateForm.emailId}">
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
    <c:if test="${empty inviteAuthenticateForm.emailId}">
        <div class="error_page">
            <img alt="receipt-o-fi logo" src="${pageContext.request.contextPath}/static/images/receipt-o-fi.logo.jpg" height="45px" />
            <h1>Invalid Link</h1>
            <p>We apologize, but we are unable to verify the link you used to access this page. <sup>(404)</sup></p>
            <p>&nbsp;</p>
            <p></p>Please <a href="${pageContext.request.contextPath}/login.htm">click here</a> to return to the main page and start over.</p>
        </div>
    </c:if>

    <c:if test="${!empty inviteAuthenticateForm.emailId}">
        <img src="${pageContext.request.contextPath}/static/images/receipt-o-fi.logo.jpg" alt="receipt-o-fi logo" height="40px"/>
        <p>&nbsp;</p>
        <h2>
            <fmt:message key="invite.heading" />
        </h2>
        <form:form method="post" action="authenticate.htm" modelAttribute="inviteAuthenticateForm">
            <form:hidden path="emailId" />
            <form:hidden path="forgotAuthenticateForm.receiptUserId" />
            <form:hidden path="forgotAuthenticateForm.authenticationKey" />
            <table style="background-color:#f8f8ff ; border: 0; border-spacing: 5px 10px; width: 600px">
                <tr>
                    <td style="text-align: right; width: 19%">Login Id:</td>
                    <td style="width: 30%">${inviteAuthenticateForm.emailId}</td>
                    <td style="width: 51%">&nbsp;</td>
                </tr>
                <tr>
                    <td style="text-align: right; width: 19%"><form:label for="firstName" path="firstName" cssErrorClass="error">First Name:</form:label></td>
                    <td style="width: 30%"><form:input class="tooltip" path="firstName" title="Please provide your First Name." /></td>
                    <td style="width: 51%"><form:errors path="firstName" cssClass="error" /></td>
                </tr>
                <tr>
                    <td style="text-align: right; width: 19%"><form:label for="lastName" path="lastName" cssErrorClass="error">Last Name:</form:label></td>
                    <td style="width: 30%"><form:input class="tooltip" path="lastName" title="Please provide your Last Name." /></td>
                    <td style="width: 51%"><form:errors path="lastName" cssClass="error" /></td>
                </tr>
                <tr>
                    <td style="text-align: right; width: 19%">
                        <form:label for="forgotAuthenticateForm.password" path="forgotAuthenticateForm.password" cssErrorClass="error">Password:</form:label>
                    </td>
                    <td style="width: 30%"><form:password class="tooltip" path="forgotAuthenticateForm.password" title="Please enter a password." /></td>
                    <td style="width: 51%"><form:errors path="forgotAuthenticateForm.password" cssClass="error" /></td>
                </tr>
                <tr>
                    <td style="text-align: right; width: 19%">
                        <form:label for="forgotAuthenticateForm.passwordSecond" path="forgotAuthenticateForm.passwordSecond" cssErrorClass="error">Retype Password</form:label>
                    </td>
                    <td style="width: 30%"><form:password class="tooltip" path="forgotAuthenticateForm.passwordSecond" title="Please re-enter the password." /></td>
                    <td style="width: 51%"><form:errors path="forgotAuthenticateForm.passwordSecond" cssClass="error" /></td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td>
                        <input id="signupId" type="submit" value="Complete Invitation" name="confirm_invitation" class="btn btn-default"/>
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
    <p>&copy; 2014 Receiptofi Inc. All Rights Reserved.</p>
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

    $(function () {
        $(document).tooltip();
    });
</script>

</body>
</html>