<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><fmt:message key="signup.title" /></title>

	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">

	<link rel='stylesheet' type='text/css' href='jquery/fullcalendar/fullcalendar.css' />
	<link rel='stylesheet' type='text/css' href='jquery/fullcalendar/fullcalendar.print.css' media='print' />
	<link rel='stylesheet' type='text/css' href='jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='jquery/css/receipt.css'>

	<script type="text/javascript" src="jquery/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
	<script type='text/javascript' src="jquery/fullcalendar/fullcalendar.min.js"></script>
    <script type='text/javascript' src="jquery/js/json.min.js"></script>
</head>
<body>
	<h2>
		<fmt:message key="signup.heading" />
	</h2>
	<form:form method="post" modelAttribute="userRegistrationForm" action="new.htm">
		<table bgcolor="f8f8ff" border="0" cellspacing="0" cellpadding="5">
			<tr>
				<td align="right" width="10%"><form:label for="firstName" path="firstName" cssErrorClass="error">First Name:</form:label></td>
				<td width="30%"><form:input path="firstName" /></td>
				<td width="60%"><form:errors path="firstName" cssClass="error" /></td>
			</tr>
			<tr>
				<td align="right" width="10%"><form:label for="lastName" path="lastName" cssErrorClass="error">Last Name:</form:label></td>
				<td width="30%"><form:input path="lastName" /></td>
				<td width="60%"><form:errors path="lastName" cssClass="error" /></td>
			</tr>
			<tr>
				<td align="right" width="10%"><form:label for="emailId" path="emailId" cssErrorClass="error">Email ID:</form:label></td>
				<td width="30%"><form:input path="emailId" /></td>
				<td width="60%"><form:errors path="emailId" cssClass="error" id="emailId.errors"/></td>
			</tr>
			<tr>
				<td align="right" width="10%"><form:label for="password" path="password" cssErrorClass="error">Password:</form:label></td>
				<td width="30%"><form:input path="password" /></td>
				<td width="60%"><form:errors path="password" cssClass="error" /></td>
			</tr>
			<tr>
				<td align="right" width="10%"><form:label for="accountType" path="accountType" cssErrorClass="error">Account Type:</form:label></td>
				<td width="30%">
					<form:select path="accountType" >
						<form:option value="0" label="Chose Account Type" />
						<form:options itemValue="name" itemLabel="description" />
					</form:select>
				</td>
				<td width="60%"><form:errors path="accountType" cssClass="error" /></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td><input type="submit" value="Signup" name="Signup"/></td>
			</tr>
		</table>
	</form:form>

	<br/>
	Please note: A verification email will be sent to your email address.

    <script type="text/javascript">
        $(document).ready(function() {
            // check name availability on focus lost
            $('#emailId').blur(function() {
                if ($('#emailId').val()) {
                    checkAvailability();
                }
            });
        });

        function checkAvailability() {
            $.getJSON("${pageContext. request. contextPath}/new/availability.htm", { emailId: $('#emailId').val() }, function(availability) {
                if (availability.available) {
                    fieldValidated("emailId", { valid : true });
                } else {
                    fieldValidated("emailId", { valid : false, message : $('#emailId').val() + " is already registered. " + availability.suggestions });
                }
            });
        }

        function fieldValidated(field, result) {
            if (result.valid) {
                $("#" + field + "Label").removeClass("error");
                $("#" + field + "\\.errors").remove();
                $('#create').attr("disabled", false);
            } else {
                $("#" + field + "Label").addClass("error");
                if ($("#" + field + "\\.errors").length == 0) {
                    $("#" + field).after("<span id='" + field + ".errors'>" + result.message + "</span>");
                } else {
                    $("#" + field + "\\.errors").html("<span id='" + field + ".errors'>" + result.message + "</span>");
                }
                $('#create').attr("disabled", true);
            }
        }
    </script>
</body>
</html>