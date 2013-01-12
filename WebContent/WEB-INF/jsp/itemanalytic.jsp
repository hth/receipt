<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><fmt:message key="item.analytic.title" /></title>
	
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
	<div>
		<p>User Id  ${sessionScope['userSession'].emailId} </p>		
	</div>
	
	<br/>
	
	<table border="1" style="background-color:#c5c021;border:1px dotted black;width:450px;border-collapse:collapse;">
		<tbody>
			<tr style="background-color:orange;color:white;">			
				<th style="padding:3px;">Name</th>
				<th style="padding:3px;">Price</th>
				<th style="padding:3px;">Average Price</th>
			</tr>	
		</tbody>
		<tr>
			<td align="left">
	    		${item.name}
			</td>
			<td align="right">
	    		<spring:eval expression="item.price" />
	    		&nbsp;
			</td>
			<td align="right">
				<spring:eval expression="averagePrice"/>
				&nbsp;
			</td>
		</tr>
	</table>
	
</body>
</html>