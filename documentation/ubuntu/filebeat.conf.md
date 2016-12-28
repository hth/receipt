## Live    
    
    ---
    filebeat.prospectors:
    - input_type: log
      paths:
        - /var/log/tomcat/receiptofi.log
      exclude_files: [".lck"]
      fields:
        tags: ['json']
      scan_frequency: 1s
      close_inactive: 5m        
      document_type: receiptapp
    - input_type: log
      paths:
        - /var/log/tomcat/receiptofi-mobile.log
      exclude_files: [".lck"]
      fields:
        tags: ['json']
      scan_frequency: 1s
      close_inactive: 5m        
      document_type: receiptapp_mobile_app  
    - input_type: log
      paths:
        - /var/log/activemq/activemq.log
        - /var/log/activemq/audit.log
      fields:
        apache: true 
      scan_frequency: 1s
      close_inactive: 5m  
      document_type: receiptapp_activemq
    - input_type: log
      paths:
        - /var/log/mongodb/mongo.log
      fields:
        apache: true 
      scan_frequency: 1s
      close_inactive: 5m  
      document_type: receiptapp_mongo
    output.logstash:
      hosts: ["192.168.1.123:5044"]
    logging:
      level: info
      to_files: true
      to_syslog: false
      files:
        path: /var/log/filebeat
        name: filebeat.log
        keepfiles: 7
        rotateeverybytes: 10485760
    name: r1        

## Sandbox        

    ---
    filebeat.prospectors:
    - input_type: log
      paths:
        - /var/log/tomcat/receiptofi.log
      exclude_files: [".lck"]
      fields:
        tags: ['json']
      scan_frequency: 1s
      close_inactive: 5m        
      document_type: sandbox
    - input_type: log
      paths:
        - /var/log/tomcat/receiptofi-mobile.log
      exclude_files: [".lck"]
      fields:
        tags: ['json']
      scan_frequency: 1s
      close_inactive: 5m        
      document_type: sandbox_mobile_app  
    - input_type: log
      paths:
        - /var/log/activemq/activemq.log
        - /var/log/activemq/audit.log
      fields:
        apache: true 
      scan_frequency: 1s
      close_inactive: 5m  
      document_type: sandbox_activemqlog
    - input_type: log
      paths:
        - /var/log/mongodb/mongo.log
      fields:
        apache: true 
      scan_frequency: 1s
      close_inactive: 5m  
      document_type: sandbox_mongolog
    output.logstash:
      hosts: ["192.168.1.123:5044"]
    logging:
      level: info
      to_files: true
      to_syslog: false
      files:
        path: /var/log/filebeat
        name: filebeat.log
        keepfiles: 7
        rotateeverybytes: 10485760
    name: s1
            
## Test    
    
    ---
    filebeat.prospectors:
    - input_type: log
      paths:
        - /var/log/tomcat/token.log
      exclude_files: [".lck"]
      fields:
        tags: ['json']
      scan_frequency: 1s
      close_inactive: 5m        
      document_type: tokenapp
    - input_type: log
      paths:
        - /var/log/tomcat/token-mobile.log
      exclude_files: [".lck"]
      fields:
        tags: ['json']
      scan_frequency: 1s
      close_inactive: 5m        
      document_type: tokenapp_mobile_app  
    - input_type: log
      paths:
        - /var/log/mongodb/mongo.log
      fields:
        apache: true 
      scan_frequency: 1s
      close_inactive: 5m  
      document_type: tokenapp_mongo
    output.logstash:
      hosts: ["192.168.1.123:5044"]
    logging:
      level: info
      to_files: true
      to_syslog: false
      files:
        path: /var/log/filebeat
        name: filebeat.log
        keepfiles: 7
        rotateeverybytes: 10485760
    name: test