#### Connect to Nginx

    Please find the server details for test environment..
    
    ssh -i ~/Downloads/test.pem  ec2-user@ec2-54-203-104-138.us-west-2.compute.amazonaws.com
    
    Server : ec2-54-203-104-138.us-west-2.compute.amazonaws.com
    user : ec2-user
    Password : attached key
    Nginx Directory : /usr/share/nginx/html
    
    S3 Bucket : test-resp.s3-website-us-west-2.amazonaws.com
    
### History to install Nginx
    ec2-user@ip-10-251-5-13 ~]$ history
        1  sudo su
        2  sudo su
        3  ps -ef | grep nginx
        4  /etc/init.d/nginx start
        5  sudo su
        6  uptime
        7  history 
        8  sudo su
        9  nginx -t
       10  pwd
       11  ls -al
       12  history

     sudo su
    
    [root@ip-10-251-5-13 ec2-user]# history
        1  yum install nginx
        2  /etc/init.d/nginx start
        3  yum install mongo
        4  yum install mongo*
        5  yum install mongodb
        6  yum install mongodb*
        7  vi /etc/yum.repos.d/10gen.repo
        8  yum install mongodb*
        9  yum install mongodb
       10  yum install mongo-10gen mongo-10gen-server
       11  /etc/init.d/mongod start
       12  top -c
       13  ps -ef | grep mongo
       14  netstat -antlp | grep mongo
       15  cd /etc/nginx/
       16  ls
       17  view nginx.conf
       18  /etc/init.d/nginx start
       19  chkconfig nginx on
       20  vi /etc/nginx/
       21  cd /etc/nginx/
       22  ls
       23  vi nginx.conf
       24  vi /etc/nginx/nginx.conf
       25  cd /etc/nginx/
       26  ls
       27  vi nginx.conf
       28  vi /etc/nginx/nginx.conf
       29  vi /etc/nginx/nginx.conf
       30  /usr/sbin/nginx -t
       31  /etc/init.d/nginx stop
       32  /etc/init.d/nginx start
       33  curl localhost/test-resp/Flowers.jpg
       34  /etc/init.d/nginx stop
       35  /etc/init.d/nginx start
       36  vi /etc/nginx/nginx.conf
       37  /etc/init.d/nginx stop
       38  /etc/init.d/nginx start
       39  vi /etc/nginx/nginx.conf
       40  /etc/init.d/nginx stop
       41  /etc/init.d/nginx start
       42  cd /etc/nginx/
       43  ls
       44  vi nginx.conf
       45  ls -a
       46  rm .nginx.conf.sw*
       47  vi nginx.conf
       48  /etc/init.d/nginx stop
       49  /etc/init.d/nginx start
       50  vi nginx.conf
       51  history 
       52  vi /etc/nginx/nginx.conf
       53  /etc/init.d/nginx stop
       54  /etc/init.d/nginx start
       55  nginx -t
       56  history   
