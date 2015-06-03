<#assign ftlDateTime = .now>
<html>
<body>
<p>
    Dear ${to},

</p>

<p>
    To reset your Receiptofi ID password, simply click the link below. That will take you to a web page where you can
    create a new password.

</p>

<p>
    Please note that the link will expire three hours after this email was sent.

</p>

<p>
    <a href="${https}://${domain}/open/forgot/authenticate.htm?authenticationKey=${link}">Reset your Receiptofi account password ></a>

</p>
<hr>
<p>
    If you weren't trying to reset your password, don't worry - your account is still secure and no one has been given
    access to it. Most likely, someone just mistyped their email address while trying to reset their own password.

</p>
Thanks,
<br/>
Receiptofi Customer Support
<br/>
<img src="cid:receiptofi.logo" alt="Receiptofi logo" height="40px" width="240px"/>


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