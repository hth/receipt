### Redis

#### Update local apt package cache and install the dependencies:

    sudo apt-get update &&
    sudo apt-get install build-essential tcl
  
#### Download REDIS

    cd /tmp &&
    curl -O http://download.redis.io/releases/redis-3.2.6.tar.gz

#### Unpack the Tarball
  
    tar xzvf redis-3.2.6.tar.gz

#### Build and Install REDIS

    cd /tmp/redis-3.2.6 &&
    make && 
    make test && 
    sudo make install

#### Configure REDIS
    
    sudo mkdir /etc/redis &&
    sudo chown -R db:db /etc/redis && 
    sudo cp /tmp/redis-3.2.6/redis.conf /etc/redis

#### Modify the redis.conf file

    sudo vi  /etc/redis/redis.conf

#### Changes in redis.conf

    supervised no => TO => supervised systemd
    logfile "" => TO => logfile "/var/log/redis/redis.log"
    dir ./ => TO => /data/redis

#### Create log directory /var/log/redis

    sudo mkdir /var/log/redis &&
    sudo chown db:db /var/log/redis &&
    sudo mkdir /data/redis &&
    sudo chown db:db /data/redis

#### Creating REDIS system file 
   
    sudo touch /etc/systemd/system/redis.service && 
    sudo chown db:db /etc/systemd/system/redis.service &&
    sudo nano /etc/systemd/system/redis.service

Add to file

    [Unit]
    Description=Redis In-Memory Data Store
    After=network.target
    
    [Service]
    User=db
    Group=db
    ExecStart=/usr/local/bin/redis-server /etc/redis/redis.conf
    ExecStop=/usr/local/bin/redis-cli shutdown
    Restart=always
    
    [Install]
    WantedBy=multi-user.target
    
#### Enable Redis to Start at Boot
   
    sudo systemctl enable redis    

#### Start up the systemd service by typing:
   
    sudo systemctl start redis
    sudo systemctl restart redis

#### Check that the service had no errors by running:
   
    sudo systemctl status redis

#### TEST if REDIS working fine ---

 Check Redis Instance
   
    redis-cli
 test connectivity by typing:
   
    127.0.0.1:6379 > ping

 You should see:
    
    PONG

Test :
 127.0.0.1:6379> set test "It's working!"
	OK
127.0.0.1:6379> get test
	"It's working!"    