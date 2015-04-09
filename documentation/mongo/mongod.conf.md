    #Sept 21 3:00 PM (using homebrew)
    # Store data in /usr/local/var/mongodb instead of the default /data/db
    dbpath = /usr/local/var/mongodb
    
    # Append logs to /usr/local/var/log/mongodb/mongo.log
    logpath = /var/logs/mongo/mongo.log
    logappend = true
    
    # Only accept local connections
    #bind_ip = 127.0.0.1,192.168.1.71
    
    # Profiles if query is slower than 10ms; default is 100ms
    slowms = 10


Mongo Backup

    mongodump --host 192.168.1.71 --port 27017 --db rm-test --out ~/Downloads/data/backup
    
Mongo Restore
    
    mongorestore --dir ~/Downloads/data/backup
    mongorestore --host 192.168.1.71 --port 27017 --dir ~/Downloads/data/backup
    
Mongo Restore single collection
    
    mongorestore --host 192.168.1.71 --port 27017 --db rm-test --collection fs.chunks /Users/hitender/Downloads/data/backup/rm-test/fs.chunks.bson