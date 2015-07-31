    #Date July 31 07:00 AM
    input {
        file {
            type => "live_app"
            path => ["/usr/local/var/log/receiptofi/receiptofi.log"]
            exclude => ["launchd.stderr.log", "launchd.stdout.log"]
            sincedb_path => "/opt/logstash/sincedb-access"
            tags => "live"
            codec => "json"
    
            # think about
            # stat_interval => 15
            # start_position => beginning
    
            # no need for this as line are clubbed together
            # codec => multiline {
            #  pattern => "^\s"
            #  what => "previous"
            # }
        }
    
        file {
            type => "live_mobile_app"
            path => ["/usr/local/var/log/receiptofi/receiptofi-mobile.log"]
            exclude => ["launchd.stderr.log", "launchd.stdout.log"]
            sincedb_path => "/opt/logstash/sincedb-access"
            tags => "live_mobile"
            codec => "json"
        }
    
        file {
            type => "live_app_mongo"
            path => ["/usr/local/var/log/mongodb/mongo.log"]
            exclude => ["output.log"]
            sincedb_path => "/opt/logstash/sincedb-access"
            tags => "live_mongo"
            codec => "json"
        }
    }
    
    filter {
        if [type] == "nginx" {
            grok {
                match => ["message", "%{COMBINEDAPACHELOG}"]
                add_tag => ["grokked"]
            }
        }
    
        if [type] == "live_app_mongo" {
            grok { pattern => ["(?m)%{GREEDYDATA} \[conn%{NUMBER:mongoConnection}\] %{WORD:mongoCommand} %{NOTSPACE:mongoDatabase} %{WORD}: \{ %{GREEDYDATA:mongoStatement} \} %{GREEDYDATA} %{NUMBER:mongoElapsedTime:int}ms"] }
            grok { pattern => [" cursorid:%{NUMBER:mongoCursorId}"] }
            grok { pattern => [" ntoreturn:%{NUMBER:mongoNumberToReturn:int}"] }
            grok { pattern => [" ntoskip:%{NUMBER:mongoNumberToSkip:int}"] }
            grok { pattern => [" nscanned:%{NUMBER:mongoNumberScanned:int}"] }
            grok { pattern => [" scanAndOrder:%{NUMBER:mongoScanAndOrder:int}"] }
            grok { pattern => [" idhack:%{NUMBER:mongoIdHack:int}"] }
            grok { pattern => [" nmoved:%{NUMBER:mongoNumberMoved:int}"] }
            grok { pattern => [" nupdated:%{NUMBER:mongoNumberUpdated:int}"] }
            grok { pattern => [" keyUpdates:%{NUMBER:mongoKeyUpdates:int}"] }
            grok { pattern => [" numYields: %{NUMBER:mongoNumYields:int}"] }
            grok { pattern => [" locks\(micros\) r:%{NUMBER:mongoReadLocks:int}"] }
            grok { pattern => [" locks\(micros\) w:%{NUMBER:mongoWriteLocks:int}"] }
            grok { pattern => [" nreturned:%{NUMBER:mongoNumberReturned:int}"] }
            grok { pattern => [" reslen:%{NUMBER:mongoResultLength:int}"] }
        }
    }
    
    output {
        redis {
            host => "192.168.1.74"
            data_type => "list"
            key => "logstash"
        }
    }
