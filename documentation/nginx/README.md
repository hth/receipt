Nginx setup
===========

Have an administrator account with Xcode pre-installed and **agreed** to Xcode agreement. 

Start with installing [homebrew](http://brew.sh "homebrew"). 

Then install nginx with help of <code>brew install nginx</code>. Once installed do not link anything yet. Open a new terminal to work on remaining steps.

**Note**: use default port **8080** and **8443**, will setup firewall redirect from **80** and **443** to nginx server ports

##### Create directory 
    /var/logs/nginx
    /var/logs/firewall

### Nginx Configuration  

Replace default **nginx.conf** file with the content below

    #user  nobody;
    worker_processes  1;

    error_log  /var/logs/nginx/error.log;
    #error_log  logs/error.log  notice;
    #error_log  logs/error.log  info;

    #pid        logs/nginx.pid;


    events {
        worker_connections  1024;
    }


    http {
        include       mime.types;
        default_type  application/octet-stream;

        #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
        #                  '$status $body_bytes_sent "$http_referer" '
        #                  '"$http_user_agent" "$http_x_forwarded_for"';

        #access_log  logs/access.log  main;    
        access_log  /var/logs/nginx/host.access.log;

        sendfile        on;
        #tcp_nopush     on;

        #keepalive_timeout  0;
        keepalive_timeout  65;

        #gzip  on;

        server {
            listen       8080;
            server_name  localhost;

            #charset koi8-r;

            #access_log  logs/host.access.log  main;

            location / {
                root   html;
                index  index.html index.htm;
            }

            #error_page  404              /404.html;

            # redirect server error pages to the static page /50x.html
            #
            error_page   500 502 503 504  /50x.html;
            location = /50x.html {
                root   html;
            }

            # proxy the PHP scripts to Apache listening on 127.0.0.1:80
            #
            #location ~ \.php$ {
            #    proxy_pass   http://127.0.0.1;
            #}

            # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
            #
            #location ~ \.php$ {
            #    root           html;
            #    fastcgi_pass   127.0.0.1:9000;
            #    fastcgi_index  index.php;
            #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
            #    include        fastcgi_params;
            #}

            # deny access to .htaccess files, if Apache's document root
            # concurs with nginx's one
            #
            #location ~ /\.ht {
            #    deny  all;
            #}
        }


        # another virtual host using mix of IP-, name-, and port-based configuration
        #
        #server {
        #    listen       8000;
        #    listen       somename:8080;
        #    server_name  somename  alias  another.alias;

        #    location / {
        #        root   html;
        #        index  index.html index.htm;
        #    }
        #}

        ssl_certificate      /var/cert/2b4bac234a6b92.crt;
        ssl_certificate_key  /var/cert/receiptofi.com.key;

        # HTTPS server
        #
        server {
            listen       8443 ssl;
            server_name  localhost;

            ssl_session_cache    shared:SSL:10m;
            ssl_session_timeout  10m;

            ssl_protocols SSLv3 TLSv1;
            ssl_ciphers  HIGH:!aNULL:!MD5;
            ssl_prefer_server_ciphers  on;

            location / {
                root   html;
                index  index.html index.htm;
            }
        }

    }
    

### Firewall Configuration

##### Create directory
    /usr/local/startup/firewall

Then create file with name **ipfw.nginx.sh** at <code>/usr/local/startup/firewall</code>. Populate the file with following text. And save the file. Of course set the **permissions** to file correctly.

    sudo ipfw add 100 fwd 127.0.0.1,8080 tcp from any to me 80
    sudo ipfw add 110 fwd 127.0.0.1,8443 tcp from any to me 443
