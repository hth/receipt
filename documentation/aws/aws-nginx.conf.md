### AWS Nginx config    

    # For more information on configuration, see:
    #   * Official English Documentation: http://nginx.org/en/docs/
    #   * Official Russian Documentation: http://nginx.org/ru/docs/
    
    user  nginx;
    worker_processes  1;
    
    error_log  /var/log/nginx/error.log;
    #error_log  /var/log/nginx/error.log  notice;
    #error_log  /var/log/nginx/error.log  info;
    
    pid        /var/run/nginx.pid;
    
    
    events {
        worker_connections  1024;
    }
    
    
    http {
        include	  /etc/nginx/mime.types;
        default_type  application/octet-stream;
    
        log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                          '$status $body_bytes_sent "$http_referer" '
                          '"$http_user_agent" "$http_x_forwarded_for"';
    
        access_log  /var/log/nginx/access.log  main;
    
        sendfile        on;
        #tcp_nopush     on;
    
        #keepalive_timeout  0;
        keepalive_timeout  65;
    
        #gzip  on;
    
        # Load modular configuration files from the /etc/nginx/conf.d directory.
        # See http://nginx.org/en/docs/ngx_core_module.html#include
        # for more information.
        include /etc/nginx/conf.d/*.conf;
    
        index   index.html index.htm;
    
        server {
    	    listen       80;
            server_name  localhost ec2-54-203-104-138.us-west-2.compute.amazonaws.com;
            root         /usr/share/nginx/html;
    
            #charset koi8-r;
    
            #access_log  /var/log/nginx/host.access.log  main;
    
            #S3 proxy config
            ######################################
            location /chk.local/ {
                try_files $uri @local;
            }
            
            location @local{
                proxy_pass http://chk.local.s3.amazonaws.com;
            }
            
            #######################################
            location /chk.live/ {
                try_files $uri @local;
            }
            
            location @live{
                proxy_pass http://chk.live.s3.amazonaws.com;
            }
            
            ##########################################
            location /chk.test/ {
                try_files $uri @local;
            }
    
            location @live{
                proxy_pass http://chk.live.s3.amazonaws.com;
            }
            
            ##########################################
            location /chk.test/ {
                try_files $uri @local;
            }
            
            location @local{
                proxy_pass http://chk.test.s3.amazonaws.com;
            }
            ########################################
            location / {
            }
    
    	    # redirect server error pages to the static page /40x.html
            #
    	    error_page  404              /404.html;
            location = /40x.html {
            }
    
    	    # redirect server error pages to the static page /50x.html
            #
    	    error_page   500 502 503 504  /50x.html;
            location = /50x.html {
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
        #    root         html;
    
        #    location / {
        #    }
        #}
        
        # HTTPS server
        #
        #server {
        #    listen       443;
        #    server_name  localhost;
        #    root         html;
    
        #    ssl                  on;
        #    ssl_certificate      cert.pem;
        #    ssl_certificate_key  cert.key;
    
        #    ssl_session_timeout  5m;
    
        #    ssl_protocols  SSLv2 SSLv3 TLSv1;
        #    ssl_ciphers  HIGH:!aNULL:!MD5;
        #    ssl_prefer_server_ciphers   on;
    
        #    location / {
        #    }
        #}
        
    }

