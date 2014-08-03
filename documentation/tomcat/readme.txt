#Tomcat set up
http://wolfpaulus.com/jounal/mac/tomcat7

Installing Tomcat
Here are the easy to follow steps to get it up and running on your Mac
Download a binary distribution of the core module: apache-tomcat-7.0.47.tar.gz from here. I picked the tar.gz in Binary Distributions / Core section.
Opening/unarchiving the archive will create a folder structure in your Downloads folder: (btw, this free Unarchiver app is perfect for all kinds of compressed files and superior to the built-in Archive Utility.app)
~/Downloads/apache-tomcat-7.0.47
Open to Terminal app to move the unarchived distribution to /usr/local
sudo mkdir -p /usr/local
sudo mv ~/Downloads/apache-tomcat-7.0.47 /usr/local
To make it easy to replace this release with future releases, we are going to create a symbolic link that we are going to use when referring to Tomcat (after removing the old link, you might have from installing a previous version):
sudo rm -f /Library/Tomcat
sudo ln -s /usr/local/apache-tomcat-7.0.47 /Library/Tomcat
Change ownership of the /Library/Tomcat folder hierarchy:
sudo chown -R <your_username> /Library/Tomcat
Make all scripts executable:
sudo chmod +x /Library/Tomcat/bin/*.sh

#Java setup
http://www.cc.gatech.edu/~simpkins/teaching/gatech/cs2340/guides/java7-macosx.html

#Tomcat env and auto start set up
http://stackoverflow.com/questions/6897476/tomcat-7-how-to-set-initial-heap-size-correctly
http://stackoverflow.com/questions/6398053/cant-change-tomcat-7-heap-size/10950387#10950387
https://confluence.atlassian.com/display/CONF33/Start+Confluence+automatically+on+OS+X+using+launchd
http://www.manniwood.com/tomcat_stuff/index.html

#Run and check if tomcat is running
sudo launchctl load -w /Library/LaunchDaemons/receiptofi.plist
sudo launchctl unload -w /Library/LaunchDaemons/receiptofi.plist

don't forget to create directory
#file-system-location
expensofi-file-location /opt/receiptofi/expensofi