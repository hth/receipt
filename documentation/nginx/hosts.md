Notice `test.receiptofi.com` and `smoker.receiptofi.com` (`Kibana` is mapped as `smoker.receiptofi.com`) and `receiptofi.com` are on same machine. They can be on different machine in future. `Elasticsearch` mapped to sub domain `es.receiptofi.com`, is on different machine. `Kibana` ***config.js*** mapping has to be changed to

    elasticsearch: "https://es.receiptofi.com",

for `Kibana` to work from external user request. And don't forget to make `Kibana` and `Elasticsearch` password protected.

##### Hosts content from nginx    
    192.168.1.71    receiptofi.com          test.receiptofi.com     live.receiptofi.com     smoker.receiptofi.com   build.receiptofi.com    es.receiptofi.com

    192.168.1.68    build
    192.168.1.74    es
    192.168.1.71    test    smoker
    192.168.1.75    live

##### Hosts content from dev machine
    192.168.1.71    receiptofi.com  live.receiptofi.com     test.receiptofi.com     smoker.receiptofi.com   build.receiptofi.com    es.receiptofi.com

    #Mongo Servers
    192.168.1.75    m2
    192.168.1.75    ofi2
    192.168.1.75    primary
    192.168.1.75    primary.receiptofi.com
    
    192.168.1.69    m1
    192.168.1.69    ofi1
    192.168.1.69    secondary1
    192.168.1.69    secondary-1.receiptofi.com
    
    192.168.1.70    m3
    192.168.1.70    ofi3
    192.168.1.70    secondary2
    192.168.1.70    secondary2.receiptofi.com
    
    #Short hostname
    192.168.1.68    build
    192.168.1.74    es
    192.168.1.71    test    smoker
    192.168.1.75    live

