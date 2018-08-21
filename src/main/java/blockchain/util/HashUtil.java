package blockchain.util;

import java.security.MessageDigest;
import java.util.logging.Level;

/**
 * Created by andrzejwilczynski on 21/08/2018.
 */
public class HashUtil
{
    /* Shorthand: applies Sha3 algorithm to an input and returns the "hashed" bytes */
    public static byte[] applyKeccak(byte[] input){
        return hashWith(input,"KECCAK-256");
    }

    /* apply a selected hash instance to a string */
    public static byte[] hashWith(byte[] input, String instanceName){
        try{
            MessageDigest digest = MessageDigest.getInstance(instanceName, "BC");
            byte[] hash = digest.digest(input);
            return hash;
        }catch(Exception e){
            Log.log(Level.WARNING, e.getMessage());
            return null;
        }
    }

    public static byte[] applySha256(byte[] input)
    {
        return hashWith(input,"SHA-256");
    }

}
