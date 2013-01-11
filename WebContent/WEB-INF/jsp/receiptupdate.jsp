<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><fmt:message key="receipt.update" /></title>		
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
		.atable{
			border-collapse:collapse;
			border:1px solid #AAA;
			margin-left:10px;
		}
		.atable th{
			border:1px solid #AAF;
			background:#BFBFFF;
			font-weight:bold;
		}
		.atable td{
			padding:4px;
			border:1px solid #AAF;
		}
		.oddRow{
			background:#FFFFFF;
		}
		.evenRow{
			background:#DFDFFF;
		}
	</style>

	<script type="text/javascript">
		$("document").ready(function(){
			$(".alternativeRow").btnAddRow({oddRowCSS:"oddRow",evenRowCSS:"evenRow"});
			$(".delRow").btnDelRow();
		});
	</script>
	
	<style>
		.error {
			color: red;
		}
	</style>
	
	<style type="text/css">
		.leftAlign {
	    	float: left;
		}
		.rightAlign {
	    	float: right;
		}
	</style>

	<script type="text/javascript">
		$("document").ready(function(){
			$(".alternativeRow").btnAddRow({oddRowCSS:"oddRow",evenRowCSS:"evenRow"});
			$(".delRow").btnDelRow();
		});
	</script>
</head>
<body>	
	<div>
		<p>User Id ${userSession.emailId}</p>
	</div>
	
	<br/>

	<table>
		<tr>
			<td valign="top">
				<form:form method="post" action="receiptupdate.htm" modelAttribute="receiptForm">
				<form:hidden path="receipt.receiptBlobId"/>
				<form:hidden path="receipt.id"/>
				<form:hidden path="receipt.userProfileId"/>
				<form:hidden path="receipt.receiptOCRTranslation"/>
				<form:hidden path="receipt.version"/>
				<table border="0" style="width: 400px" class="atable">					
					<tr>
						<td colspan="4">
							<div class="leftAlign"><form:errors path="receipt.title" cssClass="error" /></div>
							<div class="rightAlign"><form:errors path="receipt.receiptDate" cssClass="error" /></div>
						</td>
					</tr>
					<tr>
						<td colspan="4">
							<div class="leftAlign">Title <form:input path="receipt.title" size="32"/></div>
							<div class="rightAlign">Date <form:input path="receipt.receiptDate" size="32"/></div>
						</td>
					</tr>
					<tr>
						<th align="left">&nbsp;Name</th>
						<th align="left">&nbsp;Price</th>
						<th align="left">&nbsp;</th>
					</tr>
					<c:forEach items="${receiptForm.items}" varStatus="status">
					<tr>
						<td align="left">
				    		<form:input path="items[${status.index}].name" size="64"/>
						</td>
						<td align="right">
							<form:input path="items[${status.index}].price" size="16"/>
						</td>
						<td>
							<form:select path="items[${status.index}].taxed"> 
								<form:option value="NONE" label="--- Select ---"/>
								<form:options itemValue="name" itemLabel="description" />
							</form:select>
						</td>
					</tr>
					</c:forEach>
					<tr>
						<td colspan="1" align="right">
							<span>Tax &nbsp;</span> 
							<form:input path="receipt.tax" size="5"/>
							<span>&nbsp;&nbsp;Total &nbsp;</span>
						</td>
						<td align="right">
							<form:input path="receipt.total" size="5"/>
						</td>
					</tr>
					<tr>
						<td colspan="1">&nbsp;</td>
						<td colspan="2" align="left"><input type="submit" value="Receipt Update" name="receipt_update"/></td>
					</tr>
				</table>
				</form:form>
			</td>
			<td width="6px">&nbsp;</td>
			<td valign="top">			 
			 	<img src="${pageContext.request.contextPath}/receiptimage.htm?id=${receiptForm.receipt.receiptBlobId}" width="600px" height="600px"/>			 
			</td>
		</tr>
	</table>
</body>
</html>