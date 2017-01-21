### Logstash setup

Install java the box where you plan to use logstash

#### First Step to configure shipper

Install Logstash on Shipper which is ***Test*** and ***Prod*** machine. Shipper machine is any machine that pushes
logs to central machine called ***Smoker*** in our environment

	/etc/logstash/conf.d/receiptofi.shipper.conf
	
When installing through brew, create <code>logstash.conf</code> 

	mkdir /usr/local/etc
	touch logstash.conf

Note: 
- type => ***test_app*** is indexed; for prod it will be type => ***live_app***
- codec => multiline not sure how much beneficial
- http://grokdebug.herokuapp.com/ to test grok
- Example http://www.logstashbook.com/code/6/shipper.conf
- Grok patterns https://github.com/elasticsearch/logstash/blob/master/patterns/grok-patterns

Command to test conf

	/usr/local/Cellar/logstash/1.4.2/libexec/bin/logstash --configtest --verbose -f /usr/local/etc/logstash.conf 
	
Command to run logstash

	/usr/local/Cellar/logstash/1.4.2/libexec/bin/logstash agent --verbose -f /usr/local/etc/logstash.conf 

Populate Logstash conf for `test shipper` from here **[logstash.conf](logstash.conf.md)** and for `live shipper` from here **[logstash.conf](logstash.conf.live.md)**

File content for ***logstash.plist***

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
	      <string>/usr/local/var/log/logstash/logstash.log</string>
	    </array>
	    <key>RunAtLoad</key>
	    <true/>
	    <key>WorkingDirectory</key>
	    <string>/usr/local/Cellar/logstash/1.4.2/libexec</string>
	  </dict>
	</plist>


Latest for 1.5.2. Don't forget to create logstash folder for logs. 

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
	      <string>/usr/local/Cellar/logstash/1.5.2/libexec/bin/logstash</string>
	      <string>agent</string>
	      <string>-f</string>
	      <string>/usr/local/etc/logstash.conf</string>
	      <string>--log</string>
	      <string>/usr/local/var/log/logstash/logstash.log</string>
	    </array>
	    <key>RunAtLoad</key>
	    <true/>
	    <key>WorkingDirectory</key>
	    <string>/usr/local/Cellar/logstash/1.5.2/libexec</string>
	  </dict>
	</plist>


	cmd
	Verbose
	bin/logstash agent --verbose -f /etc/logstash/conf.d/receiptofi.shipper.conf

How to test configuration

	bin/logstash agent --configtest --config /etc/logstash/conf.d/receiptofi.shipper.conf

Daemon to Load and Unload logstash on shipper

	sudo launchctl unload -w /Library/LaunchDaemons/logstash.plist
	sudo launchctl load -w /Library/LaunchDaemons/logstash.plist

Update firewall to allow redis on ***port 6379***; and the reload firewall

	sudo ipfw add 120 allow tcp from 192.168.1.74 to any dst-port 6379

Note: ***The above steps should get it running on shipper. Since central is not yet created, shipper will throw warnings***

#### Second Step to configure central

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

File content for ***redis.plist***

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

Daemon to Load and Unload redis on central

	sudo launchctl unload -w /Library/LaunchDaemons/redis.plist
	sudo launchctl load -w /Library/LaunchDaemons/redis.plist

To check which ports are open

	sudo lsof -i -P | grep -i "listen"

Install elasticsearch on Central

Note: ***Elasticsearch is flaky to start when IP address is not available. This causes logstash to fail. There is no way to fix it. Tried specifying static IP to the machine but that did not worked. Currently, starts the machine, reload elasticsearch and logstash. Everything is jolly after this.***

	/etc/elasticsearch/conf.d/elasticsearch.yml
	sudo chown root:wheel elasticsearch.yml

	cluster.name: logstash
	node.name: "smoker"

	mkdir /var/lib/elasticsearch

File content for ***elasticsearch.plist***

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

This will check the syntax

	plutil -lint elasticsearch.plist

Daemon to Load and Unload elasticsearch on central

	sudo launchctl unload /Library/LaunchDaemons/elasticsearch.plist
	sudo launchctl load /Library/LaunchDaemons/elasticsearch.plist

To delete all index
Note: Do not delete all, unless you open all the dashboard or save all the dashboard, since they will get deleted 

	curl -XDELETE 'http://localhost:9200/*/'
	curl -XDELETE 'http://localhost:9200/filebeat-2016.**.**/'
	curl -XDELETE 'http://localhost:9200/filebeat-2017.01.**/'

To see stats, indices on host where `Elastic` is setup

	curl -XGET http://localhost:9200/_stats
	curl -XGET http://localhost:9200/_cat/indices?v
	curl -XGET http://localhost:9200/_all/_search?pretty

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

File content for ***logstash.plist***

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

This will check the syntax

	plutil -lint logstash.plist

Daemon to Load and Unload logstash on central

	sudo launchctl unload /Library/LaunchDaemons/logstash.plist
	sudo launchctl load /Library/LaunchDaemons/logstash.plist

File content for ***logstash.web.plist***

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

This will check the syntax

	plutil -lint logstash.web.plist

Daemon to Load and Unload logstash.web on central

	sudo launchctl unload /Library/LaunchDaemons/logstash.web.plist
	sudo launchctl load /Library/LaunchDaemons/logstash.web.plist

#### Kibana exists in logstash

	find /usr/local -name "config.js"
	/usr/local/logstash-1.4.1/vendor/kibana/config.js

see who can connect to redis by executing command

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
