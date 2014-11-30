S3 Setup
========

Log in your account. Create New Users with access key. Then [add policy] (https://awspolicygen.s3.amazonaws.com/policygen.html)
for the bucket.

Policy Example

    {
    	"Id": "Policy1417307362219",
    	"Statement": [
    		{
    			"Sid": "Stmt1417306156923",
    			"Action": "s3:*",
    			"Effect": "Allow",
    			"Resource": "arn:aws:s3:::chk.test/*",
    			"Principal": {
    				"AWS": [
    					"arn:aws:iam::764803654725:user/receiptofi-test"
    				]
    			}
    		},
    		{
    			"Sid": "Stmt1417307360773",
    			"Action": "s3:*",
    			"Effect": "Allow",
    			"Resource": "arn:aws:s3:::chk.test/*",
    			"Condition": {
    				"IpAddress": {
    					"aws:SourceIp": "67.148.60.37"
    				}
    			},
    			"Principal": {
    				"AWS": [
    					"*"
    				]
    			}
    		}
    	]
    }