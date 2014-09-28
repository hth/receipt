I recently upgraded my Mac to use Apple’s latest operating system, Mountain Lion, and in so doing, wiped out my postfix configuration that I had previously set up with advice from many different blogs. Postfix is essential for my line of business where I send many emails through MAMP on my local machine.

After hours of updating settings and getting nowhere, I stumbled across a blog post that set me in the right direction.

Rather than do this process again when Apple releases a new upgrade, I decided to document the steps I took to get this working (more for a reference for myself, but also to help those with the same issues).

I personally use Google Apps, so this process is for those also trying to authenticate through Gmail.  This process is much simpler if you don’t need SSL encryption when sending mail.  I use TextMate as a text editor, so the following command line options use the ‘mate’ command.  Alternatively, you can use ‘vi’, ‘vim’ or ‘nano’ depending on what you are familiar with.

Step 1

First, we need to create a Simple Authentication and Security Layer (SASL) password file.  To do this, open up Terminal and enter in the following command:

	sudo mate /etc/postfix/sasl_passwd

You will be required to enter your root password to edit the file. Enter in the following information, replacing <username@domain.com> with your google apps username and domain or your gmail account, and <password> with your password:

Change file content in 'sasl_passwd'

	smtp.gmail.com:587 receiptofi@gmail.com:passwd

Save and exit.

Step 2

Next, we need to create a postfix lookup for our new SASL password file.  To do this, type the following command in the Terminal:

	sudo postmap /etc/postfix/sasl_passwd

Step 3

Then we need to configure Postfix’s main.cf file which has all the configuration information for Postfix.  I would recommend backing up this file in case anything happens.  The first line of the following commands does just that, by creating a copy before editing it:

	sudo cp /etc/postfix/main.cf /etc/postfix/main.cf.orig
	sudo mate /etc/postfix/main.cf

Most of the information here is commented out.  You just need to scroll down to the bottom and paste in the following lines:

	# Minimum Postfix-specific configurations.
	mydomain_fallback = localhost
	mail_owner = _postfix
	setgid_group = _postdrop
	relayhost=smtp.gmail.com:587

	# Enable SASL authentication in the Postfix SMTP client.
	smtp_sasl_auth_enable=yes
	smtp_sasl_password_maps=hash:/etc/postfix/sasl_passwd
	smtp_sasl_security_options=

	# Enable Transport Layer Security (TLS), i.e. SSL.
	smtp_use_tls=yes
	smtp_tls_security_level=encrypt
	tls_random_source=dev:/dev/urandom


Save and exit.

Step 4

At this point, we just need to start up Postfix with our changes.  Use the following command:

	sudo postfix start

If you entered in any information incorrectly in the main.cf file, this may throw errors.  Fix the errors and then reload Postfix:

	sudo postfix reload

Step 5

You can test this configuration by sending a test email from Terminal:

	date | mail -s test receiptofi@gmail.com

If all goes well, you should receive an email to your inbox.

Step 6

Once you have everything working, you can set Postfix to start on boot by adding a key to

	/System/Library/LaunchDaemons/org.postfix.master.plist file.

The key to add is:

	<key>RunAtLoad</key>
	<true/>

The updated file looks like this (text version below):

	<?xml version="1.0" encoding="UTF-8"?>
	<!DOCTYPE plist PUBLIC "-//Apple Computer//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
	<plist version="1.0">
	<dict>
	  <key>Label</key>
	  <string>org.postfix.master</string>
	  <key>Program</key>
	  <string>/usr/libexec/postfix/master</string>
	  <key>ProgramArguments</key>
	  <array>
		<string>master</string>
		<string>-e</string>
		<string>60</string>
	  </array>
	  <key>QueueDirectories</key>
	  <array>
		<string>/var/spool/postfix/maildrop</string>
	  </array>
	  <key>AbandonProcessGroup</key>
	  <true/>
	  <key>OnDemand</key>
	  <true/>
	  <key>RunAtLoad</key>
	  <true/>
	</dict>
	</plist>

And that’s it! You should now be able to send mail using Mac OS X and Postfix from a PHP install. I am not an expert at this by any means, but feel free to leave a comment below letting me know if this helped you or if you have any issues with it. I would be glad to help in any way I can!


