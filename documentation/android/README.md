### Generate Keystore to sign APK

    keytool -genkey -v -keystore android-receiptofi.keystore -alias receiptofi.com -keyalg RSA -keysize 2048 -validity 90
    
    Enter keystore password:  
    Re-enter new password: 
    What is your first and last name?
      [Unknown]:  Receiptofi Inc
    What is the name of your organizational unit?
      [Unknown]:  receiptofi.com
    What is the name of your organization?
      [Unknown]:  Receiptofi Inc
    What is the name of your City or Locality?
      [Unknown]:  Sunnyvale
    What is the name of your State or Province?
      [Unknown]:  California
    What is the two-letter country code for this unit?
      [Unknown]:  US
    Is CN=Receiptofi Inc, OU=receiptofi.com, O=Receiptofi Inc, L=Sunnyvale, ST=California, C=US correct?
      [no]:  yes
    
    Generating 2,048 bit RSA key pair and self-signed certificate (SHA256withRSA) with a validity of 90 days
    	for: CN=Receiptofi Inc, OU=receiptofi.com, O=Receiptofi Inc, L=Sunnyvale, ST=California, C=US
    Enter key password for <receiptofi.com>
    	(RETURN if same as keystore password):  
    [Storing android-receiptofi.keystore]
