##YAML Config

- Do not use tabs in YAML files as its not supported.

  
    systemLog:
      destination: file
      path: /usr/local/var/log/mongodb/mongo.log
      logAppend: true
      timeStampFormat: iso8601-utc
    storage:
      dbPath: /data/db
      directoryPerDB: false
    net:
      #bindIp: 127.0.0.1,192.168.1.15,192.168.1.16,19.168.1.17,192.168.1.18
      port: 27017
    replication:
      oplogSizeMB: 25600
      replSetName: rs
    processManagement:
      fork: false
      