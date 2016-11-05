    ---
    filebeat.prospectors:
    - input_type: log
      paths:
        - /var/log/mongodb/mongo.log
        - /var/log/activemq/activemq.log
        - /var/log/activemq/audit.log
        - /var/log/tomcat/receiptofi.log
        - /var/log/tomcat/receiptofi-mobile.log
      multiline.pattern: ^\[
      multiline.negate: false
      multiline.match: after
    name: s2
    output.logstash:
      hosts: ["192.168.1.13:5044"]
    logging:
      level: info
        to_files: true
        to_syslog: false
        files:
          path: /var/log/filebeat
          name: filebeat.log
          keepfiles: 7
          rotateeverybytes: 10485760
    
    ---
    filebeat.prospectors:
    - input_type: log
      paths:
        - /var/log/tomcat/receiptofi.log
        - /var/log/tomcat/receiptofi-mobile.log
      exclude_files: [".lck"]
      fields:
        tags: ['json']
      document_type: tomcatlog
      multiline.pattern: '^\['
      multiline.negate: true
      multiline.match: after
    - input_type: log
      paths:
        - /var/log/activemq/activemq.log
        - /var/log/activemq/audit.log
      fields:
        apache: true 
      document_type: activemqlog
      multiline.pattern: '^\['
      multiline.negate: true
      multiline.match: after
    - input_type: log
      paths:
        - /var/log/mongodb/mongo.log
      fields:
        apache: true 
      document_type: mongolog
      multiline.pattern: '^\['
      multiline.negate: true
      multiline.match: after
    name: r4
    output.logstash:
      hosts: ["192.168.1.45:5044"]
    logging:
      level: info
      to_files: true
      to_syslog: false
      files:
        path: /var/log/filebeat
        name: filebeat.log
        keepfiles: 7
        rotateeverybytes: 10485760