### Adding password to APNS Certificate

I use PKCS#12 (a .p12 file). To create it I do:

    http://stackoverflow.com/questions/20077626/whats-the-correct-format-for-java-apns-certificate

Export the private key from the Keychain and name it `aps_private-key.p12` from the certificate.

Convert the key with the following command 

    openssl pkcs12 -nocerts -out aps_private-key.pem -in aps_private-key.p12
    
make sure to enter a PEM pass phrase of at least 4 characters.

Download the certificate for your app from https://developer.apple.com/account/ios/identifiers/bundle/bundleList.action. 
The downloaded file should be called something like `aps_development.cer`.

Convert the certificate with the following command 

    openssl x509 -in aps_development.cer -inform der -out aps_development.pem
    openssl x509 -in aps_production.cer  -inform der -out aps_production.pem

Generate the credentials using 

    openssl pkcs12 -export -in aps_development.pem -out aps_dev_credentials.p12  -inkey aps_private-key.pem
    openssl pkcs12 -export -in aps_production.pem  -out aps_prod_credentials.p12 -inkey aps_private-key.pem
