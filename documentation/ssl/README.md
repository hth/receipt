### Instead of this document, please follow SSL documentation insided nginx


Certificate could be created using openssl or keytool

1.	command for keytool

		Checkout the file name 'Keytool.png'

		To generate csr from keytool (last command in the image)
		keytool -genkey -keyalg RSA -alias tomcat -keystore domain_keystore.jks -keysize 2048

		/** set 'sigalg' to some valid algorithm
		keytool -certreq -sigalg sha1withrsa -alias tomcat -file domain_keystore.csr -keystore domain_keystore.jks

2.	command for openssl

		Checkout the file name 'OpenSSL.png'


#######Important########

In the file - Adding 3rd Party Info to Certificate -
1.	First copy the original domain_keystore.jks created from the above command to the folder where Network Solution
	files are saved/downloaded. Then execute the following commands to add the appropriate data to the existing
	domain_keystore.jks file.

	There should be only one password being asked in the beginning and the choice to enter new password means something is being done
	incorrectly. For comparison, check out the file with network solution support

	At the end, in support file with network solution it says 'Certificate reply was installed in keystore' instead of
	'Certificate was added to keystore'. Former being correct.

2.	Commands

		  keytool -import -trustcacerts -alias root -file AddTrustExternalCARoot.crt -keystore domain_keystore.jks
		  keytool -import -trustcacerts -alias INTER1 -file NetworkSolutionsAddTrustEVServerCA.crt -keystore domain_keystore.jks
		  keytool -import -trustcacerts -alias INTER2 -file NetworkSolutionsEVServerCA.crt -keystore domain_keystore.jks
		  keytool -import -trustcacerts -alias tomcat -file RECEIPTOFI.COM.crt -keystore domain_keystore.jks

3.	In server.xml point the 'keyStoreFile' attribute to the location of  'domain_keystore.jks' file and provide the password.

4. Don't forget to add the receiptofi.com to hosts file with ip address
example
    192.168.XXX.XXX	receiptofi.com

In server.xml there is no need to set the alias as 'tomcat'. Tomcat considered to be default.

<Connector port="8443"
               protocol="org.apache.coyote.http11.Http11NioProtocol"
               SSLEnabled="true"
               maxThreads="150"
               scheme="https"
               secure="true"
               clientAuth="false"
               sslProtocol="TLS"
               keystoreFile="/Location/receiptofi.com/domain_keystore.jks"
               keystorePass="ASK_FOR_PASSWORD"
               compressableMimeType ="text/html,text/xml,text/plain,text/css,text/javascript"/>
