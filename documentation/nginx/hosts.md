Notice test and smoker and receiptofi are on same machine. They can be on different machine in future. 

##### Hosts content from nginx    
    192.168.1.71    receiptofi.com          test.receiptofi.com     live.receiptofi.com     smoker.receiptofi.com
    192.168.1.68    build.receiptofi.com

    192.168.1.68    build
    192.168.1.74    smoker
    192.168.1.71    test
    192.168.1.75    live

##### Hosts content from dev machine
    192.168.1.71    receiptofi.com  live.receiptofi.com     test.receiptofi.com     smoker.receiptofi.com

    #Mongo Servers
    192.168.1.75    m2
    192.168.1.75    ofi2
    192.168.1.75    primary
    192.168.1.75    primary.receiptofi.com

    192.168.1.69    m1
    192.168.1.69    ofi1
    192.168.1.69    secondary1
    192.168.1.69    secondary-1.receiptofi.com

    192.168.1.70    m3
    192.168.1.70    ofi3
    192.168.1.70    secondary2
    192.168.1.70    secondary2.receiptofi.com

    #Short hostname
    192.168.1.68    build
    192.168.1.74    smoker
    192.168.1.71    test
    192.168.1.75    live