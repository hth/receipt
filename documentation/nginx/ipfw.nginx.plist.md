    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE plist PUBLIC "-//Apple Computer//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
    <plist version="1.0">
        <dict>
            <key>Label</key>
            <string>com.receiptofi.ipfw.nginx</string>
            <key>OnDemand</key>
            <false/>
            <key>ProgramArguments</key>
            <array>
                <string>/usr/local/startup/ipfw.ngnix.sh</string>
            </array>
            <key>RunAtLoad</key>
            <true/>
            <key>LaunchOnlyOnce</key>
            <true/>
            <key>ServiceDescription</key>
            <string>Receiptofi IPFW Nginx Filter Rules</string>
            <key>StandardErrorPath</key>
            <string>/var/logs/firewall/ipfw.stderr.log</string>
            <key>StandardOutPath</key>
            <string>/var/logs/firewall/ipfw.stdout.log</string>
            <key>UserName</key>
            <string>root</string>
        </dict>
    </plist>
