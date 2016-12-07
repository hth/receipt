## Add new mongo replica set

- Add `hostname` to `/etc/host` file. Hostname are `s1,s2,s3 or r1,r2..`
- Go to primary and see if you can connect to `s1` --> `mongo --host s1 --port 27017` 
- Execute on primary `rs.add("s1")`
- Execute `rs.status()` to check status

## Make the newly added replica as always secondary

- On primary, execute `cfg = rs.conf()`
- Note: 2 is the third member in array `cfg.members[2].priority = 0`
- Then, `rs.reconfig(cfg)`
- To check priority execute `cfg = rs.conf()`
- The member set to `0` will have `priority = 0`

## Remove replica set

- execute `rs.remove("s1:27017")`
    