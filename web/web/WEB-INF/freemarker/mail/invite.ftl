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
	Do you have receipts, expenses to track and split among friends? I found ReceiptApp to be easy and available on the go.
    I have signed up and now have all my detailed expenses at my finger tips. Travelling and shopping made simple, as
    it's paperless. My favorite feature is seeing my expenses on Google Maps.
</p>

<p>
    All you have to do is take a picture of your receipt and ReceiptApp does the rest. It's that simple.
</p>

<p>
	You are all set, just click on the link below. You will be asked to provide your first, and last name, and a
	password to access.
</p>

<p>
	<a href="${https}://${domain}/open/invite/authenticate.htm?authenticationKey=${link}">ReceiptApp Sign Up ></a>
</p>

<p>
	Or sign up using social connection. Social signup will add me to your ReceiptApp friends list to split expenses.
</p>

<p style="letter-spacing: normal">
	<a href="${https}://${domain}/open/login.htm"><img src="cid:googlePlus.logo" alt="Google Signup" /></a>
	&nbsp;&nbsp;
	<a href="${https}://${domain}/open/login.htm"><img src="cid:facebook.logo" alt="Facebook Signup" /></a>
</p>

<p>
	They are mobile too. Download free ReceiptApp on device of your choice.
</p>

<p>
	<a href="https://play.google.com/store/apps/dev?id=5932546847029461866"><img src="cid:android.logo" alt="Google Play" /></a>
	&nbsp;
	<img src="cid:ios.logo" alt="App Store" />
</p>

<p>
    Thanks, <br/>
    ${from} (${fromEmail})
</p>

<br/>
<p>
	Email sent to ${to} on behalf of your known friend ${from}.
</p>

<p>
	Receiptofi Customer Support would like to hear from you if you would not like to receive emails from us.
</p>

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