### Start and Kill MMS mongo service

    nohup ./mongodb-mms-automation-agent --config=local.config >> /var/log/mongodb-mms-automation/automation-agent.log 2>&1 &
    ps aux | grep agent
    
    /var/lib/mongodb-mms-automation/mongodb-mms-backup-agent-3.3.0.261-1.osx_x86_64/mongodb-mms-backup-agent
    /var/lib/mongodb-mms-automation/mongodb-mms-monitoring-agent-3.2.0.177-1.osx_x86_64/mongodb-mms-monitoring-agent
    ./mongodb-mms-automation-agent --config=local.config

    pkill -f mongodb-mms-backup-agent
    pkill -f mongodb-mms-monitoring-agent
    pkill -f mongodb-mms-automation-agent
    
    Else kill process through activity (forceful kill)
    
### Prefer to have mongodump for small db. MMS should be helpful for knowing page fault.    