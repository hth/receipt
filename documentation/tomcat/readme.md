#Tomcat set up
http://wolfpaulus.com/jounal/mac/tomcat7

#### Installing Tomcat
- Here are the easy to follow steps to get it up and running on your Mac
- Download a binary distribution of the core module: apache-tomcat-7.0.47.tar.gz from here. I picked the tar.gz in Binary Distributions / Core section.
- Opening/unarchiving the archive will create a folder structure in your Downloads folder: (btw, this free Unarchiver app is perfect for all kinds of compressed files and superior to the built-in Archive Utility.app)

        ~/Downloads/apache-tomcat-7.0.47
    
- Open to Terminal app to move the unarchived distribution to /usr/local

        sudo mkdir -p /usr/local
        sudo mv ~/Downloads/apache-tomcat-7.0.47 /usr/local
    
- To make it easy to replace this release with future releases, we are going to create a symbolic link that we are going to use when referring to Tomcat (after removing the old link, you might have from installing a previous version):

        sudo rm -f /Library/Tomcat
        sudo ln -s /usr/local/apache-tomcat-7.0.47 /Library/Tomcat
    
- Added `setenv.sh` and `launchd_wrapper.sh` script
- Change ownership of the /Library/Tomcat folder hierarchy:

        sudo chown -R db /Library/Tomcat
    
- Make all scripts executable:

        sudo chmod +x /Library/Tomcat/bin/*.sh

http://stas-blogspot.blogspot.ch/2011/07/most-complete-list-of-xx-options-for.html

### Stop request to tomcat before unloading
Tomcat supports unloadDelay which waits till the configured time for unloading servlets.
This should configured in `context.xml` as follows: 20 seconds below

    <Context unloadDelay="20000">

### Remove whitespaces
If your servlet container doesn't support the JSP 2.1 trimDirectiveWhitespaces property, then you need to consult its
JspServlet documentation for any initialization parameters. In for example Tomcat, you can configure it as well by
setting trimSpaces init-param to true in for JspServlet in Tomcat's /conf/web.xml:

    <init-param>
        <param-name>trimSpaces</param-name>
        <param-value>true</param-value>
    </init-param>

A completely different alternative is the JTidyFilter. It not only trims whitespace, but it also formats HTML in a
correct indentation.

#### Java setup
- http://www.cc.gatech.edu/~simpkins/teaching/gatech/cs2340/guides/java7-macosx.html

#### Set JAVA 8 symb link from above step

For anyone else having this problem you need to reboot your mac and press `cmd+r` when booting up. 
Then go into `utilities > terminal` and type the following commands:

    csrutil disable
    reboot 

After reboot

    sudo rm /usr/bin/java
    sudo ln -s /Library/Java/JavaVirtualMachines/jdk1.8.0_92.jdk/Contents/Home/bin/java /usr/bin/java
    sudo rm /Library/Java/Home
    sudo ln -s /Library/Java/JavaVirtualMachines/jdk1.8.0_92.jdk/Contents/Home /Library/Java/Home
    
Check
    
    ls -l /usr/bin/java

#### Tomcat env and auto start set up
- http://stackoverflow.com/questions/6897476/tomcat-7-how-to-set-initial-heap-size-correctly
- http://stackoverflow.com/questions/6398053/cant-change-tomcat-7-heap-size/10950387#10950387
- https://confluence.atlassian.com/display/CONF33/Start+Confluence+automatically+on+OS+X+using+launchd
- http://www.manniwood.com/tomcat_stuff/index.html

#### Create file receiptofi.plist and let the owner be root

    sudo nano /Library/LaunchDaemons/receiptofi.plist

#### Run and check if tomcat is running (plist files are rw-r-r)

    sudo launchctl load -w /Library/LaunchDaemons/receiptofi.plist
    sudo launchctl unload -w /Library/LaunchDaemons/receiptofi.plist

#### don't forget to create directory - file-system-location

    expensofi-file-location /opt/receiptofi/expensofi
