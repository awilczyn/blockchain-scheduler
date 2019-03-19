package blockchain.core;

import blockchain.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by andrzejwilczynski on 24/07/2018.
 */
public class Block implements Serializable
{
    public String hash;

    public String previousHash;

    private long timeStamp;

    private int nonce;

    public String merkleRoot;

    public ArrayList<Transaction> transactions = new ArrayList<Transaction>();

    public static int minimumNumberOfTransaction = 2;
    public static int maxNumberOfTransaction = 2;

    /**
     *
     * @param data
     * @param previousHash
     */
    public Block(String previousHash)
    {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    /**
     *
     * @param genesisHash
     */
    public void genesisBlock(String genesisHash)
    {
        this.hash = genesisHash;
    }

    /**
     *
     * @return String
     */
    public String calculateHash()
    {
        String calculatedhash = StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
        return calculatedhash;
    }

    /**
     *
     * @param difficulty
     */
    public void mineBlock(int difficulty, byte[]  publicKey)
    {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = StringUtil.getDificultyString(difficulty);
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    /**
     *
     * @param transaction
     * @return
     */
    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if(transaction == null) return false;
        if((previousHash != "0")) {
            if((transaction.processTransaction() != true)) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
