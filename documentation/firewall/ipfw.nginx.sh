sudo ipfw add 100 fwd 127.0.0.1,8080 tcp from any to any 80 in
sudo ipfw add 110 fwd 127.0.0.1,8443 tcp from any to any 443 in
sudo ipfw add 120 allow tcp from 192.168.1.74 to any dst-port 6379
