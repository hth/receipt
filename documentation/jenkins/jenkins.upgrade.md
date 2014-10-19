Upgrade Jenkins
===============

Download latest `jenkins.war` file to download folder
Stop Jenkins

    sudo launchctl unload /Library/LaunchDaemons/org.jenkins-ci.plist

Copy `jenkins.war` file to directory

    /Application/Jenkins

Start Jenkins

    sudo launchctl load -w /Library/LaunchDaemons/org.jenkins-ci.plist




