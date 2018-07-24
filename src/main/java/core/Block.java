package core;

import util.StringUtil;
import java.util.Date;

/**
 * Created by andrzejwilczynski on 24/07/2018.
 */
public class Block
{
    public String hash;

    public String previousHash;

    private String data;

    private long timeStamp;

    private int nonce;

    /**
     *
     * @param data
     * @param previousHash
     */
    public Block(String data, String previousHash)
    {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    /**
     *
     * @return String
     */
    public String calculateHash()
    {
        String calculatedhash = StringUtil.applySha256(
                previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data
        );
        return calculatedhash;
    }

    /**
     *
     * @param difficulty
     */
    public void mineBlock(int difficulty)
    {
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }
}
