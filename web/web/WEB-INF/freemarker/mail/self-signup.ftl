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
	Dear ${to},

</p>

<p>
	You've entered ${contact_email} as the contact email address for your ReceiptApp ID. To complete the process, we
	just need to verify that this email address belongs to you. Simply click the link below and sign in using your
	Receiptofi ID and password.

</p>

<p>
	<a href="${https}://${domain}/open/validate.htm?authenticationKey=${link}">Verify Now ></a>

</p>

<p>
	<b>Wondering why you got this email?</b>
	It's sent when someone sign's up or changes a contact email address for an ReceiptApp account. If you didn't do this,
	don't worry. Your email address cannot be used as a contact address with Receiptofi's ReceiptApp without your verification.
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