## Backing up database

### Change mode of the script to execute

    chmod +x mongo-backup.sh
    
### Schedule a Cronjob by opening crontab:

    env EDITOR=nano crontab -e

### Enter this in a new line:

    # Every 20 minutes
    */20 * * * * /Users/db/Mongo/mongo_backup.sh  >> /Users/db/Mongo/backup-cron.txt   

### File - mongo-backup.sh

    #!/bin/bash
     
    MONGO_DATABASE="rm-test"
    APP_NAME="rm-test"
    TIMESTAMP=`date +%F-%H%M`
    BACKUP_NAME="$APP_NAME-$TIMESTAMP"
    
    USERNAME="$USER"
    BACKUPS_DIR="/Users/$USERNAME/Mongo/backups/$APP_NAME"
    echo "##############"
    echo "$BACKUPS_DIR" $BACKUP_NAME
    echo "##############"
    
    MONGO_HOST="127.0.0.1"
    MONGO_PORT="27017"
    MONGODUMP_PATH="mongodump"
     
    # mongo admin --eval "printjson(db.fsyncLock())"
    # echo "#############################################"
    # echo "LOCKED DB. Should not lock or unlock on prod."
    # echo "#############################################"
    
    $MONGODUMP_PATH -h $MONGO_HOST:$MONGO_PORT -d $MONGO_DATABASE
    
    # echo "#################################################"
    # echo "UN-LOCKING DB. Should not lock or unlock on prod."
    # echo "#################################################"
    # mongo admin --eval "printjson(db.fsyncUnlock())"
    
    #### OR For local connection ####
    # $MONGODUMP_PATH -d $MONGO_DATABASE
    
    echo "###############################"
    echo "CREATING DIRECTORY AND TAR FILE"
    echo "###############################"
    
    mkdir -p $BACKUPS_DIR
    mv dump $BACKUP_NAME
    tar -zcvf $BACKUPS_DIR/$BACKUP_NAME.tgz $BACKUP_NAME
    rm -rf $BACKUP_NAME
    
    echo "################" 
    echo "COMPLETED BACKUP" $BACKUP_NAME
    echo "################"