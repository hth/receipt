/**
 *
 */
package com.receiptofi.utils;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * @author hitender
 * @since Dec 22, 2012 11:52:04 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class HashText {
    private static final Logger LOG = LoggerFactory.getLogger(HashText.class);

    // Define the BCrypt workload to use when generating password hashes. 10-31 is a valid value.
    private static final int WORKLOAD = 15;

    private static MessageDigest md1;
    private static MessageDigest md5;

    static {
        try {
            md1 = MessageDigest.getInstance("SHA-1");
            md5 = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException exce) {
            LOG.error("No hashing algorithm found={}", exce);
        }
    }

    private HashText() {
        // Utility classes should always be final and have an private constructor
    }

    public static String hashCodeSHA1(String text) {
        return hashCode(text, md1);
    }

    public static String hashCodeSHA512(String text) {
        return hashCode(text, md5);
    }

    public static String computeBCrypt(String text) {
        return BCrypt.hashpw(text, BCrypt.gensalt(WORKLOAD));
    }

    public static boolean checkPassword(String passwordPlainText, String storedHash) {
        if (null == storedHash || !storedHash.startsWith("$2a$")) {
            throw new IllegalArgumentException("Invalid hash provided for comparison");
        }
        return BCrypt.checkpw(passwordPlainText, storedHash);
    }

    private static String hashCode(String text, MessageDigest md) {
        if (null == md) {
            LOG.info("Un-Initialized MessageDigest");
            return null;
        } else {
            md.update(text.getBytes());

            byte byteData[] = md.digest();

            // convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString(aByteData & 0xff + 0x100, 16).substring(1));
            }

            // convert the byte to hex format method 2
            StringBuilder hexString = new StringBuilder();
            for (byte aByteData : byteData) {
                String hex = Integer.toHexString(0xff & aByteData);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
    }

    /**
     * This condition is used through Ajax call to validated if a receipt of similar value exist in db.
     *
     * @param receiptUserId
     * @param date
     * @param total
     * @return
     */
    public static String calculateChecksumForNotDeleted(String receiptUserId, Date date, Double total) {
        return calculateChecksum(receiptUserId, date, total, false);
    }

    /**
     * @param receiptUserId
     * @param date
     * @param total
     * @param deleted
     * @return
     */
    public static String calculateChecksum(String receiptUserId, Date date, Double total, boolean deleted) {
        //This is considered to be thread safe
        HashFunction hf = Hashing.md5();
        HashCode hc = hf.newHasher()
                .putString(receiptUserId, Charsets.UTF_8)
                .putString(date.toString(), Charsets.UTF_8)
                .putDouble(total)
                .putBoolean(deleted)
                .hash();

        return hc.toString();
    }
}
