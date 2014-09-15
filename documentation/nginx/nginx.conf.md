    # Date: Sep 13 7:30 PM
    # https://www.digitalocean.com/community/tutorials/how-to-optimize-nginx-configuration
    # user  nobody;
    # IP Address 192.168.1.71 is related to the nginx installed ip
    worker_processes  1;

    error_log  /var/logs/nginx/error.log;
    #error_log  logs/error.log  notice;
    #error_log  logs/error.log  info;

    #pid        logs/nginx.pid;


    events {
        worker_connections  1024;
    }


    http {
        # Black listed ips bots resides here
        include         blockips.conf;
        include         ip.blocked.conf;

        include         mime.types;
        default_type    application/octet-stream;

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
        keepalive_timeout  70;

        # Compression. Reduces the amount of data that needs to be transferred over the network
        gzip on;
        gzip_comp_level 2;
        gzip_min_length 1000;
        gzip_proxied    expired no-cache no-store private auth;
        gzip_types      text/plain text/css application/json application/x-javascript text/xml application/xml application/xml+rss text/javascript;
        gzip_disable    "MSIE [1-6]\.";

        client_max_body_size 10M;

        server {
            listen       8080;
            server_name  localhost 192.168.1.71 receiptofi.com live.receiptofi.com test.receiptofi.com;
            return  301  https://$host$request_uri;

            #charset koi8-r;

            access_log  /var/logs/nginx/access.log main;

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

        ssl_certificate      /var/certs/43c7297ae5ac1.crt;
        ssl_certificate_key  /var/certs/receiptofi.com.key;

        ssl_session_cache    shared:SSL:10m;
        ssl_session_timeout  10m;

        ssl_protocols SSLv3 TLSv1 TLSv1.1 TLSv1.2;
        ssl_ciphers HIGH:!aNULL:!MD5;
        ssl_prefer_server_ciphers on;

        # HTTPS server
        #
        server {
            listen       8443 ssl;
            server_name  localhost 192.168.1.71 receiptofi.com;

            access_log  /var/logs/nginx/access.log main;

            location / {
                root /data/www;
                index index.html;
            }
        }

        server {
            listen       8443 ssl;
            server_name  live.receiptofi.com;

            access_log  /var/logs/nginx/live.access.log main;

            location / {
                proxy_buffers 16 4k;
                proxy_buffer_size 2k;

                proxy_set_header    Host                    $http_host;
                proxy_set_header    X-Real-IP               $remote_addr;
                proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
                proxy_set_header    X-NginX-Proxy           true;

                proxy_pass http://192.168.1.75:8080;
            }
        }

        server {
            listen       8443 ssl;
            server_name  test.receiptofi.com;

            access_log  /var/logs/nginx/test.access.log main;

            location / {
                proxy_buffers 16 4k;
                proxy_buffer_size 2k;

                proxy_set_header    Host                    $http_host;
                proxy_set_header    X-Real-IP               $remote_addr;
                proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
                proxy_set_header    X-NginX-Proxy           true;

                proxy_pass http://localhost:9090;

                # Subdomain test.m.receiptofi.com would be best in its own host,
                # current architecture suggest (my opinion) to have one domain
                # and other application(s) list as /test.domain.com/receipt-mobile/
                # instead of /test.m.domain.com/receipt-mobile/
                #proxy_set_header   X-Forwarded-Host        $host;
                #proxy_set_header   X-Forwarded-Server      $host;
                #proxy_set_header   X-Forwarded-For         $proxy_add_x_forwarded_for;
                #proxy_pass http://localhost:9090/receipt-mobile/;
            }
        }
    }
