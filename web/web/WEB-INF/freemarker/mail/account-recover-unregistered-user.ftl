<#assign ftlDateTime = .now>
<html>
<body style="font-size: 15px;">
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