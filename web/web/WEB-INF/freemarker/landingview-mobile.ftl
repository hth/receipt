[#ftl]
[#--Line above is required else the freemarker complains--]

<html>
<head>
	<meta charset="UTF-8">
	<title></title>
	<link rel='stylesheet' type='text/css' 			href='${protocol}://${host}:${port}/${appname}/static/jquery/css/receipt.css' />
	<link rel='stylesheet' type='text/css' 			href='${protocol}://${host}:${port}/${appname}/static/external/css/jquery/jquery-ui.min.css' />

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

[#if doc.landingView.header.status?contains('FAILURE')]
<div class="ui-widget">
	<div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
		<p>
			<span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
			<span style="display:block; width:810px;">
			${doc.landingView.header.message}
			</span>
		</p>
	</div>
</div>
[#elseif doc.landingView.receipts.receipt?has_content]
[#assign counter = 1]
<div id="wrapper">
	<div id="scroller">
		<div style="padding:15px 30px;"> <!-- padding for content -->
			<table style="width: 100%" class="etable">
				<tr>
					<th style="padding: 3px;"></th>
					<th style="padding: 3px;">Business</th>
					<th style="padding: 3px;">Receipt Date</th>
					<th style="padding: 3px;">Tax</th>
					<th style="padding: 3px;">Total</th>
				</tr>
				[#foreach receipt in doc.landingView.receipts.receipt]
					<tr>
						<td style="padding: 1px; text-align: right; vertical-align: top;">
						${counter}.
							[#assign counter=counter + 1]
						</td>
						<td style="padding: 1px; vertical-align: top;">
						${receipt.bizName.businessName}
						</td>
						<td style="padding: 1px; vertical-align: top;">
						${receipt.receiptDate?datetime("yyyy-MM-dd'T'HH:mm:ssXXX")?date}
						</td>
						<td style="padding: 1px; text-align: right; vertical-align: top;">
						${receipt.tax}
						</td>
						<td style="padding: 1px; text-align: right; vertical-align: top;">
						${receipt.total}
						</td>
					</tr>
				[/#foreach]
			</table>
		</div>
	</div>
</div>
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

<script type="text/javascript">
	var myScroll = new iScroll('wrapper');
</script>
</body>
</html>