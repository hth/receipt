Install java

First Step:

	Install Logstash on Shipper
	/etc/logstash/conf.d/receiptofi.shipper.conf

	input {
		file {
			type => "syslog"
			path => ["/var/log/receiptofi/*.log"]
			exclude => ["*.gz", "shipper.log"]
			sincedb_path => "/opt/logstash/sincedb-access"
		}
	}

	output {
		stdout { }
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
		  <string>/usr/local/logstash-1.4.1/bin/logstash</string>
		  <string>agent</string>
		  <string>-f</string>
		  <string>/etc/logstash/conf.d/receiptofi.shipper.conf</string>
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

	sudo launchctl unload /Library/LaunchDaemons/logstash.plist
	sudo launchctl load /Library/LaunchDaemons/logstash.plist



Second Step:

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

	Note: Change the binding of redis.conf to 192.168.1.74
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

	open port for listening
	--sudo ipfw add 100 allow tcp from any to any dst-port 6379
	sudo ipfw add 100 allow tcp from 192.168.1.60 to any dst-port 6379

	sudo launchctl unload /Library/LaunchDaemons/redis.plist
	sudo launchctl load /Library/LaunchDaemons/redis.plist

	to check which ports are open
	sudo lsof -i -P | grep -i "listen"

Install elastic search on Central

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
		<string>/dev/null</string>
		<key>StandardOutPath</key>
		<string>/dev/null</string>
	  </dict>
	</plist>

	plutil -lint elasticsearch.plist

	sudo launchctl unload /Library/LaunchDaemons/elasticsearch.plist
	sudo launchctl load /Library/LaunchDaemons/elasticsearch.plist

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
		}
	}

	output {
		stdout { }
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