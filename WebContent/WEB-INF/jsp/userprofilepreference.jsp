<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><fmt:message key="profile.title" /></title>
	
	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
	
	<link rel='stylesheet' type='text/css' href='jquery/fullcalendar/fullcalendar.css' />
	<link rel='stylesheet' type='text/css' href='jquery/fullcalendar/fullcalendar.print.css' media='print' />
	<link rel='stylesheet' type='text/css' href='jquery/css/smoothness/jquery-ui-1.9.2.custom.css'>
	<link rel='stylesheet' type='text/css' href='jquery/css/receipt.css'>	
	
	<script type="text/javascript" src="jquery/js/jquery-1.8.3.js"></script>
	<script type="text/javascript" src="jquery/js/jquery-ui-1.9.2.custom.js"></script>
	<script type='text/javascript' src="jquery/fullcalendar/fullcalendar.min.js"></script>
	
	<!-- For tabs -->
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
</head>
<body>
	<div>
		<p>User Id <a href="${pageContext.request.contextPath}/userprofilepreference.htm">${userSession.emailId}</a></p>
	</div>
		
	<!-- Tabs -->
	<h2 class="demoHeaders">Profile And Preferences</h2>
	<div id="tabs">
		<ul>
			<li><a href="#tabs-1">Profile</a></li>
			<li><a href="#tabs-2">Preferences</a></li>
		</ul>
		<div id="tabs-1">	
			<form:form modelAttribute="userProfile" method="post" enctype="multipart/form-data">
				<div class="divTable">
					<div class="divRow">
						<div class="divOfCell400">Name: ${userProfile.firstName}  ${userProfile.lastName}</div>
					</div>
					<div class="divRow">
					    <div class="divOfCell400">Registration: ${userProfile.registration}</div>
					</div>
			   	</div>	
			</form:form>			
		</div>
		<div id="tabs-2">
			<form:form modelAttribute="userPreference" method="post" enctype="multipart/form-data">
				<div class="divTable">
					<div class="divRow">
						<div class="divOfCell400">Account Type: ${userPreference.accountType.description}</div>
					</div>
					<div class="divRow">
					    <div class="divOfCell400"></div>
					</div>
			   	</div>	
			</form:form>		
		</div>
	</div>
</body>
</html>