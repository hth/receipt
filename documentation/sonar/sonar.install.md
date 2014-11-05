Sonar
=====

Using `homebrew` install `mysql` and `sonar` and `sonar-runner`.

After this execute command 

    mysql --user=root mysql
    
Followed by command [here](https://github.com/SonarSource/sonar-examples/tree/master/scripts/database/mysql "Create Sonar DB")

    CREATE DATABASE sonar CHARACTER SET utf8 COLLATE utf8_general_ci;

    CREATE USER 'sonar' IDENTIFIED BY 'sonar';
    GRANT ALL ON sonar.* TO 'sonar'@'%' IDENTIFIED BY 'sonar';
    GRANT ALL ON sonar.* TO 'sonar'@'localhost' IDENTIFIED BY 'sonar';
    FLUSH PRIVILEGES

To drop    

    DROP DATABASE IF EXISTS sonar;
    DROP USER 'sonar'@'localhost';
    DROP USER 'sonar'@'%';

Then configure **[nginx.conf](../nginx/nginx.conf.md)** to map with `https://sonar.receiptofi.com` and note local LAN
users should have access. Update `/etc/hosts` file with `sonar` IP and sub-domain. Do not forget to update the `hosts`
file in development environment.

### Configure IntelliJ with Sonar

Avoid installing `IntelliJ Sonar Plugin` from `Sonar`. Currently this plugin supports multi-module `Maven` projects and
not `Gradle` based project.

So far `IntelliJ` is not well configured to work with `Sonar`. Plugins fails with error message

    Only multi-module Maven projects are supported for now

### Configure Sonar to work with gradle plugin on Jenkins machine

Follow steps on http://docs.codehaus.org/display/SONAR/Setup+and+Upgrade

This seems to not have worked either

### Command line execution on Jenkins machine with sonar-runner

Once in the workspace of the project `/Users/Shared/Jenkins/../workspace` and with all the configuration changes made as
suggested in `brew info sonar-runner`

Refer: http://docs.codehaus.org/display/SONAR/Analyzing+with+Gradle

    sonar-runner -Dsonar.login=myLogin -Dsonar.password=myPassword

The above command looks at the `sonar-project.properties` to run analysis. Run as `cron` once a day on test.

### Setting cron task

    env EDITOR=nano crontab -e

And then type

    0   7   *   *   *   cd /absolute.path.from.root/location.to
        && chmod 544 /absolute.path.from.root/location.to/sonar.sh
        && /absolute.path.from.root/location.to/sonar.sh
           >> /absolute.path.from.root/location.to/cron.txt

Remember change mode to execute

    chmod 544 sonar.sh
