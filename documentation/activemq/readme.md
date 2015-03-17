### ActiveMQ Setup

Use brew to get ActiveMQ or Download activemq
unzip
note: chown root:wheel (always root)


	sudo mv ~/Downloads/apache-activemq-5.11.1 /usr/local/
	sudo rm /Library/ActiveMQ
	sudo ln -s /usr/local/Cellar/activemq/5.11.1 /Library/ActiveMQ
	
And then change owner ship to local user ***db***

	sudo chown -R db /Library/ActiveMQ
	
Create activemq.plisl and let the owner be root
	
	sudo nano activemq.list

Load 	

	sudo launchctl unload -w /Library/LaunchDaemons/activemq.plist
	sudo launchctl load -w /Library/LaunchDaemons/activemq.plist

For local environment start active mq by

	/usr/local/Cellar/activemq/5.11.1/bin/activemq start
