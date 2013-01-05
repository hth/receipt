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
	<link rel='stylesheet' type='text/css' href='jquery/css/smoothness/jquery-ui-1.9.2.custom.css'>
	
	<script type="text/javascript" src="jquery/js/jquery-1.8.3.js"></script>
	<script type="text/javascript" src="jquery/js/jquery-ui-1.9.2.custom.js"></script>
	<script type='text/javascript' src="jquery/fullcalendar/fullcalendar.min.js"></script>
	
	<script>
	$(function() {
		
		$( "#accordion" ).accordion();
		
		var availableTags = [
			"ActionScript",
			"AppleScript",
			"Asp",
			"BASIC",
			"C",
			"C++",
			"Clojure",
			"COBOL",
			"ColdFusion",
			"Erlang",
			"Fortran",
			"Groovy",
			"Haskell",
			"Java",
			"JavaScript",
			"Lisp",
			"Perl",
			"PHP",
			"Python",
			"Ruby",
			"Scala",
			"Scheme"
		];
		$( "#autocomplete" ).autocomplete({
			source: availableTags
		});
		
		$( "#button" ).button();
		$( "#radioset" ).buttonset();
		
		$( "#tabs" ).tabs();
		
		$( "#dialog" ).dialog({
			autoOpen: false,
			width: 400,
			buttons: [
				{
					text: "Ok",
					click: function() {
						$( this ).dialog( "close" );
					}
				},
				{
					text: "Cancel",
					click: function() {
						$( this ).dialog( "close" );
					}
				}
			]
		});

		// Link to open the dialog
		$( "#dialog-link" ).click(function( event ) {
			$( "#dialog" ).dialog( "open" );
			event.preventDefault();
		});
		
		$( "#datepicker" ).datepicker({
			inline: true
		});
		
		$( "#slider" ).slider({
			range: true,
			values: [ 17, 67 ]
		});
		
		$( "#progressbar" ).progressbar({
			value: 20
		});		

		// Hover states on the static widgets
		$( "#dialog-link, #icons li" ).hover(
			function() {
				$( this ).addClass( "ui-state-hover" );
			},
			function() {
				$( this ).removeClass( "ui-state-hover" );
			}
		);
	});
	</script>
	
	<style>
	body{
		font: 62.5% "Trebuchet MS", sans-serif;
		margin: 50px;
	}
	.demoHeaders {
		margin-top: 2em;
	}
	#dialog-link {
		padding: .4em 1em .4em 20px;
		text-decoration: none;
		position: relative;
	}
	#dialog-link span.ui-icon {
		margin: 0 5px 0 0;
		position: absolute;
		left: .2em;
		top: 50%;
		margin-top: -8px;
	}
	#icons {
		margin: 0;
		padding: 0;
	}
	#icons li {
		margin: 2px;
		position: relative;
		padding: 4px 0;
		cursor: pointer;
		float: left;
		list-style: none;
	}
	#icons span.ui-icon {
		float: left;
		margin: 0 4px;
	}
	</style>
	
	<style>
	.error {
		color: red;
	}
	</style>
</head>
<body>
	<h2>
		<fmt:message key="signup.heading" />
	</h2>	
	<form:form method="post" modelAttribute="userRegistrationWrapper" action="newaccount.htm">
		<table bgcolor="f8f8ff" border="0" cellspacing="0" cellpadding="5">
			<tr>
				<td align="right" width="20%"><form:label for="firstName" path="firstName" cssErrorClass="error">First Name:</form:label></td>
				<td width="20%"><form:input path="firstName" /></td>
				<td width="60%"><form:errors path="firstName" cssClass="error" /></td>
			</tr>
			<tr>
				<td align="right" width="20%"><form:label for="lastName" path="lastName" cssErrorClass="error">Last Name:</form:label></td>
				<td width="20%"><form:input path="lastName" /></td>
				<td width="60%"><form:errors path="lastName" cssClass="error" /></td>
			</tr>			
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
			<tr>
				<td align="right" width="20%"><form:label for="accountType" path="accountType" cssErrorClass="error">Account Type:</form:label></td>
				<td width="20%">
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
</body>
</html>