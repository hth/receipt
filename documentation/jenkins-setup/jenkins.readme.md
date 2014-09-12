Jenkins Plist

    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
    <plist version="1.0">
    <dict>
        <key>StandardOutPath</key>
        <string>/var/log/jenkins/jenkins.log</string>
        <key>StandardErrorPath</key>
        <string>/var/log/jenkins/jenkins.log</string>
            <key>EnvironmentVariables</key>
            <dict>
                    <key>JENKINS_HOME</key>
                    <string>/Users/Shared/Jenkins/Home</string>
            </dict>
            <key>GroupName</key>
            <string>daemon</string>
            <key>KeepAlive</key>
            <true/>
            <key>Label</key>
            <string>org.jenkins-ci</string>
            <key>ProgramArguments</key>
            <array>
                    <string>/bin/bash</string>
                    <string>/Library/Application Support/Jenkins/jenkins-runner.sh</string>
            </array>
            <key>RunAtLoad</key>
            <true/>
            <key>UserName</key>
            <string>jenkins</string>
            <key>SessionCreate</key>
            <true />
    </dict>
    </plist>

————————————————

War file location — /Applications/Jenkins
-rw-r--r--   1 root  wheel  67098064 Feb 14 15:47 jenkins.war

no @ sign; remove that next time

Gradle setup for Jenkins - Use GradleWrapper and set just the 'Task'

Jenkins System Info
localhost:8080/systemInfo
———————

History 

    maudes-Mac-mini:Users maude$ history
        1  java -version
        2  echo $PATH
        3  echo $JAVA_HOME
        4  cd ..
        5  ls -al
        6  sudo passwd jenkins
        7  sudo ln -s /Users/Shared/Jenkins/ /Users/jenkins
        8  sudo chown jenkins:jenkins /Users/jenkins
        9  cd /Users/jenkins/
       10  cd Downloads/
       11  cd Downloads/
       12  su jenkins
       13  exit
       14  cd /Users/jenkins/Downloads/
       15  exit
       16  su jenkins
       17  history
       18  sudo visudo
       19  ls -al
       20  cd /usr/local/
       21  ls -al
       22  cd /usr/local/
       23  ls -al
       24  exit
       25  history

———————————


Jenkins setup on OXS
http://flow.apphance.com/introduction/hello-continuous-integration/osx-server/continuous-integration-server


Apphance Flow should work with any Continuous Integration system. Our CI server of choice is Jenkins and this part of documentation describes how to set it up. But the steps below should be pretty similar when installing other CI software.

You can follow the step by step tutorial below for Jenkins for other CI software you should test it your self.
Contents
1 Installing Jenkins
2 Setting user jenkins password
3 Setting jenkins auto login.
4 Setting up jenkins user as administrator
5 Home dir symlink creation
6 Setting up node environment variables
7 Setting up PATH variable for Jenkins
8 Next steps

Installing Jenkins

In this document we describe the simplest possible installation on one machine. For more complex setups (i.e. using jenkins slaves) please see the official documentation at
 https://wiki.jenkins-ci.org/display/JENKINS/Thanks+for+using+OSX+Installer for OSX.

OSX gatekeeper

Jenkins can be easily installed using native installer . Before you begin installation you have to disable OS X Gatekeeper feature. Otherwise you will see following error:


To disable Gatekeeper you should:
Open system preferences, open “Security & Privacy” section. Unlock changes by clicking lock in left corner and select “Anywhere” in “Allow applications downloaded from:” section:


In order to be able to run jenkins, java 7 must be installed. 

Java 7 can be installed using Oracle's installation instructions.
Note! Jenkins startup script (nor jenkins installer) will not warn you that java is not installed it just silently won’t start if you forgot this step!

After disabling sandboxing and java installation you can install jenkins from pkg.


Setting user jenkins password

Jenkins installer adds jenkins user, but you have to manually set the password. You can do this with following command:

sudo passwd jenkins

Setting jenkins auto login.

To be able to run test on simulator you have to setup auto login as jenkins user. Open “Users & Groups” section in OS X settings and select “Login Option” The tricky part is that jenkins user is not labeled:



Setting up jenkins user as administrator

Boxen software (which we are using to install additional software) requires sudo permissions to install. You have to open “Users & Groups” preferences and check “Allow user to administer this computer” checkbox:



Home dir symlink creation

Jenkins creates its user dir at /Users/Shared/Jenkins. Some command line tools assumes that home dir is at /Users/jenkins. You have to make symlink:

sudo ln -s /Users/Shared/Jenkins /Users/jenkins
sudo chown jenkins:jenkins /Users/jenkins


Setting up node environment variables

The unlockKeychain task requires OS_KEYCHAIN_LOCATION and OSX_KEYCHAIN_PASSWORDS variables. If you use Login Keychain (default) then keychain password is the same as Jenkins user password. Both variables should be set in Jenkins's configuration (Jenkins -> Manage Jeknkins -> Configure System in single master environment or <node name>/Configure in master/slave environments) section. 
Similarly if you will configure mail server on your Jenkins you need to setup MAIL_SERVER and MAIL_PORT environment variables.



Setting up PATH variable for Jenkins

In order to be able to use command line tools installed with homebrew/boxen you have to edit jenkins startupscript:
vim /Library/Application\ Support/Jenkins/jenkins-runner.sh
and add source /opt/boxen/env.sh atter #/bin/bash  then restart jenkins:

sudo launchctl stop org.jenkins-ci
sudo launchctl start org.jenkins-ci

———————————————————————


Jenkins Plugins
http://flow.apphance.com/introduction/hello-continuous-integration/linux-server/jenkins-plugins

After finishing basic setup you should install required Jenkins plugins. Some of the plugins depend on what version control you are using, how you connect jenkins to your network and what kind of reports you want to run. 

Installing plugins on Jenkins is described here.

The bare minimum are:

Jenkins GIT plugin
Jenkins Mercurial plugin
Jenkins Release Plugin
Jenkins Gradle plugin
Static Analysis Utilities
xUnit plugin

There are also other useful plugins that help in making Jenkins' output more useful and help to graph some additional data and manage your Jenkins installation:

Timestamper
Shelve Project Plugin
Audit Trail
Hudson Locks and Latches plugin
Jenkins build timeout plugin
Jenkins disk-usage plugin
GitHub plugin
Green Balls

These are just examples of plugins that we found most useful. There are virtually hundreds plugins available in Jenkins' plugin repository.

——
back up post — Have not used it
How to set up Jenkins CI on a Mac
http://goodliffe.blogspot.com/2011/09/how-to-set-up-jenkins-ci-on-mac.html

In this post I will describe how to get a running Jenkins server set up on your Mac. Like most free software ("free" as in price and "free" as in freedom), Jenkins is very capable, very functional, and mostly documented. But it didn't quite work out of the box.

As with many such projects, you get far more than you pay for. But you can end up spending more than you expect.

There aren't enough step-by-step how-to guides. And there aren't many documents that help you out when things go wrong. There is a great community behind Jenkins, though, which does help. And plenty of people moaning and blogging. Now I'm adding to that noise.

It took me a few days to get the setup working properly. Hopefully this story-cum-howto will save you some of that effort.

The Prologue

All good developers know that a continuous integration (CI) server is a linchpin of the development effort. I joined a large software project without one, and made loud noises that we needed one. And so, it naturally fell to me to set up.

It's been some time since I set up a CI server. Previously, I've used ViewTier's Parabuild. It was more than adequate. But times have moved on. Although I still have a licence for it, the cools kids are hanging around at other parties these days.

Jenkins (the recent fork of Husdon) seems well-regarded, popular, and to have a good development and support community. It's also open source, so seemed the right way to go. Plenty of people have sung it's praises to me in the past, and that kind of thing counts for a lost.

Our requirements for the builds were:
to build two products from the same codebase on Mac OSX
to build two products from the same codebase on Windows

Both of these need 32-bit and 64-bit versions.

That's already a reasonable configuration matrix, and highlighted why we needed CI in place. A developer would check in a tweak that they'd built on one configuration. All the other config could easily get broken without anyone noticing for a while.

So: Jenkins to the rescue.

Almost.

I purchased a small Mac Mini to use as a build server. I downloaded a copy of Jenkins (free, whoop!), installed the Mac dev tools (free, whoop!) bought (and installed) Parallels, Windows 7, and the Visual Studio toolchain (not quite as free), and sat down for a small configuration session.


Getting your project ready for a CI build

Before setting up your build on an CI server, you should first create a simple script that builds everything from a clean checkout. Then check that script into the repository, to be versioned alongside the software itself.

For our project, I already had that in place. The script cleaned, built, versioned and packaged the software in one step.

Such scripts are clearly useful for deployment on a CI server, and also for making official software releases by hand, whether or not you release from the CI server builds. It's a record of the recipe needed to build a release.

With a fixed recipe like this in place, every software release can be guaranteed to be good and reproducible.

That's development 101.




Installing Jenkins on the Mac

The Jenkins website has a handy Mac installer that you can download. (In retrospect, I'm not sure if this was more hassle than it was worth, but this is the route I obviously sought to go down.)

STEP 1: Install Jenkins

Download the Mac installer and run it.

This installer creates a system launch daemon that fires up Jenkins when your machine boots. This runs in the background even if you haven't logged in, making a true stand-alone build server installation.

However, if you have a fresh Lion install you don't yet have Java.

STEP 2: Install Java

Try to run a Java app. Any app. The OS may fumble around for a while looking for Java. If you're lucky, it'll download and install it automatically. Otherwise, install it by hand.

And of course, now, Jenkins "just works".

Not.


Configuring Jenkins on the Mac

The Jenkins war (web application archive) is unpacked into /Users/shared/Jenkins. The application runs from there. All configuration is stored there. The source code checkouts and builds go in there. It's the center of your Jenkins universe.

A launch daemon plist is installed in /Library/LaunchDaemons. It runs a script /Library/Application Support/jenkins-runner.sh as the user "daemon" (a system specific user that runs background processes - it is not shown on the login screen, nor does it have a home directory).

This installation has all the hallmarks of a runnable system. You can now point your browser to http://localhost:8080 and start configuring the Jenkins server. The lights are most definitely on. But no one's home yet. As we're about to see...

STEP 3: Set up Jenkins to build a Mac project

I was a good soldier with a simple shell script that built and packaged my application. If you don't have this, write one now. To build Mac projects, you'll need some cunning invocation of xcodebuild and probably packagemaker.

This single script is a critical step in configuring your build job. Whilst it is possible to place multiple build commands into the Jenkins task itself, it's far better to keep them under source control in a script checked in to your codebase (if you need to ask why then you probably need to go to a more basic tutorial!).

Configure Jenkins to check out your repository, to react to appropriate build triggers (e.g. manual build requests through the UI, automatic detection of the repository changing, or other triggers) and to run the appropriate scripts to kick off the build.

Then press "Build Now" to start your first build.

In all probability Jenkins will crash and burn. But don't tear your hair out just yet. You'll be needing it for later on.


Fix Jenkins so it works

Welcome to the nether-world of almost working builds.

STEP 4: Configure the Java heap size

My project is large. It includes lots of third party libraries, including the vastness that is Boost and many other comparable-size libraries. There are also several SDKs that are shipped as large binaries (with versions for each platform and 32/64 bit). That's a lot of data to shovel around.

Jenkins choked trying to check out this monster. It would collapse with Java heap exhaustion errors before it even got to triggering a build. Goodness only knows why a large heap is required to check files out of a subversion repository, but the solution can only be to increase the heap size to remove the bottleneck.

By default, Java allocates a very conservative default heap size to running applications; I believe its 256M or  so on 32-bit Mac OS.

On the Mac, this can be changed using the "Java Preferences" application (found in the /Applications/Utilities folder). The trick is to adjust the Java launch command line (hit Options...) to include the comand-line sneeze: "-Xmx1024M" (or whatever heap size you want). However, this didn't seem to affect the Jenkins Java process launched through launchd.

To set the heap size in that context, you have to adjust the launch script itself. You can place the command line switch into the jenkins-runner.sh file directly. However, the file does have provision to load the heap size parameter from a configuration plist. This plist does not exist by default, but you can create/edit it with the following incantation:

sudo defaults write /Library/Preferences/org.jenkins-ci heapSize 1024M

This will write a file /Library/Preferences/org.jenkins-ci.plist (note that you must not specify the plist file extension to the defaults command).

To make the system use this new heap size, you can't just restart Jenkins (either gracefully within the web interface, or by "kill -9"-ing the process. You can't even use "sudo launchctl {stop,start} org.jenkins-ci".

You could reboot. Or, more cleanly, you have to force launchd to reload of the configuration for the launch daemon using launchctl by unloading, and then reloading the daemon. It's the reloading that'll force the new configuration to take hold. (It took me a while to figure that one out!)

With an increased heap, Jenkins will fall over less. In my case, I got through a whole checkout.

But once Jenkins manages to check out the project and run the build script, you're still not quite done...

My script called xcodebuild to invoke Xcode from the command line to build the various configurations of the project. This script worked fine when run directly from the command line. However, when running within Jenkins it would bomb out with quite unfathomable errors - e.g. NSAssertions triggered from deep within the Xcode IDE codebase. Or it would just enter a hibernation state; lock-up completely, performing no work, but generating no error.

The reason for the strangeness is that xcodebuild doesn't work when run as a user that has no home directory, like daemon. It throws its toys out of the pram in as baroque a manner as it can muster.

STEP 5: Create a "jenkins" user

So, to solve this we can either have Jenkins run as one of the existing users, or - more cleanly - create a new user specifically for jenkins.

To do this:

Create a user called "jenkins" from Control Panel. If you care particularly, you might want to create a "hidden" user; follow the instructions here: http://hints.macworld.com/article.php?story=20080127172157404
Stop jenkins again: sudo launchctl unload -w /Library/LaunchAgents/org.jenkins-ci.plist
Edit  /Library/LaunchAgents/org.jenkins-ci.plist, change the username entry from daemon to jenkins
Change all permissions on the existing jenkins files: "sudo chown -R jenkins /User/Shared/jenkins" "sudo chgrp -R staff /User/Shared/Jenkins"
Restart Jenkins: sudo launchctl load -w /Library/LaunchAgents/org.jenkins-ci.plist
Re-start the build. Sit and wait.


Job done

From memory, that was the set-up steps required to get builds to work on a Mac from a fresh Jenkins install. These things aren't really covered by the install guides. They're obvious once you know them. Hindsight is great like that.

Plenty of people followed my whining on Twitter with disbelief, saying that Jenkins "just works" for them. Others suggested moving over to Hudson instead, but I imagine I'd've had the same issues there.

Perhaps I'm unusual, and this stuff does just work for everyone else. If that's not the case, then I hope this rant proves useful.

As a postscript, I now have my Jenkins server working well. I have configured a Windows client, running under a Parallels virtual machine on the same computer. It's not the fastest build server when both run together, but it's passable.

There are definitely some rough edges and features lacking from Jekins, but I can't complain at the price. And there are plenty of excellent plugins that really do make it a very capable build server.
