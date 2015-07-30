## Server Set Up 

- Update Mac
- Change to UTC/GMT time 

         sudo ln -sf /usr/share/zoneinfo/UTC /etc/localtime
         
- Install XCode
  - Agree to XCode agreement
- Install HomeBrew
- Install Mongo
  - Update Mongo config
  
        	systemLog:
        	  destination: file
        	  path: /usr/local/var/log/mongodb/mongo.log
        	  logAppend: true
        	storage:
        	  dbPath: /usr/local/var/mongodb
        	net:
        	  bindIp: 127.0.0.1

  - Start Mongo, Check for logs 
- Install Java manual
- Install ActiveMQ from brew
- Install Tomcat manual
  - Add setenv script and launchd
- Install Logstash from brew


