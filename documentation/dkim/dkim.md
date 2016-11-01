### DKIM

Follow https://github.com/markenwerk/java-utils-mail-dkim

In order to use DKIM, it is necessary to create a RSA key pair and publish the public key in an appropriate DNS entry.

A RSA private pair with a key size of 1024 bits can be generated as a PEM encoded PKCS8 file like this:

    openssl genrsa -out dkim.pem 1024
While DKIM should be compatible with any reasonable key size, it might not be possible to publish arbitrary large public keys. See section 3.3.3. of the RFC for further information on key sizes.

Javas standard API only allows to import PKCS8 files in unencrypted PEM encoding. Therefore, it is either necessary to use a third party library like the Java version of The Legion of the Bouncy Castle or to convert the PEM encoded file into an unencrypted DER encoded file like this:

    openssl pkcs8 -topk8 -nocrypt -in dkim.pem -outform der -out dkim.der
The corresponding public key can be obtained from the private key like this:

    openssl rsa -in dkim.pem -pubout
This yields an output like this:

writing RSA key

    -----BEGIN PUBLIC KEY-----
    MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCf4lvVllV2eoDqxartI0bUiJXD
    v+TVhFoGcheKocQyLGrTi8BKamhoDt8yKiecpCm1rZ/nRyxSqIAJFMV3y/XslSVV
    2Sc48efPtrdViGUcGYNCC/KrqYNgCF7vRO2oAQ7ePPBohwcR1hzavGeY/AVxpEeI
    vixQNmunxkdaqHCLuQIDAQAB
    -----END PUBLIC KEY-----
The content of the DNS resource record consists of a set of keys and values, where a typical DNS resource record has values for following keys:

    v: The DKIM version, currently DKIM1.
    g: The DKIM granularity, used to restrict the allowed sender identities, usualy *.
    k: The key type, usualy rsa.
    p: The Base64 encoded public key, usualy a RSA public key.
    s: The allowed service types, usualy email or *.
    t: Some flags used by DKIM validators.

### For screens checkout the link below

https://support.smtp2go.com/hc/en-gb/articles/223086907-DKIM-Setup-for-GoDaddy

### Add key to domain

    Name: receiptapp._domainkey
    Value: v=DKIM1;g=*;k=rsa;p=MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCf4lvV
           llV2eoDqxartI0bUiJXDv+TVhFoGcheKocQyLGrTi8BKamhoDt8yKiecpCm1rZ/n
           RyxSqIAJFMV3y/XslSVV2Sc48efPtrdViGUcGYNCC/KrqYNgCF7vRO2oAQ7ePPBo
           hwcR1hzavGeY/AVxpEeIvixQNmunxkdaqHCLuQIDAQAB;s=email;t=s 
