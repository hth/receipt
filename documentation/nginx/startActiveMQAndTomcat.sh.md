echo "sudo launchctl unload -w /Library/LaunchDaemons/receiptofi.plist"
sudo launchctl unload -w /Library/LaunchDaemons/receiptofi.plist
echo "waiting 10 seconds"
sleep 10
echo "sudo launchctl unload -w /Library/LaunchDaemons/activemq.plist"
sudo launchctl unload -w /Library/LaunchDaemons/activemq.plist
echo "waiting 5 seconds"
sleep 5
echo "sudo launchctl load -w /Library/LaunchDaemons/activemq.plist"
sudo launchctl load -w /Library/LaunchDaemons/activemq.plist
echo "waiting 5 seconds"
sleep 5
echo "sudo launchctl load -w /Library/LaunchDaemons/receiptofi.plist"
sudo launchctl load -w /Library/LaunchDaemons/receiptofi.plist
echo "script executed successfully"