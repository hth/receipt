[#ftl]
[#--Line above is required else the freemarker complains--]

<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
	<link rel="icon" type="image/x-icon" 			href="${protocol}://${host}:${port}/${appname}/images/circle-leaf-sized_small.png" />
	<link rel="shortcut icon" type="image/x-icon" 	href="${protocol}://${host}:${port}/${appname}/images/circle-leaf-sized_small.png" />

	<link rel='stylesheet' type='text/css' 			href='${protocol}://${host}:${port}/${appname}/jquery/css/receipt.css' />
	<link rel='stylesheet' type='text/css' 			href='${protocol}://${host}:${port}/${appname}/jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css' />

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
		<div class="divOfCell250" style="height: 46px"><img src="${protocol}://${host}:${port}/${appname}/images/receipt-o-fi.logo.jpg" alt="receipt-o-fi logo" style="height: 40px"/></div>
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

</body>
</html>