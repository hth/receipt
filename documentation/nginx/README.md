Nginx setup
===========

For [SSL creation](ssl-install/README.md) follow this documentation

Have an administrator account with Xcode pre-installed and **agreed** to Xcode agreement. 

Start with installing [homebrew](http://brew.sh "homebrew"). 

Then install nginx with help of <code>brew install nginx</code>. Once installed do not link anything yet. Open a new terminal to work on remaining steps.

Then execute these commands to start <code>ngnix</code>, this would be listed when brew installs ***nginx***

    launchctl unload -w ~/Library/LaunchAgents/homebrew.mxcl.nginx.plist
    launchctl load -w ~/Library/LaunchAgents/homebrew.mxcl.nginx.plist

**Note**: use default port **8080** and **8443**, will setup firewall redirect from **80** and **443** to nginx server ports

##### Create directory 
    /var/logs/nginx
    /var/logs/firewall          (set chown to root)
    /var/logs/receiptofi
    /var/logs/mongo
    /var/logs/activemq
    /var/logs/logstash
    /opt/receiptofi/expensofi   (for generating reports)

### Nginx Configuration  

Replace default **[nginx.conf](nginx.conf.md)** file with the contents listed at the link

### Firewall Configuration

Its preferred to set **root** access for firewall configuration

##### Create directory
    /usr/local/startup/firewall
    
In theory and practice, let the router's firewall do the port forwarding from 80 to 8080 and 443 to 8443. This firewall script will help internally to reach the host on port 80. Otherwise, will have to enter port number at each request internally. 

Do not forget to do periodic port scan.

Then create file with name **ipfw.nginx.sh** at <code>/usr/local/startup/firewall</code>. Populate the file with following text. And save the file. Of course, set the **permissions** to file correctly and make it executable. If script is not made executable then you would see error <code>Job failed to exec(3) for weird reason: 13</code>

    sudo ipfw add 100 fwd 127.0.0.1,8080 tcp from any to me 80
    sudo ipfw add 110 fwd 127.0.0.1,8443 tcp from any to me 443
    sudo ipfw add 120 allow tcp from 192.168.1.74 to any dst-port 6379
    
<code>port 6379</code> is for logstash    
    
Set the file to executable and persmission set to **chown root**

    sudo chmod +x ipfw.ngnix.sh
Then create file **[ipfw.nginx.plist](ipfw.nginx.plist.md)** under directory <code>/Library/LaunchDaemons/ipfw.nginx.plist</code>, load the file with by running command. Let the owner be root.
    
    sudo nano /Library/LaunchDaemons/ipfw.nginx.plist
    
Then execute these commands to start <code>ngnix firewall</code>

    sudo launchctl load -w /Library/LaunchDaemons/ipfw.nginx.plist
    sudo launchctl unload -w /Library/LaunchDaemons/ipfw.nginx.plist
    
When firewall is not configured, you would see error like 

    curl -ik -X POST -H "Content-Type: application/json" -d '{"pid": "FACEBOOK","at": "C"}' https://test.receiptofi.com/receipt-mobile/authenticate.json
    curl: (7) Failed connect to test.receiptofi.com:443; Connection refused
    
#### Static www for nginx
Create directory <code>/data/www</code> matching <code>root</code> mapping in **[nginx.conf](nginx.conf.md)**. Then add file <code>index.html</code> inside this newly created directory. Do not forget to create **[robots.txt](robots.txt.md)** at root level.

#### Subdomain support
Add **A Host** to domain provider. Example below
- receiptofi.com, (already exists as primary domain, hence no change here. Static site)
- prod.receiptofi.com, 
- test.receiptofi.com,
- *.m.receiptofi.com (on hold, until standalone server is deployed with mobile web app)

Note: **m** for mobile site

Once subdomain are registered, add references in **[nginx.conf](nginx.conf.md)**. Also, look out for firewall settings. Try <code>curl</code> from nginx host to validated working of Tomcat instance on another host. 

**Note**: Curl check does not prove firewall settings are working. But seems the issue was with the ssl certificate not able to recognize hostname as <code>/etc/hosts</code> did not contain the correct hostname. Make sure hostname exists, else <code>ssl</code> will reject the call silently when called from browser. This can be verfied with <code>httpie</code> installed using **<code>brew install httpie</code>**. Message was clear when the call was executed using <code>httpie</code>

Httpie call to verify if server responds correctly. Looks like hostname mis-match occured. Another thing to note is that the call between <code>nginx</code> and <code>Tomcat</code> would always be insecure. SSL is on <code>nginx</code>, so the call below is of less help but it did help point me in correct direction.

    http -v https://prod.receiptofi.com
    http: error: SSLError: hostname 'prod.receiptofi.com' doesn't match 'receiptofi.com'

Curl command to check is everything working fine.

    curl http://192.168.1.75:8080 -H "host: prod.receiptofi.com" -v
    curl http://192.168.1.75:8080/receipt-mobile/ -H "host: prod.receiptofi.com" -v
