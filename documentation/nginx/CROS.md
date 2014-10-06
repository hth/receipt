##### Example for CROS on nginx 

***Note***: As of now CROS has not be used or required


    server {
        listen          .........;
        server_name     .........;

        location / {
            ........
            ....
            ..
            
            # https://github.com/johnhamelink/ansible-kibana/blob/master/templates/nginx.conf.j2
            # For CORS Ajax
            proxy_pass_header Access-Control-Allow-Origin;
            proxy_pass_header Access-Control-Allow-Methods;
            proxy_hide_header Access-Control-Allow-Headers;
            add_header Access-Control-Allow-Headers 'X-Requested-With, Content-Type';
            add_header Access-Control-Allow-Credentials true;
        }
    }
