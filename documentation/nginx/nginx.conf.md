    #Date: Aug 31 4:30 PM
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

        log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                          '$status $body_bytes_sent "$http_referer" '
                          '"$http_user_agent" "$http_x_forwarded_for"';

        # Sendfile copies data between one FD and other from within the kernel.
        # More efficient than read() + write(), since the requires transferring data to and from the user space.
        sendfile       on;

        # Tcp_nopush causes nginx to attempt to send its HTTP response head in one packet,
        # instead of using partial frames. This is useful for prepending headers before calling sendfile,
        # or for throughput optimization.
        tcp_nopush     on;

        # Timeout for keep-alive connections. Server will close connections after this time.
        keepalive_timeout  65;

        # Compression. Reduces the amount of data that needs to be transferred over the network
        gzip on;
        gzip_min_length 10240;
        gzip_proxied    expired no-cache no-store private auth;
        gzip_types      text/plain text/css application/json application/x-javascript text/xml application/xml application/xml+rss text/javascript;
        gzip_disable    "MSIE [1-6]\.";

        server {
            listen       8080;
            server_name  localhost receiptofi.com;

            #charset koi8-r;

            access_log  /var/logs/nginx/prod.access.log main;

            location / {
                root   /data/www;
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
            server_name  localhost receiptofi.com;

            access_log  /var/logs/nginx/prod.access.log main;

            ssl_session_cache    shared:SSL:10m;
            ssl_session_timeout  10m;

            ssl_protocols SSLv3 TLSv1;
            ssl_ciphers  HIGH:!aNULL:!MD5;
            ssl_prefer_server_ciphers  on;

            location / {
                root   /data/www;
                index  index.html index.htm;
            }
        }

    }
