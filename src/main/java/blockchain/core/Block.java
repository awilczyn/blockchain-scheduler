package blockchain.core;

import blockchain.core.genesis.GenesisBlock;
import blockchain.serialization.Serializer;
import blockchain.util.ByteUtil;
import blockchain.util.HashUtil;
import blockchain.util.StringUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static java.lang.Thread.sleep;

/**
 * Created by andrzejwilczynski on 24/07/2018.
 */
public class Block
{
    public byte[] hash;

    public byte[] previousHash;

    private byte[] timeStamp;

    private byte[] nonce;

    public byte[] merkleRoot;

    public ArrayList<Transaction> transactions = new ArrayList<Transaction>();

    /**
     *
     * @param previousHash
     */
    public Block(byte[] previousHash)
    {
        this.previousHash = previousHash;
        this.timeStamp = ByteUtil.getNowTimeStamp();
        this.nonce = new byte[] {0};
        this.merkleRoot = new byte[] {0};
        this.hash = calculateHash();
    }

    public byte[] calculateHash()
    {
        byte[] hashData = Serializer.createParcel(
                new Object[]{
                        previousHash,
                        timeStamp,
                        nonce,
                        merkleRoot
                });
        return HashUtil.applySha256(hashData);
    }

    /**
     *
     * @param difficulty
     */
    public void mineBlock(int difficulty)
    {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
      //  String target = StringUtil.getDificultyString(difficulty);
        String target = new String(new char[difficulty]).replace('\0', '0');

        //hash = calculateHash();

//        while(!hash.substring( 0, difficulty).equals(target)) {
//            nonce ++;
//            hash = calculateHash();
//        }
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

        if(!Arrays.equals(previousHash, GenesisBlock.getHash())) {
            if((transaction.processTransaction() != true)) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }
}
