http://opennebula.org/tryout/sandbox-testdrive/

http://blog.zadarastorage.com/2013/09/comparing-nas-at-aws-cloud.html

http://simplyopensource.blogspot.com/2014/01/how-to-configure-raid-0-in-aws-with.html

http://bioteam.net/2010/07/playing-with-nfs-glusterfs-on-amazon-cc1-4xlarge-ec2-instance-types/

http://pyd.io/glusterfs/

http://gluster.org/

http://simplyopensource.blogspot.com/2013/11/how-to-implement-glusterfs-in-amazon.html
sudo su
and then follow the documentation

The Gluster file system is used to have high availability via use of data replication in servers, following are the steps to implement it in Amazon AMI's:

As Amazon AMI don't have GlusterFS repo enabled in it so we need to enable it..

	[root@ip-10-144-143-144 ec2-user]# wget -P /etc/yum.repos.d http://download.gluster.org/pub/gluster/glusterfs/LATEST/EPEL.repo/glusterfs-epel.repo

	[root@ip-10-144-143-144 ec2-user]# sed -i 's/$releasever/6/g' /etc/yum.repos.d/glusterfs-epel.repo

Install the required dependencies..

	[root@ip-10-144-143-144 ec2-user]# yum install libibverbs-devel fuse-devel -y

Install the Gluster Server and Fuse packets in master server..

	[root@ip-10-144-143-144 ec2-user]# yum install -y glusterfs{-fuse,-server}

Start the Gluster service in server..

	[root@ip-10-144-143-144 ec2-user]# service glusterd start
	Starting glusterd:                                         [  OK  ]

Follow the same procedure in client server and install the GlusterFS packages and start the service..

Add file system to Kernal..

	[root@ip-10-144-143-144 ec2-user]# modprobe fuse

Add a truster peer pool storage.. don`t forget to add *'default VPC security group'* *sg-f5942990*

	[root@ip-10-144-143-144 ec2-user]# gluster peer probe  ec2-54-254-58-214.ap-southeast-1.compute.amazonaws.com
	peer probe: success

Check the status of the peer..

	[root@ip-10-144-143-144 ec2-user]# gluster peer probe  ec2-54-254-58-214.ap-southeast-1.compute.amazonaws.com
	peer probe: success
	[root@ip-10-144-143-144 ec2-user]# gluster peer status
	Number of Peers: 1

	Hostname: ec2-54-254-58-214.ap-southeast-1.compute.amazonaws.com
	Port: 24007
	Uuid: 51c7c768-b046-46ac-a4ad-caa67c1b768d
	State: Peer in Cluster (Connected)

 Create and start volume in both the servers..

	[root@ip-10-144-143-144 ec2-user]# gluster volume create Test-Volume replica 2 transport tcp ec2-122-248-202-153.ap-southeast-1.compute.amazonaws.com:/data1 ec2-54-254-58-214.ap-southeast-1.compute.amazonaws.com:/data2
	volume create: Test-Volume: success: please start the volume to access data

	[root@ip-10-144-143-144 ec2-user]# gluster volume start Test-Volume
	volume start: Test-Volume: success


Check Gluster volume..

	[root@ip-10-144-143-144 ec2-user]# gluster volume info

	Volume Name: Test-Volume
	Type: Replicate
	Volume ID: e5a8a24d-bf47-4770-a2e8-f13993998a51
	Status: Started
	Number of Bricks: 1 x 2 = 2
	Transport-type: tcp
	Bricks:
	Brick1: ec2-122-248-202-153.ap-southeast-1.compute.amazonaws.com:/data1
	Brick2: ec2-54-254-58-214.ap-southeast-1.compute.amazonaws.com:/data2


data1 and data2 will both be having same data in between them its just taken to show that its not neccesary to take same mount name..

create a /data directory in both master and client box so it can have common mount point..


	[root@ip-10-144-143-144 ec2-user]# mkdir /data
	[root@ip-10-144-143-144 ec2-user]# mount -t glusterfs ec2-122-248-202-153.ap-southeast-1.compute.amazonaws.com:Test-Volume /data
	[root@ip-10-144-143-144 ec2-user]# df -h
	Filesystem            Size  Used Avail Use% Mounted on
	/dev/xvda1            7.9G  983M  6.9G  13% /
	tmpfs                 298M     0  298M   0% /dev/shm
	ec2-122-248-202-153.ap-southeast-1.compute.amazonaws.com:Test-Volume
	                      7.9G  983M  6.9G  13% /data


	[root@ip-10-146-2-125 ec2-user]# mkdir /data
	[root@ip-10-146-2-125 ec2-user]#  mount -t glusterfs ec2-122-248-202-153.ap-southeast-1.compute.amazonaws.com:Test-Volume /data
	[root@ip-10-146-2-125 ec2-user]# df -h
	Filesystem            Size  Used Avail Use% Mounted on
	/dev/xvda1            7.9G  981M  6.9G  13% /
	tmpfs                 298M     0  298M   0% /dev/shm
	ec2-122-248-202-153.ap-southeast-1.compute.amazonaws.com:Test-Volume
	                      7.9G  983M  6.9G  13% /data



 To test files are replicating as required or not..


	[root@ip-10-144-143-144 ec2-user]# cd /data
	[root@ip-10-144-143-144 data]# touch a1
	[root@ip-10-144-143-144 data]# ls
	a1  a2

	[root@ip-10-146-2-125 ec2-user]# cd /data
	[root@ip-10-146-2-125 data]# ls
	a1
	[root@ip-10-146-2-125 data]# touch a2
	[root@ip-10-146-2-125 data]# ls
	a1  a2
