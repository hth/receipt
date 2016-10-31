    # Date: Oct 31 3:50 PM
    # https://www.digitalocean.com/community/tutorials/how-to-optimize-nginx-configuration
    # user  nobody;
    # IP Address 192.168.1.71 is related to the nginx installed ip
    worker_processes  1;
    
    error_log  /var/logs/nginx/error.log;
    #error_log  /var/logs/nginx/error.log debug;
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
    
        server_tokens   off;
    
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
    
        # Cannot mark ssl on as it will make http as not working. Only works on https. Redirect to https is better choice.
        #ssl on;    
        ssl_session_cache   shared:SSL:10m;
        ssl_session_timeout 10m;
        ssl_buffer_size     1400;
        ssl_session_tickets off;
    
        ssl_protocols TLSv1 TLSv1.1 TLSv1.2;
    
        ssl_ciphers 'ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-AES256-GCM-SHA384:DHE-RSA-AES128-GCM-SHA256:DHE-DSS-AES128-GCM-SHA256:kEDH+AESGCM:ECDHE-RSA-AES128-SHA256:ECDHE-ECDSA-AES128-SHA256:ECDHE-RSA-AES128-SHA:ECDHE-ECDSA-AES128-SHA:ECDHE-RSA-AES256-SHA384:ECDHE-ECDSA-AES256-SHA384:ECDHE-RSA-AES256-SHA:ECDHE-ECDSA-AES256-SHA:DHE-RSA-AES128-SHA256:DHE-RSA-AES128-SHA:DHE-DSS-AES128-SHA256:DHE-RSA-AES256-SHA256:DHE-DSS-AES256-SHA:DHE-RSA-AES256-SHA:AES128-GCM-SHA256:AES256-GCM-SHA384:AES128-SHA256:AES256-SHA256:AES128-SHA:AES256-SHA:AES:CAMELLIA:DES-CBC3-SHA:!aNULL:!eNULL:!EXPORT:!DES:!RC4:!MD5:!PSK:!aECDH:!EDH-DSS-DES-CBC3-SHA:!EDH-RSA-DES-CBC3-SHA:!KRB5-DES-CBC3-SHA';
    
        ssl_prefer_server_ciphers on;    
    
        ssl_dhparam          /var/certs/2016_OCT/dhparams.pem;
        ssl_certificate      /var/certs/2016_OCT/ssl-bundle.crt;
        ssl_certificate_key  /var/certs/2016_OCT/www_receiptofi_com.key;
        
        # OCSP Stapling ---
        # fetch OCSP records from URL in ssl_certificate and cache them
        ssl_stapling        on;
        ssl_stapling_verify on;
        
        ## verify chain of trust of OCSP response using Root CA and Intermediate certs
        ssl_trusted_certificate /var/certs/2016_OCT/root_CA_cert_plus_intermediates.crt;
    
        resolver            8.8.4.4 8.8.8.8 valid=300s ipv6=off; # Google DNS
        resolver_timeout    30s;
                
        # Remember this setting for 365 days
        add_header Strict-Transport-Security "max-age=31536000; includeSubdomains";
        add_header X-Frame-Options DENY;
        add_header X-Content-Type-Options nosniff;
    
        server {
            listen       8080;
            server_name  receiptofi.com live.receiptofi.com test.receiptofi.com smoker.receiptofi.com sandbox.receiptofi.com;
            return  301  https://$host$request_uri;
    
            #charset koi8-r;
    
            access_log  /var/logs/nginx/access.log main;
    
            location / {
                root   /data/www;
                index  index.html;
    
                # try_files not required here 
                # this way nginx first tries to serve the file as an .html although it doesn't have the extension
                try_files $uri.html $uri $uri/ @handler;
            }
    
            #error_page  404              /404.html;
    
            # redirect server error pages to the static page /50x.html
            #
            error_page   500 502 503 504  /50x.html;
            location = /50x.html {
                root   html;
            }
        }
    
        server {
            listen          8443 ssl;
            server_name     receiptofi.com;
    
            access_log  /var/logs/nginx/access.log main;       
    
            location / {
                expires 7d;
                root /data/www;
                index index.html;
    
                # this way nginx first tries to serve the file as an .html although it doesn't have the extension
                try_files $uri.html $uri $uri/ @handler;
            }
        }
    
        server {
            listen          8443 ssl;
            server_name     live.receiptofi.com;
    
            access_log  /var/logs/nginx/live.access.log main;
    
            location /monitoring {
                # block one workstation
                deny    192.168.1.1;
                # allow anyone in 192.168.1.0/24
                allow   192.168.1.0/24; 
                allow   63.145.59.92;
                # drop rest of the world
                deny    all;
                
                proxy_buffers 16 4k;
                proxy_buffer_size 2k;
    
                proxy_set_header    Host                    $http_host;
                proxy_set_header    X-Real-IP               $remote_addr;
                proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
                proxy_set_header    X-NginX-Proxy           true;
    
                proxy_pass http://192.168.1.75:8080;
            }
    
            location /receipt-mobile/monitoring {
                # block one workstation
                deny    192.168.1.1;
                # allow anyone in 192.168.1.0/24
                allow   192.168.1.0/24; 
                allow   63.145.59.92;
                # drop rest of the world
                deny    all;
                
                proxy_buffers 16 4k;
                proxy_buffer_size 2k;
    
                proxy_set_header    Host                    $http_host;
                proxy_set_header    X-Real-IP               $remote_addr;
                proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
                proxy_set_header    X-NginX-Proxy           true;
    
                proxy_pass http://192.168.1.75:8080;
            }
    
            location / {
                expires 7d;
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
            listen          8443 ssl;
            server_name     receiptapp.receiptofi.com;
    
            access_log  /var/logs/nginx/live.access.log main;
    
            location /monitoring {
                # block one workstation
                deny    192.168.1.1;
                # allow anyone in 192.168.1.0/24
                allow   192.168.1.0/24; 
                allow   63.145.59.92;
                # drop rest of the world
                deny    all;
                
                proxy_buffers 16 4k;
                proxy_buffer_size 2k;
    
                proxy_set_header    Host                    $http_host;
                proxy_set_header    X-Real-IP               $remote_addr;
                proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
                proxy_set_header    X-NginX-Proxy           true;
    
                proxy_pass http://192.168.1.150:80;
            }
    
            location /receipt-mobile/monitoring {
                # block one workstation
                deny    192.168.1.1;
                # allow anyone in 192.168.1.0/24
                allow   192.168.1.0/24; 
                allow   63.145.59.92;
                # drop rest of the world
                deny    all;
                
                proxy_buffers 16 4k;
                proxy_buffer_size 2k;
    
                proxy_set_header    Host                    $http_host;
                proxy_set_header    X-Real-IP               $remote_addr;
                proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
                proxy_set_header    X-NginX-Proxy           true;
    
                proxy_pass http://192.168.1.150:80;
            }
    
            location / {
                expires 7d;
                proxy_buffers 16 4k;
                proxy_buffer_size 2k;
    
                proxy_set_header    Host                    $http_host;
                proxy_set_header    X-Real-IP               $remote_addr;
                proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
                proxy_set_header    X-NginX-Proxy           true;
    
                proxy_pass http://192.168.1.150:80;
            }
        }
    
        server {
            listen          8443 ssl;
            server_name     test.receiptofi.com;
    
            access_log  /var/logs/nginx/test.access.log main;
    
            location /monitoring {
                # block one workstation
                deny    192.168.1.1;
                # allow anyone in 192.168.1.0/24
                allow   192.168.1.0/24; 
                allow   63.145.59.92;
                # drop rest of the world
                deny    all;
                
                proxy_buffers 16 4k;
                proxy_buffer_size 2k;
    
                proxy_set_header    Host                    $http_host;
                proxy_set_header    X-Real-IP               $remote_addr;
                proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
                proxy_set_header    X-NginX-Proxy           true;
    
                proxy_pass http://192.168.1.150:80;
            }
    
            location /receipt-mobile/monitoring {
                # block one workstation
                deny    192.168.1.1;
                # allow anyone in 192.168.1.0/24
                allow   192.168.1.0/24; 
                allow   63.145.59.92;
                # drop rest of the world
                deny    all;
                
                proxy_buffers 16 4k;
                proxy_buffer_size 2k;
    
                proxy_set_header    Host                    $http_host;
                proxy_set_header    X-Real-IP               $remote_addr;
                proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
                proxy_set_header    X-NginX-Proxy           true;
    
                proxy_pass http://192.168.1.150:80;
            }
    
            location / {
                proxy_buffers 16 4k;
                proxy_buffer_size 2k;
    
                proxy_set_header    Host                    $http_host;
                proxy_set_header    X-Real-IP               $remote_addr;
                proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
                proxy_set_header    X-NginX-Proxy           true;
    
                proxy_pass http://192.168.1.150:80;
    
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
    
        server {
            listen          8443 ssl;
            server_name     sandbox.receiptofi.com;
    
            access_log  /var/logs/nginx/sandbox.access.log main;
    
            location /monitoring {
                # block one workstation
                deny    192.168.1.1;
                # allow anyone in 192.168.1.0/24
                allow   192.168.1.0/24; 
                allow   63.145.59.92;
                # drop rest of the world
                deny    all;
                
                proxy_buffers 16 4k;
                proxy_buffer_size 2k;
    
                proxy_set_header    Host                    $http_host;
                proxy_set_header    X-Real-IP               $remote_addr;
                proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
                proxy_set_header    X-NginX-Proxy           true;
    
                proxy_pass http://192.168.1.150;
            }
    
            location /receipt-mobile/monitoring {
                # block one workstation
                deny    192.168.1.1;
                # allow anyone in 192.168.1.0/24
                allow   192.168.1.0/24; 
                allow   63.145.59.92;
                # drop rest of the world
                deny    all;
                
                proxy_buffers 16 4k;
                proxy_buffer_size 2k;
    
                proxy_set_header    Host                    $http_host;
                proxy_set_header    X-Real-IP               $remote_addr;
                proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
                proxy_set_header    X-NginX-Proxy           true;
    
                proxy_pass http://192.168.1.150;
            }
    
            location / {
                proxy_buffers 16 4k;
                proxy_buffer_size 2k;
    
                proxy_set_header    Host                    $http_host;
                proxy_set_header    X-Real-IP               $remote_addr;
                proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
                proxy_set_header    X-NginX-Proxy           true;
    
                proxy_pass http://192.168.1.150;
    
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
    
    
        server {
            listen          8443 ssl;
            server_name     sonar.receiptofi.com;
    
            access_log  /var/logs/nginx/sonar.access.log main;
    
            location / {
                # block one workstation
                deny    192.168.1.1;
                # allow anyone in 192.168.1.0/24
                allow   192.168.1.0/24;
                allow   63.145.59.92;
                # drop rest of the world
                deny    all;
    
                proxy_buffers 16 4k;
                proxy_buffer_size 2k;
    
                proxy_set_header    Host                    $http_host;
                proxy_set_header    X-Real-IP               $remote_addr;
                proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
                proxy_set_header    X-NginX-Proxy           true;
    
                proxy_pass http://192.168.1.68:9000;
            }
        }
    
        server {
            listen          8443 ssl;
            server_name     build.receiptofi.com;
    
            access_log  /var/logs/nginx/build.access.log main;
    
            location / {
                # block one workstation
                deny    192.168.1.1;
                # allow anyone in 192.168.1.0/24
                allow   192.168.1.0/24;
                allow   63.145.59.92;
                # drop rest of the world
                deny    all;
    
                proxy_buffers 16 4k;
                proxy_buffer_size 2k;
    
                proxy_set_header    Host                    $http_host;
                proxy_set_header    X-Real-IP               $remote_addr;
                proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
                proxy_set_header    X-NginX-Proxy           true;
    
                proxy_pass http://192.168.1.68:8080;
            }
        }
    
        server {
            listen          8443 ssl;
            server_name     es.receiptofi.com;
    
            access_log  /var/logs/nginx/es.access.log main;
    
            # auth_basic "Receiptofi ES authorized users";
            # auth_basic_user_file /usr/local/etc/nginx/kibana.smoker.htpasswd;
    
            location / {
                # block one workstation
                deny    192.168.1.1;
                # allow anyone in 192.168.1.0/24
                allow   192.168.1.0/24;
                allow   63.145.59.92;
                # drop rest of the world
                deny    all;
    
                proxy_buffers 16 4k;
                proxy_buffer_size 2k;
    
                proxy_set_header    Host                    $http_host;
                proxy_set_header    X-Real-IP               $remote_addr;
                proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
                proxy_set_header    X-NginX-Proxy           true;
    
                proxy_pass http://192.168.1.74:9200;
            }
        }
    
        server {
            listen          8443 ssl;
            server_name     smoker.receiptofi.com;
    
            access_log  /var/logs/nginx/smoker.access.log main;
    
            auth_basic "Receiptofi Smoker authorized users";
            auth_basic_user_file /usr/local/etc/nginx/kibana.smoker.htpasswd;
    
            location / {
                root  /usr/local/kibana-3.1.0;
                index  index.html  index.htm;
            }
    
            location ~ ^/_aliases$ {
                proxy_pass http://192.168.1.74:9200;
                proxy_read_timeout 90;
            }
            location ~ ^/.*/_aliases$ {
                proxy_pass http://192.168.1.74:9200;
                proxy_read_timeout 90;
            }
            location ~ ^/_nodes$ {
                proxy_pass http://192.168.1.74:9200;
                proxy_read_timeout 90;
            }
            location ~ ^/.*/_search$ {
                proxy_pass http://192.168.1.74:9200;
                proxy_read_timeout 90;
            }
            location ~ ^/.*/_mapping {
                proxy_pass http://192.168.1.74:9200;
                proxy_read_timeout 90;
            }
    
            # Password protected end points
            location ~ ^/kibana-int/dashboard/.*$ {
                proxy_pass http://192.168.1.74:9200;
                proxy_read_timeout 90;
                limit_except GET {
                    proxy_pass http://192.168.1.74:9200;
                    # auth_basic "Receiptofi authorized users";
                    # auth_basic_user_file /usr/local/etc/nginx/kibana.smoker.htpasswd;
                }
            }
            location ~ ^/kibana-int/temp.*$ {
                proxy_pass http://192.168.1.74:9200;
                proxy_read_timeout 90;
                limit_except GET {
                    proxy_pass http://192.168.1.74:9200;
                    # auth_basic "Receiptofi authorized users";
                    # auth_basic_user_file /usr/local/etc/nginx/kibana.smoker.htpasswd;
                }
            }
        }
    
        server {
            listen          8443 ssl;
            server_name     focker.receiptofi.com;
    
            access_log  /var/logs/nginx/focker.access.log main;
    
            auth_basic "Receiptofi Focker authorized users";
            auth_basic_user_file /usr/local/etc/nginx/kibana.smoker.htpasswd;
    
            location / {
                proxy_pass http://192.168.1.13:5601;
                proxy_http_version 1.1;
                proxy_set_header Upgrade $http_upgrade;
                proxy_set_header Connection 'upgrade';
                proxy_set_header Host $host;
                proxy_cache_bypass $http_upgrade;        
            }
        }
    
    }