Example for CROS on nginx


    server {
        listen          8443 ssl;
        server_name     smoker.receiptofi.com;

        access_log  /var/logs/nginx/smoker.access.log main;

        location / {
            proxy_buffers 16 4k;
            proxy_buffer_size 2k;

            # auth_basic "Restricted";
            # auth_basic_user_file /var/www/htpasswd;

            proxy_redirect off;

            proxy_set_header    Host                    $http_host;
            proxy_set_header    X-Real-IP               $remote_addr;
            proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
            proxy_set_header    X-NginX-Proxy           true;

            proxy_pass http://192.168.1.74:9200;

            # https://github.com/johnhamelink/ansible-kibana/blob/master/templates/nginx.conf.j2
            # For CORS Ajax
            proxy_pass_header Access-Control-Allow-Origin;
            proxy_pass_header Access-Control-Allow-Methods;
            proxy_hide_header Access-Control-Allow-Headers;
            add_header Access-Control-Allow-Headers 'X-Requested-With, Content-Type';
            add_header Access-Control-Allow-Credentials true;
        }
    }
