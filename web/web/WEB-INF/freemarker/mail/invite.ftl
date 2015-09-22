<#assign ftlDateTime = .now>
<html>
<body style="font-size: 15px;">
<p>
    Hey,
</p>

<p>
    I would like you to join ReceiptApp. It is a great place to manage and analyze your receipts and expenses month over
    month. Easy to compare, on the go 24/7, at your finger tips, paperless. Fun to track expenses with mapped location.
</p>

<p>
    You are all set, just click on the link below. You will be asked to provide your first, and last name, and a
    password to access.
</p>

<p>
    <a href="${https}://${domain}/open/invite/authenticate.htm?authenticationKey=${link}">ReceiptApp Sign Up ></a>
</p>

<p>
	Or sign up using social connection to connect with your friends to split expenses and share receipts.
</p>

<p>
    <a href="${https}://${domain}/open/login.htm"><img src="cid:googlePlus.logo" alt="Google Signup" height="48px" width="48px"/></a>
    &nbsp;&nbsp;
    <a href="${https}://${domain}/open/login.htm"><img src="cid:facebook.logo" alt="Facebook Signup" height="48px" width="48px"/></a>
</p>

<p>
    Thanks,
</p>

<p>
${from} (${fromEmail})
</p>

<p>
    Email sent to ${to} on behalf of your known friend ${from}.
</p>

<p>
    Receiptofi Customer Support would like to hear from you if you would not like to receive emails from us.
</p>
<img src="cid:receiptofi.logo" alt="Receiptofi Logo" height="48px" width="48px"/>


<br/><br/><br/>
<span style="font-size: 9px;">
    This email is intended solely for the individual or entity to which it is addressed. If you are not the intended recipient of this email, you should know that any dissemination, distribution, copying, or action taken in relation to this email's contents or attachments is prohibited and may be unlawful. If you received this email in error, please notify the sender immediately and delete all electronic and hard copies of both the e-mail and its attachments. Thank you.
</span>
<br/>
<span style="font-size: 9px;">
    Time: ${ftlDateTime?iso("PST")}
</span>
</body>
</html>