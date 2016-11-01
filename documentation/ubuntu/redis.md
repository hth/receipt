### Redis

https://www.globo.tech/learning-center/install-configure-redis-ubuntu-16/
    
Tutorial

Ensure that your installation of Ubuntu 16 is fully up to date with this command.

    apt-get update && apt-get upgrade -y

Install the redis server package, which is included in the base repositories.

    apt-get install redis-server -y

To prevent others from accessing your Redis keystore, you will need to make sure that Redis is bound to your local IP address. This is hands-down the safest security option, and the best one for this particular guide.

First, open the Redis configuration in a text editor.

    nano /etc/redis/redis.conf

Next, search for the line which starts with bind. Change it to this line instead:

    bind 127.0.0.1

Start the Redis server.

    service redis-server start

In order for Redis to start up again when the server is restarted, enable it to start on boot.

    update-rc.d redis-server enable
    update-rc.d redis-server defaults

Redis is installed! You can now enter the first data into your new Redis server.

    root@redis-node:~# redis-cli
    127.0.0.1:6379> set besthost "Globo.Tech"
    OK
    127.0.0.1:6379> get besthost
    "Globo.Tech"  