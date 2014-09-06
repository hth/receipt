### ActiveMQ Setup

Use brew to get ActiveMQ or Download activemq
unzip
note: chown root:wheel (always root)


	sudo mv ~/Downloads/apache-activemq-5.8.0 /usr/local/
	sudo ln -s /usr/local/apache-activemq-5.8.0 /Library/ActiveMQ

	sudo launchctl unload /Library/LaunchDaemons/activemq.plist
	sudo launchctl load /Library/LaunchDaemons/activemq.plist

For local environment start active mq by

	/usr/local/Cellar/activemq/5.9.0/bin/activemq start
