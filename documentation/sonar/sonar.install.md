Sonar
=====

Using `homebrew` install `Mysql` and `Sonar`.

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

Then configure **[nginx.conf](../nginx/nginx.conf.md)** to map with `https://sonar.receiptofi.com` and note local LAN users should have access.
Install `IntelliJ Sonar Plugin` from `Sonar`.

    
