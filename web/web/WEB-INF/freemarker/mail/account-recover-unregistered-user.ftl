<#assign ftlDateTime = .now>
<html>
<style type="text/css">
	@import url('http://fonts.googleapis.com/css?family=Open+Sans');

	body {
		margin: 0;
		mso-line-height-rule: exactly;
		padding: 25px;
		min-width: 90%;
		font-size: 13px;
		font-family: "Open Sans", sans-serif;
		letter-spacing: 0.02em;
		color: #555b61;
	}

	.cd-header {
		position: relative;
		background-color: #93a748;
		box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
		z-index: 1;
	}

	.cd-header:after {
		content: "";
		display: table;
		clear: both;
	}

	.cd-header #cd-logo {
		float: left;
		margin: 13px 0 0 5%;
	}

	.cd-header #cd-logo-img {
		display: block;
		height: 23px;
		width: 30px;
		background: url("../img/Receipt-30x23.png") no-repeat;
	}

	.cd-header h3 {
		width: 90%;
		margin: 0 0 0 25px;
		max-width: 10px;
		color: black;
		text-align: left;
		font-size: 1.1rem;
		-webkit-font-smoothing: antialiased;
		-moz-osx-font-smoothing: grayscale;
		position: absolute;
		left: 40px;
		top: 50%;
		bottom: auto;
		right: auto;
		-webkit-transform: translateX(-50%) translateY(-50%);
		-moz-transform: translateX(-50%) translateY(-50%);
		-ms-transform: translateX(-50%) translateY(-50%);
		-o-transform: translateX(-50%) translateY(-50%);
		transform: translateX(-50%) translateY(-50%);
	}

	.tm {
		letter-spacing: 0.05em;
		font-size: 8px !important;
		color: black;
		vertical-align: super;
	}

	@media only screen and (min-width: 768px) {
		.cd-header {
			height: 70px;
		}

		.cd-header #cd-logo {
			/*margin: 23px 0 0 5%;*/
		}

		.cd-header h3 {
			font-size: 1.5rem;
			margin: 0 0 0 30px;
			max-width: 10px;
		}

		.tm {
			font-size: 10px !important;
		}
	}
</style>

<header class="cd-header">
	<div id="cd-logo">
		<img src="cid:receiptofi.logo" alt="Receiptofi: " height="48px" width="48px"/>
	</div>
	<h3>ReceiptApp</h3>
</header>
<body>
<p>
	Hey,

</p>

<p>
	Someone requested an account recovery on Receiptofi's ReceiptApp for ${contact_email}, but we don’t have an account on this site that matches this email address.

</p>

<p>
	If you would like to create an account on Receiptofi just visit our sign-up page:
	<a href="${https}://${domain}/open/registration.htm">${https}://${domain}/open/registration.htm ></a>

</p>

<p>
	If you did not request this account recovery, just ignore this email. We'll keep your accounts safe.

</p>
<hr>
<p>
	Questions? Comments? Let us know on our feedback site.

</p>
Thanks,
<br/>
Receiptofi Customer Support

<br/><br/><br/>
<hr/>
<span class="tm">
    TM &trade; and Copyright &copy; 2015 Receiptofi Inc. Sunnyvale, CA 94085 USA. <br/>
    All Rights Reserved / <a href="https://www.receiptofi.com/privacypolicy">Privacy Policy</a>
</span>
<br/>
<span class="tm">
    S:${ftlDateTime?iso("PST")}
</span>
</body>
</html>