##### Change elasticsearch from localhost to where it is installed. 

`Kibana` is installed on `nginx`. `Kibana` is just collection of static files. **config.js** under `Kibana` home directory should be changed from localhost to proper FQDN. In the example below, `elasticsearch` is installed on 192.168.1.74, but changing to local ip would work within local network, and will not work externally. This is because `config.js` is called by `Kibana`. `Kibana` tries the `Ajax` call using `FQDN` defined in `config.js` (here it is IP) to load data from `Elasticsearch`. Instead, of mapping to some local `IP`, it is better to change to subdomain like `es.receiptofi.com` for global calls to work. 

But thats not enough. We do not want to expose internal port externally. Plus firewall allows only `80` and `443` ports. So we have to change the port to something similar to `Kibana` hosts port which is `8443`. Since `Kibana` is mapped on `SSL`. But `8443` port will not work for `elasticsearch`, as its running on `9200`. To make this work, we add mapping in `nginx` config that listen on port `8443` (firewall configured to changed 443 request to 8443), which in turns forward request to correct local ip and correct port `proxy_pass http://192.168.1.74:9200;`
  
    /** Do this when access is for local network */
    elasticsearch: "http://192.168.1.74:9200",
    
    /** Do this when access is global */
    elasticsearch: "https://es.receiptofi.com",
    
nginx.conf

    server {
        listen          8443 ssl;
        server_name     es.receiptofi.com;

        access_log  /var/logs/nginx/es.access.log main;

        auth_basic "Receiptofi ES authorized users";
        auth_basic_user_file /usr/local/etc/nginx/kibana.smoker.htpasswd;

        location / {
            proxy_buffers 16 4k;
            proxy_buffer_size 2k;

            proxy_set_header    Host                    $http_host;
            proxy_set_header    X-Real-IP               $remote_addr;
            proxy_set_header    X-Forwarded-For         $proxy_add_x_forwarded_for;
            proxy_set_header    X-NginX-Proxy           true;

            proxy_pass http://192.168.1.74:9200;
        }
    }
    
Installed `Logstash` on `Central` has inbuilt vendor directory containing `Kibana`. Configuring `nginx` to allow access to `Kibana` from outside world is preferred over inbuilt which has resricted use as `nginx` has no control. Secured access is more preferred as not everyone should be allowed to access logs. Hence, secure `nginx` with passwords (`.htaccess`) to give access to `Kibana` and `Elasticsearch` site. Example below shows location of inbuild `Kibana` in `Logstash`. 

    /usr/local/logstash-1.4.1/vendor/kibana
    
Note: Config under vendor currently is configured with FQDN but <code>localhost/default</code> setting should work as <code>elasticsearch</code> in on same machine as **Central** <code>logstash</code>.    
