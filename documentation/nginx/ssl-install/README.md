Nginx + GoDaddy SSL Setup
=========================

###Step 1: Create CSR – Certificate Signing Request on Nginx Server###

Create a directory to store keys & certificates for example.com domain. You can use any directory. Following example uses these conventions.

    mkdir /var/www/example.com/cert/
    cd /var/www/example.com/cert/
    
Next, create a 2048-bit private key

Finally Create a CSR (Certificate signing request)
    
    openssl req -new -newkey rsa:2048 -nodes -keyout yourdomain.key -out yourdomain.csr
    
Running this command will ask you some details. For Common Name (eg, YOUR name) []: field use example.com (or *.example.com if you are setting up a wild-card SSL certificate)

Note: www.example.com and example.com are not same. Use exactly same domain your website is using.

###Step 2: Get a SSL Certificate from GoDaddy###

- Buy a SSL certificate from GoDaddy.com.
- Paste CSR i.e. content of example.com.csr in GoDaddy web-interface. You will need to provide some more details, Try to match them to details in Step #1.
- Depending on type of certificate, it may take some time for GoDaddy to approve your certificate.
- Once certificate is approved, you can download it. For detailed instructions on downloading, please refer this.

###Step 3: Fix Intermediate Certificate Chain###

The zip file you will get from Godaddy will contain 2 files: *example.com.crt* and *gd_bundle.crt*.

One is your certificate and other is bundle i.e intermediate certificates. Nginx doesn’t have a special directive to specify path to certificate bundle/chain file. So we need to append bundle into SSL certificate file itself in a way that SSL certificate remains on top.

You can do it simply by running following command:

    cat gd_bundle.crt >> example.com.crt
or

    cat sf_bundle-g2-g1.crt >> 2bf57.crt 
Move this *example.com.crt* file to <code>/var/www/example.com/cert/directory</code> on nginx server.

###Step 4: Adjusting Nginx Configuration###

Enable SSL for example.com

Make it look like below:

    server {
        listen 443;
        server_name example.com;
        ssl on;
        ssl_certificate /var/www/example.com/cert/example.com.crt;
        ssl_certificate_key /var/www/example.com/cert/example.com.key;
     #... other stuff
    }
Force non SSL site to redirect traffic to SSL

Add following codes if you want to force SSL on your site.

    server {
        listen 80;
        server_name example.com;
        return 301 https://example.com$request_uri;
    }
Turn on SSL session cache for performance

In file */etc/nginx/nginx.conf*, inside <code>http {..}</code> block add following:

    http {
        ssl_session_cache   shared:SSL:10m;
        ssl_session_timeout 10m;
        #... other stuff
    }
Also make sure value of worker_processes directive is greater than 1 (only if your server has multiple cores).

Finally, reload the processes to make the change take effect.

    service nginx reload

###Step-4: Ask WordPress to use SSL###

Add following to you WordPress’s wp-config.php file.

To force SSL for login form:

    define('FORCE_SSL_LOGIN', true);

To force SSL for wp-admin section:

    define('FORCE_SSL_ADMIN', true);

###Step-5: Verifying SSL Installation###

Last and most important step is to verify if we have installed SSL certificate properly.

Below are some nice online tools to help you with that:

https://sslcheck.casecurity.org/en_US <br/>
https://www.wormly.com/test_ssl <br/>
https://sslcheck.globalsign.com/en_US/sslcheck

If you face any issues, feel free to use our free support forum.
