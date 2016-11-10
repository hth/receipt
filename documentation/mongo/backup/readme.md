## Mongo Backup on Mac

### Create directory

    mkdir ~/Mongo &&
    cd ~/Mongo &&
    touch com.receiptofi.mongo.backup.plist &&
    touch mongo-backup.sh &&
    chmod 755 mongo-backup.sh  

### Copy content to these files
    
com.receiptofi.mongo.backup.plist && mongo-backup.sh 



#### Command to setup
    
    rm -f ~/Library/LaunchAgents/com.receiptofi.mongo.backup.plist &&
    rm -f /var/log/mongodb/backup_error.log &&
    rm -f /var/log/mongodb/backup.log &&
    cp ~/Mongo/com.receiptofi.mongo.backup.plist ~/Library/LaunchAgents &&
    launchctl unload ~/Library/LaunchAgents/com.receiptofi.mongo.backup.plist &&
    launchctl load ~/Library/LaunchAgents/com.receiptofi.mongo.backup.plist &&
    launchctl start ~/Library/LaunchAgents/com.receiptofi.mongo.backup.plist
    