    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
    <plist version="1.0">
    <dict>
        <key>Label</key>
        <string>com.receiptofi.mongo.backup</string>
        <key>LowPriorityIO</key>
        <true/>
        <key>ProgramArguments</key>
        <array>
            <string>/bin/sh</string>
            <string>/Users/db/Mongo/mongo-backup.sh</string>
        </array>
        <!-- Run every four hour -->
        <key>StartInterval</key>
        <integer>14400</integer><!-- seconds -->
        <key>StandardOutPath</key>
        <string>/var/log/mongodb/backup.log</string>
        <key>StandardErrorPath</key>
        <string>/var/log/mongodb/backup_error.log</string>
    </dict>
    </plist>
