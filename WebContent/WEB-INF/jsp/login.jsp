<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
	<title><fmt:message key="login.title" /></title>

	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <link rel="icon" type="image/x-icon" href="images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="images/circle-leaf-sized_small.png" />

	<link rel='stylesheet' type='text/css' href='jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='jquery/css/receipt.css'>

	<script type="text/javascript" src="jquery/js/jquery-1.10.1.min.js"></script>
	<script type="text/javascript" src="jquery/js/jquery-ui-1.10.2.custom.min.js"></script>

</head>
<body>
<div class="wrapper">
    <img src="images/receipt-o-fi.logo.jpg" alt="receipt-o-fi logo" height="40px"/>
    <p>&nbsp;</p>
	<h2>
		<fmt:message key="login.heading" />
	</h2>
	<form:form method="post" modelAttribute="userLoginForm" action="login.htm">
		<table bgcolor="f8f8ff" border="0" cellspacing="0" cellpadding="5" style="width: 600px;">
			<tr>
				<td align="right" width="19%"><form:label for="emailId" path="emailId" cssErrorClass="error">Email Address:</form:label></td>
				<td width="30%"><form:input class="tooltip" path="emailId" title="Please enter the email address when you registered with us." /></td>
				<td width="51%"><form:errors path="emailId" cssClass="error" /></td>
			</tr>
			<tr>
				<td align="right" width="19%"><form:label for="password" path="password" cssErrorClass="error">Password:</form:label></td>
				<td width="30%"><form:password class="tooltip" path="password" title="Please enter the password you registered with." /></td>
				<td width="51%"><form:errors path="password" cssClass="error" /></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td align="left"><input type="submit" value="Login"></td>
			</tr>
		</table>
	</form:form>

	<p>
        <a href="<c:url value="new.htm"/>" title="Create a new account.">Register Now</a>
        &nbsp;&nbsp;|&nbsp;&nbsp;
        <a href="forgot/password.htm" title="Click here to recover your password.">Forgot your password ?</a>
    </p>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>&copy; 2013 Receiptofi Inc. All Rights Reserved. (<fmt:message key="build.version" />)</p>
</div>

<script>
    $(function() {
        $("#emailId").focus();
    });
</script>

<!--- For Text boxes -->
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