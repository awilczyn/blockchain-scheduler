package blockchain.util;

import java.security.MessageDigest;

/**
 * Created by andrzejwilczynski on 11/08/2018.
 */
public class HashUtil
{
    public static byte[] applySha256(byte[] input)
    {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input);
            return hash;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
