### Install java
    
Download `jdk-8u112-linux-x64.tar.gz`. 

    sftp l4@192.X.X.X
    put javax
    exit
    
    ssh l4@192.X.X.X
    
Installing Java in `/opt/java`

    tar -xvf jdk-8u112-linux-x64.tar.gz &&
    sudo mkdir /opt/java &&
    sudo mv jdk1.8.0_112 /opt/java/ &&
    rm jdk-8u112-linux-x64.tar.gz && 
    sudo ln -s /opt/java/jdk1.8.0_112 /usr/local/java 
    
Ownership of link remains with `root` and cannot be converted to `db`    

Screenshot sample    

    drwxr-xr-x  3 root root 4096 Oct  9 18:55 .
    drwxr-xr-x 23 root root 4096 Oct  9 18:40 ..
    drwxr-xr-x  3 root root 4096 Oct  9 18:56 java
    l4@l4:/opt$ cd java/
    l4@l4:/opt/java$ ls -al
    total 12
    drwxr-xr-x 3 root root 4096 Oct  9 18:56 .
    drwxr-xr-x 3 root root 4096 Oct  9 18:55 ..
    drwxr-xr-x 8 l4   l4   4096 Jun 22 18:56 jdk1.8.0_102
    l4@l4:/opt/java$     

Set JAVA_HOME
 
    nano ~/.bash_profile
    JAVA_HOME=/usr/local/java
    export PATH=$JAVA_HOME/bin:$PATH

Source to import environment
    
    source ~/.bash_profile
    java -version
