##### Point the elasticsearch from localhost to where it is installed. 

<code>Kibana</code> is installed on <code>nginx</code>. <code>Kibana</code> is just collection of static files. **config.js** under this directory is changed from localhost to proper FQDN. In the example below, <code>elasticsearch</code> is installed on 192.168.1.74
  
    elasticsearch: "http://192.168.1.74:9200",
    
Installed <code>Logstash</code> on central has inbuilt vendor directory containing <code>Kibana</code>. Configuring <code>nginx</code> to allow access to <code>Kibana</code> from outside world is preferred over inbuilt which has resricted access as <code>nginx</code> has no control. Secured access is more preferred as not everyone should be allowed to access logs. Hence, secure <code>nginx</code> with passwords (<code>.htaccess</code>) to give access to <code>Kibana</code> site. Example below shows location of inbuild <code>Kibana</code> in <code>Logstash</code>. 

    /usr/local/logstash-1.4.1/vendor/kibana
    
Note: Config under vendor currently is configured with FQDN but <code>localhost/default</code> setting should work as <code>elasticsearch</code> in on same machine as **central** <code>logstash</code>.    
