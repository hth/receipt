Nginx setup
===========

For [SSL creation](ssl-install/README.md) follow this documentation

Have an administrator account with Xcode pre-installed and **agreed** to Xcode agreement. 

Start with installing [homebrew](http://brew.sh "homebrew"). 

Then install nginx with help of <code>brew install nginx</code>. Once installed do not link anything yet. Open a new terminal to work on remaining steps.

**Note**: use default port **8080** and **8443**, will setup firewall redirect from **80** and **443** to nginx server ports

##### Create directory 
    /var/logs/nginx
    /var/logs/firewall

### Nginx Configuration  

Replace default **[nginx.conf](nginx.conf.md)** file with the contents listed at the link

### Firewall Configuration

Its prefered to set **root** access for firewall configuration

##### Create directory
    /usr/local/startup/firewall

Then create file with name **ipfw.nginx.sh** at <code>/usr/local/startup/firewall</code>. Populate the file with following text. And save the file. Of course set the **permissions** to file correctly and make its executable. If script is not made executable then you would see error <code>Job failed to exec(3) for weird reason: 13</code>

    sudo ipfw add 100 fwd 127.0.0.1,8080 tcp from any to me 80
    sudo ipfw add 110 fwd 127.0.0.1,8443 tcp from any to me 443
Set the file to executable and persmission set to **chown root**

    sudo chmod +x ipfw.ngnix.sh
Then create file **[ipfw.nginx.plist](ipfw.nginx.plist.md)** under directory <code>/Library/LaunchDaemons/ipfw.nginx.plist</code>, load the file with by running command 

    sudo launchctl load -w /Library/LaunchDaemons/ipfw.nginx.plist
    sudo launchctl unload -w /Library/LaunchDaemons/ipfw.nginx.plist
