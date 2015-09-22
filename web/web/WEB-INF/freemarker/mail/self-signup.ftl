<#assign ftlDateTime = .now>
<html>
<body style="font-size: 15px;">
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
<br/>
<img src="cid:receiptofi.logo" alt="Receiptofi logo" height="48px" width="48px"/>


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