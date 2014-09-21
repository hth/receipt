### Logstash steup

Install java

First Step:

Install Logstash on Shipper (Test machine)
	/etc/logstash/conf.d/receiptofi.shipper.conf
	
When installing through brew, create <code>logstash.conf</code> 
	mkdir /usr/local/etc
	touch logstash.conf

Note: 
- type => ***test_app*** is indexed; for prod it will be type => ***prod_app***
- codec => multiline not sure how much beneficial
- http://grokdebug.herokuapp.com/ to test grok
- Example http://www.logstashbook.com/code/6/shipper.conf
- Grok patterns https://github.com/elasticsearch/logstash/blob/master/patterns/grok-patterns

Command to test conf

	/usr/local/Cellar/logstash/1.4.2/libexec/bin/logstash --configtest --verbose -f /usr/local/etc/logstash.conf 
	
Command to run logstash

	/usr/local/Cellar/logstash/1.4.2/libexec/bin/logstash agent --verbose -f /usr/local/etc/logstash.conf 

Conf

        input {
	        file {
	            type => "test_app"
	            path => ["/var/logs/receiptofi/receiptofi.log"]
	            exclude => ["launchd.stderr.log", "launchd.stdout.log"]
	            sincedb_path => "/opt/logstash/sincedb-access"
	            tags => "test"
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
	            type => "test_mobile_app"
	            path => ["/var/logs/receiptofi/receiptofi-mobile.log"]
	            exclude => ["launchd.stderr.log", "launchd.stdout.log"]
	            sincedb_path => "/opt/logstash/sincedb-access"
	            tags => "test_mobile"
	            codec => "json"
	        }
	
	        file {
	            type => "nginx"
	            path => ["/var/logs/nginx/*.log"]
	            exclude => ["test.access.log", "live.access.log"]
	            sincedb_path => "/opt/logstash/sincedb-access"
	            tags => "nginx"
	            codec => "json"
	        }
	
	        file {
	            type => "test_app_nginx"
	            path => ["/var/logs/nginx/test.access.log"]
	            exclude => ["access.log", "error.log", "live.access.log"]
	            sincedb_path => "/opt/logstash/sincedb-access"
	            tags => "test_nginx"
	            codec => "json"
	        }
	
	        file {
	            type => "test_app_mongo"
	            path => ["/var/logs/mongo/mongo.log"]
	            exclude => ["output.log"]
	            sincedb_path => "/opt/logstash/sincedb-access"
	            tags => "test_mongo"
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
	
	        if [type] == "test_app_mongo" {
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


logstash.plist

	<?xml version="1.0" encoding="UTF-8"?>
	<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
	<plist version="1.0">
	  <dict>
	    <key>KeepAlive</key>
	    <true/>
	    <key>Label</key>
	    <string>logstash</string>
	    <key>ProgramArguments</key>
	    <array>
	      <string>/usr/local/Cellar/logstash/1.4.2/libexec/bin/logstash</string>
	      <string>agent</string>
	      <string>-f</string>
	      <string>/usr/local/etc/logstash.conf</string>
	      <string>--log</string>
	      <string>/var/logs/logstash/logstash.log</string>
	    </array>
	    <key>RunAtLoad</key>
	    <true/>
	    <key>WorkingDirectory</key>
	    <string>/usr/local/Cellar/logstash/1.4.2/libexec</string>
	  </dict>
	</plist>

	cmd
	Verbose
	bin/logstash agent --verbose -f /etc/logstash/conf.d/receiptofi.shipper.conf

How to test configuration

	bin/logstash agent --configtest --config /etc/logstash/conf.d/receiptofi.shipper.conf


	sudo launchctl unload -w /Library/LaunchDaemons/logstash.plist
	sudo launchctl load -w /Library/LaunchDaemons/logstash.plist

Update firewall to allow redis on port 6379; and the reload firewall

	sudo ipfw add 120 allow tcp from 192.168.1.74 to any dst-port 6379

	The above steps should get it running on shipper. Since central is not created yet it will throw warnings

Second Step to configure central:

Download redis
	move to /usr/local/redis-x-x-x
	make
	make test
	sudo make install
	cd utils
	sudo ./install_server.sh

	Setting
	Port           : 6379
	Config file    : /etc/redis/redis.conf
	Log file       : /var/log/redis/redis.log
	Data dir       : /var/lib/redis
	Executable     : /usr/local/redis-2.8.9/src/redis-cli
	Cli Executable : /usr/local/redis-2.8.9/src/redis-cli

	Note: Change the binding of redis.conf to 192.168.1.74. Just keep on ip address otherwise it gives (ECONNREFUSED) warn
	sudo chown root:wheel redis.conf

redis.plist

	<?xml version="1.0" encoding="UTF-8"?>
	<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
	<plist version="1.0">
	  <dict>
		<key>KeepAlive</key>
		<true/>
		<key>Label</key>
		<string>redis</string>
		<key>ProgramArguments</key>
		<array>
		  <string>/usr/local/redis-2.8.9/src/redis-server</string>
		  <string>/etc/redis/redis.conf</string>
		</array>
		<key>RunAtLoad</key>
		<true/>
		<key>WorkingDirectory</key>
		<string>/var/lib/redis</string>
		<key>StandardErrorPath</key>
		<string>/var/log/redis/redis.log</string>
		<key>StandardOutPath</key>
		<string>/var/log/redis/redis.log</string>
	  </dict>
	</plist>


This will check the syntax

	plutil -lint redis.plist

	sudo launchctl unload -w /Library/LaunchDaemons/redis.plist
	sudo launchctl load -w /Library/LaunchDaemons/redis.plist

To check which ports are open

	sudo lsof -i -P | grep -i "listen"

Install elastic search on Central

	Note: Elasticsearch is flaky to start when IP address is not available. This causes logstash to fail.
			There is no way to fix it. Tried specifying static IP to the machine but that did not worked.
			Currently, starts the machine, reload elasticsearch and logstash. Everything is jolly after this.

	/etc/elasticsearch/conf.d/elasticsearch.yml
	sudo chown root:wheel elasticsearch.yml

	cluster.name: logstash
	node.name: "smoker"

	mkdir /var/lib/elasticsearch

	<?xml version="1.0" encoding="UTF-8"?>
	<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
	<plist version="1.0">
	  <dict>
		<key>KeepAlive</key>
		<true/>
		<key>Label</key>
		<string>elasticsearch</string>
		<key>ProgramArguments</key>
		<array>
		  <string>/usr/local/elasticsearch-1.1.1/bin/elasticsearch</string>
		  <string>--config=/etc/elasticsearch/conf.d/elasticsearch.yml</string>
		</array>
		<key>EnvironmentVariables</key>
		<dict>
		  <key>ES_JAVA_OPTS</key>
		  <string>-Xss200000</string>
		</dict>
		<key>RunAtLoad</key>
		<true/>
		<key>WorkingDirectory</key>
		<string>/var/lib/elasticsearch</string>
		<key>StandardErrorPath</key>
		<string>/var/log/elasticsearch/elasticsearch.log</string>
		<key>StandardOutPath</key>
		<string>/var/log/elasticsearch/elasticsearch.log</string>
	  </dict>
	</plist>

	plutil -lint elasticsearch.plist

	sudo launchctl unload /Library/LaunchDaemons/elasticsearch.plist
	sudo launchctl load /Library/LaunchDaemons/elasticsearch.plist

To delete all index

	curl -XDELETE 'http://localhost:9200/*/'

To see stats, indices

	http://192.168.1.74:9200/_stats
	http://192.168.1.74:9200/_cat/indices?v
	http://192.168.1.74:9200/_all/_search?pretty

Install Logstash on Central Server

	/etc/logstash/conf.d/receiptofi.central.conf
	sudo chown root:wheel receiptofi.central.conf

Note: stdout {} - In a production environment you would probably disable this to prevent any excess noise being generated.

	input {
		redis {
			host => "192.168.1.74"
			type => "redis-input"
			data_type => "list"
			key => "logstash"
			codec => "json"
		}
	}

	output {
		elasticsearch {
			cluster => "logstash"
		}
	}

	<?xml version="1.0" encoding="UTF-8"?>
	<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
	<plist version="1.0">
	  <dict>
		<key>KeepAlive</key>
		<true/>
		<key>Label</key>
		<string>logstash</string>
		<key>ProgramArguments</key>
		<array>
		  <string>/usr/local/logstash-1.4.1/bin/logstash</string>
		  <string>agent</string>
		  <string>-f</string>
		  <string>/etc/logstash/conf.d/receiptofi.central.conf</string>
		  <string>--log</string>
		  <string>/var/log/logstash/logstash.log</string>
		</array>
		<key>RunAtLoad</key>
		<true/>
		<key>WorkingDirectory</key>
		<string>/var/lib/logstash</string>
		<!--
		<key>StandardErrorPath</key>
		<string>/var/log/logstash/logstash.log</string>
		<key>StandardOutPath</key>
		<string>/var/log/logstash/logstash.log</string>
		-->
	  </dict>
	</plist>

	plutil -lint logstash.plist

	sudo launchctl unload /Library/LaunchDaemons/logstash.plist
	sudo launchctl load /Library/LaunchDaemons/logstash.plist

logstash.web.plist

	<?xml version="1.0" encoding="UTF-8"?>
	<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
	<plist version="1.0">
	  	<dict>
		<key>KeepAlive</key>
		<true/>
		<key>Label</key>
		<string>logstash.web</string>
		<key>ProgramArguments</key>
		<array>
	  		<string>/usr/local/logstash-1.4.1/bin/logstash</string>
	  		<string>web</string>
		</array>
		<key>RunAtLoad</key>
		<true/>
		<key>WorkingDirectory</key>
		<string>/dev/null</string>
		<!--
		should be dev null I believe
		<key>StandardErrorPath</key>
		<string>/var/log/logstash/logstash.log</string>
		<key>StandardOutPath</key>
		<string>/var/log/logstash/logstash.log</string>
		-->
	  	</dict>
	</plist>

	plutil -lint logstash.web.plist

	sudo launchctl unload /Library/LaunchDaemons/logstash.web.plist
	sudo launchctl load /Library/LaunchDaemons/logstash.web.plist

	see who can connect to redis by
	client list

	........................ END ...............

	############### DON'T NEED THIS FIREWALL SECTION #############
	############### DON'T NEED THIS FIREWALL SECTION #############
	############### DON'T NEED THIS FIREWALL SECTION #############
	############### DON'T NEED THIS FIREWALL SECTION #############

	firewall.plist

	<?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE plist PUBLIC "-//Apple Computer//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
    <plist version="1.0">
        <dict>
            <key>Label</key>
            <string>ipfw</string>
            <key>OnDemand</key>
            <false/>
            <key>ProgramArguments</key>
            <array>
                <string>/usr/local/ipfw.startup.sh</string>
            </array>
            <key>RunAtLoad</key>
            <true/>
            <key>LaunchOnlyOnce</key>
            <true/>
            <key>ServiceDescription</key>
            <string>IPFW Filter Rules</string>
            <key>StandardErrorPath</key>
            <string>/var/log/firewall/ipfw.stderr.log</string>
            <key>StandardOutPath</key>
            <string>/var/log/firewall/ipfw.stdout.log</string>
            <key>UserName</key>
            <string>root</string>
        </dict>
    </plist>

    plutil -lint redis.plist

	open port for listening open port for listening is not required. It works without this line
	file -- ipfw.startup.sh
	--sudo ipfw add 100 allow tcp from any to any dst-port 6379
	sudo ipfw add 100 allow tcp from 192.168.1.60 to any dst-port 6379

	sudo launchctl unload /Library/LaunchDaemons/firewall.plist
    sudo launchctl load /Library/LaunchDaemons/firewall.plist

Set Mail to send email from central logstash

	Follow readme for setting email alerts and if possible the link
	http://benjaminrojas.net/configuring-postfix-to-send-mail-from-mac-os-x-mountain-lion/

Gork Debugger Site
	http://grokdebug.herokuapp.com/
	http://drewsearcy.com/email-alerts-with-logstash/
