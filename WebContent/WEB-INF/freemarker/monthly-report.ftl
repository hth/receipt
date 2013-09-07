[#ftl]

<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
	<link rel="icon" type="image/x-icon" 			href="${http}://${host}:${port}/${appname}/images/circle-leaf-sized_small.png" />
	<link rel="shortcut icon" type="image/x-icon" 	href="${http}://${host}:${port}/${appname}/images/circle-leaf-sized_small.png" />

	<link rel='stylesheet' type='text/css' 			href='${http}://${host}:${port}/${appname}/jquery/css/receipt.css' />
	<link rel='stylesheet' type='text/css' 			href='${http}://${host}:${port}/${appname}/jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css' />

	<style type="text/css">
		#headerDiv, #contentDiv {
			float: left;
			width: 202px;
		}
		#titleText {
			float: left;
			font-size: 1.1em;
			font-weight: bold;
			margin: 5px;
		}
		#myHeader {
			font-size: 1.1em;
			font-weight: bold;
			margin: 5px;
		}
		#headerDiv {
			background-color: #065c14;
			color: #FFFFFF;
		}
		#contentDiv {
			background-color: #FFE694;
		}
		#myContent {
			margin: 1px 1px;
		}
		#headerDiv a {
			float: right;
			margin: 10px 10px 5px 5px;
		}
		#headerDiv a:hover {
			color: #FFFFFF;
		}
	</style>

	<script type="text/javascript">
		function toggle2(showHideDiv, switchTextDiv) {
			var ele = document.getElementById(showHideDiv);
			var text = document.getElementById(switchTextDiv);
			if(ele.style.display == "block") {
				ele.style.display = "none";
				text.innerHTML = '<img src="${http}://${host}:${port}/${appname}/images/gear.png" width="18px" height="15px" style="float: right;"/>';
			} else {
				ele.style.display = "block";
				text.innerHTML = '<img src="${http}://${host}:${port}/${appname}/images/gear.png" width="18px" height="15px" style="float: right;"/>';
			}
		}
	</script>
</head>
<body>
<div class="divTable" style="width: 810px">
	<div class="divRow">
		<div class="divOfCell250" style="height: 46px"><img src="${http}://${host}:${port}/${appname}/images/receipt-o-fi.logo.jpg" alt="receipt-o-fi logo" style="height: 40px"/></div>
		<div class="divOfCell250">
			<h3>
				<div class="dropdown" style="height: 17px">
					<div>
						<a class="account" style="color: #065c14">
						${doc.landingView.emailId}
						</a>
					</div>
				</div>
			</h3>
		</div>
	</div>
</div>

[#if doc.landingView.header.status?contains('FAILURE')]
<div class="ui-widget">
	<div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
		<p>
			<span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
			<span style="display:block; width:790px;">
			${doc.landingView.header.message}
			</span>
		</p>
	</div>
</div>
[#elseif doc.landingView.receipts.receipt?has_content]
<table style="width: 790px" class="etable">
	<tr>
		<th style="padding: 3px;"></th>
		<th style="padding: 3px;">Business</th>
		<th style="padding: 3px;">Receipt Date</th>
		<th style="padding: 3px;">Tax</th>
		<th style="padding: 3px;">Total</th>
		<th></th>
	</tr>
[#foreach receipt in doc.landingView.receipts.receipt]
	<tr>
		<td style="padding: 3px; text-align: right; vertical-align: top;">&nbsp;</td>
		<td style="padding: 3px; vertical-align: top;">
			${receipt.bizName.name}
		</td>
		<td style="padding: 3px; vertical-align: top;">
			${receipt.receiptDate}
		</td>
		<td style="padding: 3px; text-align: right; vertical-align: top;">
			${receipt.tax}
		</td>
		<td style="padding: 3px; text-align: right; vertical-align: top;">
			${receipt.total}
		</td>
		<td style="padding: 3px; vertical-align: top; width: 204px">
			<div id="headerDiv">
				<div id="titleText">Hide/Show Receipt</div>
				<a id="myHeader" href="javascript:toggle2('myContent','myHeader');">
				<img src="${http}://${host}:${port}/${appname}/images/gear.png" width="18px" height="15px" style="float: right;"/>
				</a>
			</div>
			<div style="clear:both;"></div>
			<div id="contentDiv">
				<div id="myContent" style="display: block;">
					<img src="${http}://${host}:${port}/${appname}/receiptimage.htm?id=${receipt.receiptBlobId}" style="width: 200px; height: 200px"/>
				</div>
			</div>
		</td>
	</tr>
[/#foreach]
</table>
[#else]
<div class="ui-widget">
	<div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
		<p>
			<span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
			<span style="display:block; width:410px;">
				No receipt(s) submitted or transformed for this month.
			</span>
		</p>
	</div>
</div>
[/#if]
</body>
</html>