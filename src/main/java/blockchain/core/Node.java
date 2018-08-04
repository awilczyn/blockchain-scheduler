package blockchain.core;

import blockchain.Start;
import blockchain.db.Context;
import blockchain.networking.Peer2Peer;
import blockchain.util.Log;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.logging.Level;

/**
 * Created by andrzejwilczynski on 04/08/2018.
 */
public class Node implements Runnable
{
    private Context context;

    private Wallet minerWallet;

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
    public static int difficulty = 5;
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;

    public static Transaction genesisTransaction;

    /** peer to peer data */

    private Thread miningThread;

    private Peer2Peer p2p;
    private int serverPort = 8889;
    private boolean shouldMine;

    public Node(Context context, Wallet wallet){
        this.context = context;
        this.minerWallet = wallet;
    }

    public Node(){
        this(Start.localContext, Start.localWallet);
    }

    public void start()
    {
        if(miningThread == null){
            miningThread = new Thread(this);
        }

        if(p2p == null) {
            p2p = new Peer2Peer(serverPort);
        }

        if(shouldMine){
            Log.log(Level.INFO, "Node already running");
            return;
        }

        p2p.start();
        shouldMine = true;
        miningThread.start();
    }

    public void run()
    {
        mine();
    }

    private void mine()
    {
        while(shouldMine) {
            walletA = new Wallet();
            walletB = new Wallet();
            Wallet coinbase = new Wallet();

            genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
            genesisTransaction.generateSignature(coinbase.privateKey);
            genesisTransaction.transactionId = "0";
            genesisTransaction.outputs.add(
                    new TransactionOutput(
                            genesisTransaction.recipient,
                            genesisTransaction.value,
                            genesisTransaction.transactionId
                    )
            );

            UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

            System.out.println("Creating and Mining Genesis block... ");
            Block genesis = new Block("0");
            genesis.addTransaction(genesisTransaction);
            addBlock(genesis);

            Block block1 = new Block(genesis.hash);
            System.out.println("\nWalletA's balance is: " + walletA.getBalance());
            System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
            block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
            addBlock(block1);
            System.out.println("\nWalletA's balance is: " + walletA.getBalance());
            System.out.println("WalletB's balance is: " + walletB.getBalance());

            Block block2 = new Block(block1.hash);
            System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
            block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
            addBlock(block2);
            System.out.println("\nWalletA's balance is: " + walletA.getBalance());
            System.out.println("WalletB's balance is: " + walletB.getBalance());

            Block block3 = new Block(block2.hash);
            System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
            block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
            System.out.println("\nWalletA's balance is: " + walletA.getBalance());
            System.out.println("WalletB's balance is: " + walletB.getBalance());

            String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
            System.out.println("\nThe block chain: ");
            System.out.println(blockchainJson);
        }
    }

    public static void addBlock(Block newBlock)
    {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}
