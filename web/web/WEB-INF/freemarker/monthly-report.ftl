[#ftl]
[#--Line above is required else the freemarker complains--]

<html>
<head>
	<meta charset="UTF-8">
	<title></title>
	<link rel="icon" type="image/x-icon" 			href="${protocol}://${host}:${port}/${appname}/static/images/circle-leaf-sized_small.png" />
	<link rel="shortcut icon" type="image/x-icon" 	href="${protocol}://${host}:${port}/${appname}/static/images/circle-leaf-sized_small.png" />

	<link rel='stylesheet' type='text/css' 			href='${protocol}://${host}:${port}/${appname}/static/jquery/css/receipt.css' />
	<link rel='stylesheet' type='text/css' 			href='${protocol}://${host}:${port}/${appname}/static/jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css' />

	<style type="text/css">
		#headerDiv, #contentDiv {
			float: left;
			width: 202px;
		}
		#titleText {
			float: left;
			font-size: 0.9em;
			font-weight: bold;
			margin: 6px 6px;
		}
		#myHeader {
			font-size: 0.9em;
			font-weight: bold;
			margin: 1px;
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
			margin: 1px 1px 1px 1px;
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
				text.innerHTML =
						'<ul id="icons" class="ui-widget ui-helper-clearfix">' +
							'<li class="ui-state-default ui-corner-all" title="Show"><span class="ui-icon ui-icon-circle-triangle-s"></span></li>' +
						'</ul>';
			} else {
				ele.style.display = "block";
				text.innerHTML =
						'<ul id="icons" class="ui-widget ui-helper-clearfix">' +
							'<li class="ui-state-default ui-corner-all" title="Hide"><span class="ui-icon ui-icon-circle-triangle-n"></span></li>' +
						'</ul>';
			}
		}
	</script>
</head>
<body>
<div class="divTable" style="width: 810px">
	<div class="divRow">
		<div class="divOfCell250" style="height: 46px"><img src="${protocol}://${host}:${port}/${appname}/static/images/receipt-o-fi.logo.jpg" alt="receipt-o-fi logo" style="height: 40px"/></div>
		<div class="divOfCell250">
			<h3>
				<div class="dropdown" style="height: 17px">
					<div>
						<a class="account" style="color: #065c14">
						${doc.reportView.emailId}
						</a>
					</div>
				</div>
			</h3>
		</div>
	</div>
</div>

[#if doc.reportView.header.status?contains('FAILURE')]
<div class="ui-widget">
	<div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
		<p>
			<span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
			<span style="display:block; width:810px;">
			${doc.reportView.header.message}
			</span>
		</p>
	</div>
</div>
[#elseif doc.reportView.receipts.receipt?has_content]
[#assign counter = 1]
<table style="width: 810px" class="etable">
	<tr>
		<th style="padding: 3px;"></th>
		<th style="padding: 3px;">Business</th>
		<th style="padding: 3px;">Receipt Date</th>
		<th style="padding: 3px;">Tax</th>
		<th style="padding: 3px;">Total</th>
		<th></th>
	</tr>
[#foreach receipt in doc.reportView.receipts.receipt]
	<tr>
		<td style="padding: 8px; text-align: right; vertical-align: top;">
			${counter}.
			[#assign counter=counter + 1]
		</td>
		<td style="padding: 8px; vertical-align: top;">
			${receipt.bizName.businessName}
		</td>
		<td style="padding: 8px; vertical-align: top;">
			${receipt.receiptDate?datetime("yyyy-MM-dd'T'HH:mm:ssXXX")?date}
		</td>
		<td style="padding: 8px; text-align: right; vertical-align: top;">
			${receipt.tax}
		</td>
		<td style="padding: 8px; text-align: right; vertical-align: top;">
			${receipt.total}
		</td>
		<td style="padding: 3px; vertical-align: top; width: 204px">
			<div id="headerDiv">
				<div id="titleText">Show Receipt</div>
				<a id="myHeader" href="javascript:toggle2('myContent','myHeader');">
					<ul id="icons" class="ui-widget ui-helper-clearfix">
						<li class="ui-state-default ui-corner-all" title="Show"><span class="ui-icon ui-icon-circle-triangle-s"></span></li>
					</ul>
				</a>
			</div>
			<div style="clear:both;"></div>
			<div id="contentDiv">
				<div id="myContent" style="display: block;">
					[#foreach fileSystemEntity in receipt.fileSystemEntities]
					<img src="${protocol}://${host}:${port}/${appname}/access/filedownload/receiptimage/${fileSystemEntity.blobId}.htm" style="width: 200px; height: 200px"/>
					[/#foreach]
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