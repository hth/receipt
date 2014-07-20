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

First create EC2 instance with 10 GB of storage; connect the instance on ssh

Create volumes; mount it http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ebs-using-volumes.html

	[ec2-user@ip-172-30-0-215 ~]$ lsblk
		NAME    MAJ:MIN RM SIZE RO TYPE MOUNTPOINT
		xvda    202:0    0   8G  0 disk
		└─xvda1 202:1    0   8G  0 part /
		xvdb    202:16   0  10G  0 disk

    [ec2-user@ip-172-30-0-215 ~]$ sudo file -s /dev/xvdb
    	/dev/xvdb: data

    [ec2-user@ip-172-30-0-215 ~]$ sudo mkfs -t ext4 /dev/xvdb
		mke2fs 1.42.8 (20-Jun-2013)
		Filesystem label=
		OS type: Linux
		Block size=4096 (log=2)
		Fragment size=4096 (log=2)
		Stride=0 blocks, Stripe width=0 blocks
		655360 inodes, 2621440 blocks
		131072 blocks (5.00%) reserved for the super user
		First data block=0
		Maximum filesystem blocks=2684354560
		80 block groups
		32768 blocks per group, 32768 fragments per group
		8192 inodes per group
		Superblock backups stored on blocks:
			32768, 98304, 163840, 229376, 294912, 819200, 884736, 1605632

		Allocating group tables: done
		Writing inode tables: done
		Creating journal (32768 blocks): done
		Writing superblocks and filesystem accounting information: done

	[ec2-user@ip-172-30-0-215 ~]$ sudo mkdir /data

	[ec2-user@ip-172-30-0-215 ~]$ sudo mount /dev/xvdb /data

Once mounted; created directory **/data/brick**

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

Add a truster peer pool storage.. don`t forget to add **'default VPC security group'** name **sg-f5942990**

	[root@ip-10-144-143-144 ec2-user]# gluster peer probe  ec2-54-254-58-214.ap-southeast-1.compute.amazonaws.com
	peer probe: success

Check the status of the peer..

	[root@ip-10-144-143-144 ec2-user]# gluster peer status
	Number of Peers: 1

	Hostname: ec2-54-254-58-214.ap-southeast-1.compute.amazonaws.com
	Port: 24007
	Uuid: 51c7c768-b046-46ac-a4ad-caa67c1b768d
	State: Peer in Cluster (Connected)

Create Replica set

	gluster volume create receiptofiFS replica 2 transport tcp 172.30.0.210:/data/brick 172.30.0.204:/data/brick

Create and start volume in both the servers..

	[root@ip-10-144-143-144 ec2-user]# gluster volume create receiptofiFS replica 2 transport tcp ec2-122-248-202-153.ap-southeast-1.compute.amazonaws.com:/data1 ec2-54-254-58-214.ap-southeast-1.compute.amazonaws.com:/data2
	volume create: receiptofiFS: success: please start the volume to access data

	[root@ip-10-144-143-144 ec2-user]# gluster volume start receiptofiFS
	volume start: receiptofiFS: success

Check status

	[root@ip-172-30-0-216 brick]# gluster volume status
    Status of volume: receiptofiFS
    Gluster process						Port	Online	Pid
    ------------------------------------------------------------------------------
    Brick 172.30.0.216:/data/brick		49152	Y	5381
    Brick 172.30.0.215:/data/brick		49152	Y	5345
    NFS Server on localhost				N/A		N	N/A
    Self-heal Daemon on localhost		N/A		Y	5396
    NFS Server on 172.30.0.215			N/A		N	N/A
    Self-heal Daemon on 172.30.0.215	N/A		Y	5360

    Task Status of Volume receiptofiFS
    ------------------------------------------------------------------------------
    There are no active volume tasks

Check Gluster volume..

	[root@ip-10-144-143-144 ec2-user]# gluster volume info

	Volume Name: receiptofiFS
	Type: Replicate
	Volume ID: e5a8a24d-bf47-4770-a2e8-f13993998a51
	Status: Started
	Number of Bricks: 1 x 2 = 2
	Transport-type: tcp
	Bricks:
	Brick1: ec2-122-248-202-153.ap-southeast-1.compute.amazonaws.com:/data1
	Brick2: ec2-54-254-58-214.ap-southeast-1.compute.amazonaws.com:/data2

	OR

	[root@ip-172-30-0-204 ec2-user]# gluster volume info

    Volume Name: receiptofiFS
    Type: Replicate
    Volume ID: 279ae7f1-5876-42ce-bc00-7a17d466f07a
    Status: Started
    Number of Bricks: 1 x 2 = 2
    Transport-type: tcp
    Bricks:
    Brick1: 172.30.0.210:/data/brick
    Brick2: 172.30.0.204:/data/brick


data1 and data2 will both be having same data in between them its just taken to show that its not necessary to take same mount name..

Follow -- http://www.howtoforge.com/distributed-replicated-storage-across-four-storage-nodes-with-glusterfs-3.2.x-on-centos-6.3

create a /data directory in both master and client box so it can have common mount point..


	[root@ip-10-144-143-144 ec2-user]# mkdir /data
	[root@ip-10-144-143-144 ec2-user]# mount -t glusterfs ec2-122-248-202-153.ap-southeast-1.compute.amazonaws.com:receiptofiFS /data
	[root@ip-10-144-143-144 ec2-user]# df -h
	Filesystem            Size  Used Avail Use% Mounted on
	/dev/xvda1            7.9G  983M  6.9G  13% /
	tmpfs                 298M     0  298M   0% /dev/shm
	ec2-122-248-202-153.ap-southeast-1.compute.amazonaws.com:receiptofiFS
	                      7.9G  983M  6.9G  13% /data


	[root@ip-10-146-2-125 ec2-user]# mkdir /data
	[root@ip-10-146-2-125 ec2-user]#  mount -t glusterfs ec2-122-248-202-153.ap-southeast-1.compute.amazonaws.com:receiptofiFS /data
	[root@ip-10-146-2-125 ec2-user]# df -h
	Filesystem            Size  Used Avail Use% Mounted on
	/dev/xvda1            7.9G  981M  6.9G  13% /
	tmpfs                 298M     0  298M   0% /dev/shm
	ec2-122-248-202-153.ap-southeast-1.compute.amazonaws.com:receiptofiFS
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
