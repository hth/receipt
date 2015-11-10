I use PKCS#12 (a .p12 file). To create it I do:

Export the private key from the Keychain and name it `aps_private-key.p12`.

Convert the key with the following command 

    openssl pkcs12 -nocerts -out aps_private-key.pem -in aps_private-key.p12
    
make sure to enter a PEM pass phrase of at least 4 characters.

Download the certificate for your app from https://developer.apple.com/account/ios/identifiers/bundle/bundleList.action. 
The downloaded file should be called something like `aps_development.cer`.

Convert the certificate with the following command 

    openssl x509 -in aps_development.cer -inform der -out aps_development.pem

Generate the credentials using 

    openssl pkcs12 -export -in aps_development.pem -out aps_dev_credentials.p12 -inkey aps_private-key.pem.

And I'm ready to use the credentials generated in step 5 (aps_dev_credentials.p12).

final InputStream certificate = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream("aps_dev_credentials.p12");
final char[] passwd = {'1','2','3','4'};
final ApnsService apnsService = com.notnoop.apns.APNS.newService()
        .withCert(certificate, new String(passwd))
        .withSandboxDestination().build();
apnsService.testConnection();


### Adding password to APNS Certificate

Things taken for granted in this post:

- You already have a Apple Developer ID, 
- You have already setup the App ID and enabled it for Push Notifications,
- You have created the development and production certificate for Push Notifications for your app,
- You have the private key in your keychain (it must be there since its needed for the certificate creation above).

All we need is 2 files:

- the private key .p12 file (let’s call it pkey.p12). This can be found in the Keys section of the OSX keychain. Right 
click on it, select export, enter the filename in .p12 file format and enter its password.
- the SSL certificate (let’s call this sslcert.cer). For this post, I’m using the development certificate and this can 
be either downloaded from the Developer Connection website (same page where you created it) or you can simply drag and drop it to Finder/Desktop from the My Certificates section in the keychain.

Having done the above, open Ternimal in a Mac OSX. A Linux distribution with openssl installed will do the job as well (100% compatible and tested on Fedora Core 18). If your Linux doesn’t have openssl, type

    sudo yum install openssl
for RedHat-type distributions or

    sudo apt-get install openssl
for Debian.


#### Step 1: The Certificate

At first we need to convert the sslcert.cer to a .pem format the APNS will understand. Just type in Terminal:

    $ openssl x509 -inform der -in sslcert.cer -out certificate.pem
and let’s call the output file certificate.pem.

#### Step 2: The Private Key

First create a default certificate using access keychain.
Save Certificates.p12 locally

Same must be done for the private key. This is a bit more complex as it involves a security pass phrase. Again on Terminal:

    $ openssl pkcs12 -nocerts -in pkey.p12 -out pkey.pem
You will be asked to enter the password protecting the pkey.p12 file and then enter a pass phrase that will protect the output pem file. We call the output pkey.pem and we’ll use it later on. Type the password, type the pass phrase, remember the pass phrase and let’s proceed…

#### Step 3: Merging the pem

Where we need to merge the two files into a single pem file. Extremely simple:

    $ cat certificate.pem pkey.pem > apns_cert.pem
The created apns_cert.pem file will have the same pass phrase entered in step 2 and is the file needed for communication with the APNS servers.

At this point, the only thing left is to test if the file is correct.

    http://stackoverflow.com/questions/9418661/how-to-create-p12-certificate-for-ios-distribution
    Read - .p12 files are used to publish app on the Apple App Store

#### Step 4: Testing

So far, the required pem file for the APNS communication is ready but we need to test it. The openssl command provides this as well. Since we used the development certificate on this post, we are going to test the sandbox APNS using this command:

    $ openssl s_client -connect gateway.sandbox.push.apple.com:2195 -cert apns_cert.pem -key apns_cert.pem
And type the pass phrase again. After that, if all is done correctly, the connection will open with a wall of text letting
 you know what is going on. Typing a couple of characters, it will disconnect which is normal. If there is an error in the
  file, openssl will give an error message and you’ll have to read the whole wall of text to find what went wrong.

An indication of a successful connection will look something like this:

    CONNECTED(00000003)
    depth=2 O = Entrust.net, OU = www.entrust.net/CPS_2048 incorp. by ref. (limits liab.), OU = (c) 1999 Entrust.net Limited, CN = Entrust.net Certification Authority (2048)
    verify return:1
    depth=1 C = US, O = "Entrust, Inc.", OU = www.entrust.net/rpa is incorporated by reference, OU = "(c) 2009 Entrust, Inc.", CN = Entrust Certification Authority - L1C
    verify return:1
    depth=0 C = US, ST = California, L = Cupertino, O = Apple Inc., OU = iTMS Engineering, CN = gateway.sandbox.push.apple.com
    verify return:1
    ---
    Certificate chain
    0 s:/C=US/ST=California/L=Cupertino/O=Apple Inc./OU=iTMS Engineering/CN=gateway.sandbox.push.apple.com
    i:/C=US/O=Entrust, Inc./OU=www.entrust.net/rpa is incorporated by reference/OU=(c) 2009 Entrust, Inc./CN=Entrust Certification Authority - L1C
    1 s:/C=US/O=Entrust, Inc./OU=www.entrust.net/rpa is incorporated by reference/OU=(c) 2009 Entrust, Inc./CN=Entrust Certification Authority - L1C
    i:/O=Entrust.net/OU=www.entrust.net/CPS_2048 incorp. by ref. (limits liab.)/OU=(c) 1999 Entrust.net Limited/CN=Entrust.net Certification Authority (2048)
    ---
    Server certificate
    -----BEGIN CERTIFICATE-----
where the certificate data are printed and then:

    -----END CERTIFICATE-----
    subject=/C=US/ST=California/L=Cupertino/O=Apple Inc./OU=iTMS Engineering/CN=gateway.sandbox.push.apple.com
    issuer=/C=US/O=Entrust, Inc./OU=www.entrust.net/rpa is incorporated by reference/OU=(c) 2009 Entrust, Inc./CN=Entrust Certification Authority - L1C
    ---
    No client certificate CA names sent
    ---
    SSL handshake has read 4318 bytes and written 2172 bytes
    ---
    New, TLSv1/SSLv3, Cipher is AES256-SHA
    Server public key is 2048 bit
    Secure Renegotiation IS supported
    Compression: NONE
    Expansion: NONE
    SSL-Session:
    Protocol : TLSv1
    Cipher : AES256-SHA
    Session-ID: D85E15E39624323B4EBA268214077587711A2EEC4FB083C7F79435EC1A0E58EC
    Session-ID-ctx: 
    Master-Key: D433A396CE2FCF8C97C2E53B1C4F08BFAB738D343D5D4E69D2F6E42268A67EB490AFA554FA59FB4337F7FFA34AFC0A3A
    Key-Arg : None
    Krb5 Principal: None
    PSK identity: None
    PSK identity hint: None
    TLS session ticket:
    followed by the ticket data.

Finally, with the pem file created, you can use it in any application or system you have developed or used to manage Push Notifications. Copy it where it needs to be and start sending…

