echo "sudo launchctl unload -w /Library/LaunchDaemons/receiptofi.plist"
sudo launchctl unload -w /Library/LaunchDaemons/receiptofi.plist
sleep 20
echo "sudo launchctl unload -w /Library/LaunchDaemons/activemq.plist"
sudo launchctl unload -w /Library/LaunchDaemons/activemq.plist
sleep 5
echo "sudo launchctl load -w /Library/LaunchDaemons/activemq.plist"
sudo launchctl load -w /Library/LaunchDaemons/activemq.plist
sleep 5
echo "sudo launchctl load -w /Library/LaunchDaemons/receiptofi.plist"
sudo launchctl load -w /Library/LaunchDaemons/receiptofi.plist
echo "script executed successfully"