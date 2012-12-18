<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title><fmt:message key="title" /></title>
<style>
.error {
	color: red;
}
</style>
</head>
<body>
	<h1>
		<fmt:message key="login.heading" />
	</h1>
	<form:form method="post" modelAttribute="receiptUser" action="login.htm">
		<table width="95%" bgcolor="f8f8ff" border="0" cellspacing="0" cellpadding="5">
			<tr>
				<td align="right" width="20%"><form:label for="emailId" path="emailId" cssErrorClass="error">Email ID:</form:label></td>
				<td width="20%"><form:input path="emailId" /></td>
				<td width="60%"><form:errors path="emailId" cssClass="error" /></td>
			</tr>
			<tr>
				<td align="right" width="20%"><form:label for="password" path="password" cssErrorClass="error">Password:</form:label></td>
				<td width="20%"><form:input path="password" /></td>
				<td width="60%"><form:errors path="password" cssClass="error" /></td>
			</tr>
		</table>
		<br>
		<input type="submit" align="center" value="Login">
	</form:form>
	<a href="<c:url value="signup.htm"/>">Signup</a>
</body>
</html>