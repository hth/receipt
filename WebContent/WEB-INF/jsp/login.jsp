<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
	<title><fmt:message key="login.title" /></title>

	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">

	<link rel='stylesheet' type='text/css' href='jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='jquery/css/receipt.css'>

	<script type="text/javascript" src="jquery/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
</head>
<body>
<div class="wrapper">
    <img src="images/receipt-o-fi.logo.jpg" alt="receipt-o-fi logo" height="40px">
    <p>&nbsp;</p>
	<h2>
		<fmt:message key="login.heading" />
	</h2>
	<form:form method="post" modelAttribute="userLoginForm" action="login.htm">
		<table bgcolor="f8f8ff" border="0" cellspacing="0" cellpadding="5" style="width: 350px;">
			<tr>
				<td align="right" width="10%"><form:label for="emailId" path="emailId" cssErrorClass="error">Email ID:</form:label></td>
				<td width="20%"><form:input path="emailId" /></td>
				<td width="60%"><form:errors path="emailId" cssClass="error" /></td>
			</tr>
			<tr>
				<td align="right" width="20%"><form:label for="password" path="password" cssErrorClass="error">Password:</form:label></td>
				<td width="20%"><form:input path="password" /></td>
				<td width="60%"><form:errors path="password" cssClass="error" /></td>
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


	<br>
	<a href="<c:url value="new.htm"/>">New Account Sign In</a>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>Copyright &copy; 2013 receipt-o-fi. All Rights Reserved.</p>
</div>

</body>
</html>