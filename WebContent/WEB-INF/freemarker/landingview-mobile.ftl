[#ftl]
[#--Line above is required else the freemarker complains--]

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
<!-- Refer css in mobile app -->
<div id="wrapper">
	<div id="scroller">
		<div class="table">
			<div class="tbody">
				<div class="tr">
					<div class="td"></div>
					<div class="td">Business</div>
					<div class="td">Date</div>
					<div class="td">Total</div>
				</div>
				[#foreach receipt in doc.landingView.receipts.receipt]
				<div class="tr">
					<div class="td">
						${counter}.
					</div>
					<div class="td">
						[#--<#if ${receipt.bizName.name?length} &lt; 27>--]
							[#----]
						[#--<#else>--]
							[#--${receipt.bizName.name?substring(0,26)} ...--]
						[#--</#if>--]
						${receipt.bizName.name}
					</div>
					<div class="td">
						${receipt.receiptDate?datetime("yyyy-MM-dd'T'HH:mm:ssXXX")?date}
					</div>
					<div class="td">
						${receipt.total}
					</div>
				</div>
				[#assign counter=counter + 1]
			[/#foreach]
			</div>
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