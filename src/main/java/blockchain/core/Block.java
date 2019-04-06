package blockchain.core;

import blockchain.util.ByteUtil;
import blockchain.util.StringUtil;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    public static int minimumNumberOfInstruction = 100000;
    public static int maxNumberOfTransaction = 2;

    public static int numberOfDayLimit = 30;

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
        float TF = Node.getTrustFactor(publicKey, numberOfDayLimit, true, this.getCurrentBlockTransaction());
        float W = Node.getTrustFactor(publicKey, numberOfDayLimit, false, this.getCurrentBlockTransaction());
        double Pt = 0;
        if (W > 0 && TF > 0) {
            Pt = TF/W;
        }
        if (Pt >= 0.1) {
            difficulty = 10 - Math.min((int)Math.round(Pt*10), 9);
        } else {
            difficulty = 10;
        }
        String target = StringUtil.getDifficultyString(difficulty);
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    private ArrayList<BigInteger> getCurrentBlockTransaction()
    {
        ArrayList<BigInteger> currentTransactionIds = new ArrayList<BigInteger>();
        for (Transaction i : transactions) {
            // accessing each element of array
            BigInteger transactionId = ByteUtil.bytesToBigInteger(i.getTransactionId());
            currentTransactionIds.add(transactionId);
        }
        return currentTransactionIds;
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
            if((transaction.processTransaction(timeStamp) != true)) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction successfully added to the block.");
        return true;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
