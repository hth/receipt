<#assign ftlDateTime = .now>
<html>
<body style="font-size: 15px;">
<p>
	Dear ${to},

</p>

<p>
	We have completed your registration and your account is now active. Please click on the link below to login.

</p>

<p>
	<a href="${https}://${domain}/open/login.htm">Login now ></a>

</p>

<p>
	<b>Wondering why you got this email?</b>
	It's sent when someone sign's up when registration was not allowed and now you have been selected and this marks as completion of your registration.
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