### Filebeat Install

    curl -L -O https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-5.0.0-rc1-amd64.deb
    sudo dpkg -i filebeat-5.0.0-rc1-amd64.deb
    
Filebeat runs as `root`. Could have picked `db`   
    
### Modify config 
Config created at `/etc/filebeat/filebeat.yml`
    
    
### Starting filebeat
    
    sudo /etc/init.d/filebeat start
    sudo /etc/init.d/filebeat stop
    
    