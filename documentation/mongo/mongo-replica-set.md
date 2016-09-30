Before embarking on setting the replica set make sure three machines are set as mentioned in previous MongoDB setup blog
post.

Once everything is set make sure all three machines can communicate with each other. Mostly there will be a either a
firewall issue that could be limiting the communication or bind_ip property in mongod.conf that might have been left
un-commented. I had no success with setting bind_ip to specific ip. So had to comment it out. When commented MongoDB
listens to all the interfaces.

Also would recommend set the /etc/hosts file to map m1, m2, m3 with specific ip addresses.

The process to check if all the machines are connected and communicating (m1, m2, m3)
Connect to s1 and then type the following command to confirm the connection.
Note: Port is not required if the set ports are default. You can also refer official MongoDB site on Replica Setup

    ssh username@m1
    mongo --host m2 --port 27017
    mongo --host m3 --port 27017
    
Do not forget to set the host name through this command for the primary mongo as it take the name from some other. 
Without this replica set fails. If replica set fails then stop `mongo` and delete `db` and restart `mongo` 
    
    sudo scutil -set HostName t1
    
else name is set incorrectly
 
    "name" : "t1:27017", -- incorrect example -- "name" : "t1-mini-local:27017",
       
The above command will help recognize other machine this as t1. Otherwise other mongo would be stuck in `STARTUP` state. 
And of course don't forget to add host info in host files.   

Repeat the same test on m2 and m3. In total, 6 results should show success in connection
On Mac, oplog size is limited and normally MongoDB complains about it but if you notice
we have already set the parameter in mongod.conf file to 1024

Now you are almost ready to have Mongo Replica working with two secondaries and one primary.

Do the following on the primary of your choice. My choice is 'm2'.
Connect to mongo on the primary you like. Note: This is still not the primary.

On shell type: mongo
Then type. Note: This takes some time. Have patience. At this point I would advice you
take a small break. [ '>' is mongo prompt]

	>rs.initiate()

On screen you will see something like. Do the above command on `primary`. 

	m2:~ db$ mongo
    MongoDB shell version: 2.4.6
    connecting to: test
    > rs.initiate()

    {
    	"info2" : "no configuration explicitly specified -- making one",
    	"me" : "m2:27017",
    	"info" : "Config now saved locally.  Should come online in about a minute.",
    	"ok" : 1
    }
    flab:STARTUP2>

['>' Mongo prompt again]. This means Mongo is ready for replica. This machine is not primary yet. In a second it will
be assigned as primary. Mongo decides that.

Then type command rs.conf() on same shell or start a new shell and type mongo then type the command rs.conf()

	flab:PRIMARY> rs.conf();
    {
    	"_id" : "flab",
    	"version" : 1,
    	"members" : [
    		{
    			"_id" : 0,
    			"host" : "m2:27017"
    		}
    	]
    }

At this point you are ready to add the secondary host. If you have set hosts name in '/etc/hosts' file then just pass
the host name or else pass ip address. Preferred way is to set the host name for convenience.

	flab:PRIMARY> rs.add("m1")
	{ "ok" : 1 }
	flab:PRIMARY> rs.add("m3")
	{ "ok" : 1 }

You will see a message for both saying

	{ "ok" : 1 }

Now run rs.conf(); & run rs.status() command to see the list of host mapped and listed as secondary. Run the same
command on host m1 and m3 to see almost the same result.

    flab:PRIMARY> rs.conf();
    {
        "_id" : "flab",
        "version" : 3,
        "members" : [
            {
                "_id" : 0,
                "host" : "m2:27017"
            },
            {
                "_id" : 1,
                "host" : "m1:27017"
            },
            {
                "_id" : 2,
                "host" : "m3:27017"
            }
        ]
    }
    flab:PRIMARY> rs.status();
    {
        "set" : "flab",
        "date" : ISODate("2013-08-25T03:12:43Z"),
        "myState" : 1,
        "members" : [
            {
                "_id" : 0,
                "name" : "m2:27017",
                "health" : 1,
                "state" : 1,
                "stateStr" : "PRIMARY",
                "uptime" : 727,
                "optime" : Timestamp(1377400316, 1),
                "optimeDate" : ISODate("2013-08-25T03:11:56Z"),
                "self" : true
            },
            {
                "_id" : 1,
                "name" : "m1:27017",
                "health" : 1,
                "state" : 5,
                "stateStr" : "STARTUP2",
                "uptime" : 62,
                "optime" : Timestamp(0, 0),
                "optimeDate" : ISODate("1970-01-01T00:00:00Z"),
                "lastHeartbeat" : ISODate("2013-08-25T03:12:42Z"),
                "lastHeartbeatRecv" : ISODate("2013-08-25T03:12:42Z"),
                "pingMs" : 4
            },
            {
                "_id" : 2,
                "name" : "m3:27017",
                "health" : 1,
                "state" : 5,
                "stateStr" : "STARTUP2",
                "uptime" : 46,
                "optime" : Timestamp(0, 0),
                "optimeDate" : ISODate("1970-01-01T00:00:00Z"),
                "lastHeartbeat" : ISODate("2013-08-25T03:12:42Z"),
                "lastHeartbeatRecv" : ISODate("2013-08-25T03:12:42Z"),
                "pingMs" : 31
            }
        ],
        "ok" : 1
    }
    flab:PRIMARY> exit
    bye

Cut and paste the line below on mongo console 

    cfg = rs.conf()
    cfg.members[0].priority = 4
    cfg.members[1].priority = 2
    cfg.members[2].priority = 2
    rs.reconfig(cfg)

Higher the number, more likely to be primary.

To check replication status 

    rs.printReplicationInfo()
