<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><fmt:message key="title" /></title>
	
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
	
	<style type='text/css'>		
		#calendar {
			width: 300px;
			margin: 0 auto;
		}
	</style>
	
	<style>
		.error {
			color: red;
		}
	</style>
</head>
<body>
	User Id <c:out value="${user.emailId}" /> 
	
	<!-- Tabs -->
	<h2 class="demoHeaders">Dashboard</h2>
	<div id="tabs">
		<ul>
			<li><a href="#tabs-1">Receipts</a></li>
			<li><a href="#tabs-2">Expense Analysis</a></li>
			<li><a href="#tabs-3">More</a></li>
		</ul>
		<div id="tabs-1">				
			<c:if test="${receipts.size() > 0}">
			<table border="1" style="background-color:#c5c021;border:1px dotted black;width:500px;border-collapse:collapse;">
				<tbody>
					<tr style="background-color:orange;color:white;">
						<th style="padding:3px;">Status</th>
						<th style="padding:3px;">Title</th>
						<th style="padding:3px;">Receipt Date</th>
						<th style="padding:3px;">Tax</th>				
						<th style="padding:3px;">Total</th>
					</tr>
				</tbody>
					<c:forEach var="receipt" items="${receipts}"  varStatus="status">
					<tr>
						<td style="padding:3px;">
							${status.count} ??
						</td>
						<td style="padding:3px;" title="${receipt.description}">
							<spring:eval expression="receipt.title" />
						</td>
						<td style="padding:3px;">
							<spring:eval expression="receipt.receiptDate" />
						</td>
						<td style="padding:3px;" align="right">
							<spring:eval expression="receipt.tax" />
						</td>
						<td style="padding:3px;" align="right">
							<a href="${pageContext.request.contextPath}/showreceipt.htm?id=${receipt.id}&uid=${user.id}">
								<spring:eval expression="receipt.total" />				
							</a>
						</td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
			</c:if>
		</div>
		<div id="tabs-2">			
			
		</div>
		<div id="tabs-3">
		
		</div>
</body>
</html>