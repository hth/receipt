Download activemq
unzip

sudo mv ~/Downloads/apache-activemq-5.8.0 /usr/local/
sudo ln -s /usr/local/apache-activemq-5.8.0 /Library/ActiveMQ

sudo launchctl unload /Library/LaunchDaemons/activemq.plist
sudo launchctl load /Library/LaunchDaemons/activemq.plist
com.apache.activemq: Already loaded