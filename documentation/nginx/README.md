Nginx setup
===========

For (SSL configuration)[ssl-install/README.md] follow this documentation

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

##### Create directory
    /usr/local/startup/firewall

Then create file with name **ipfw.nginx.sh** at <code>/usr/local/startup/firewall</code>. Populate the file with following text. And save the file. Of course set the **permissions** to file correctly.

    sudo ipfw add 100 fwd 127.0.0.1,8080 tcp from any to me 80
    sudo ipfw add 110 fwd 127.0.0.1,8443 tcp from any to me 443
