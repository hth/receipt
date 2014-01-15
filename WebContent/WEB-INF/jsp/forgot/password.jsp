<%@ include file="/WEB-INF/jsp/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="account.recover.title" /></title>

    <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <link rel="icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="../images/circle-leaf-sized_small.png" />

    <link rel='stylesheet' type='text/css' href='../jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>

</head>
<body>
<div class="wrapper">
    <img src="../images/receipt-o-fi.logo.jpg" alt="receipt-o-fi logo" height="40px"/>
    <p>&nbsp;</p>
    <h2>
        <fmt:message key="account.recover.title" />
    </h2>
    <form:form method="post" modelAttribute="forgotRecoverForm" action="password.htm">
        <table style="background-color:#f8f8ff ; border: 0; border-spacing: 5px 10px; width: 600px">
            <p style="display:none;visibility:hidden;">
                <form:label for="captcha" path="captcha" cssErrorClass="error">Captcha:</form:label>
                <form:input path="captcha" />
                <form:errors path="captcha" cssClass="error" />
            </p>
            <tr>
                <td style="text-align: right; width: 19%"><form:label for="emailId" path="emailId" cssErrorClass="error">Email Address:</form:label></td>
                <td style="width: 30%"><form:input class="tooltip" path="emailId" title="Enter your account's login email address." /></td>
                <td style="width: 51%"><form:errors path="emailId" cssClass="error" /></td>
            </tr>
            <tr>
                <td colspan="3">&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td align="left"><input type="submit" value="Recover Account" name="forgot_password"></td>
            </tr>
        </table>
    </form:form>

    <p><a href="<c:url value="../login.htm"/>" title="Click here to go to login.">Login</a></p>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2013 Receiptofi Inc. All Rights Reserved.</p>
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

<!-- For http links -->
<script>
    $(function () {
        $(document).tooltip();
    });
</script>

</body>
</html>