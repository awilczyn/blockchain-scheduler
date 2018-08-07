package blockchain.core;

import blockchain.Start;
import blockchain.core.genesis.GenesisBlock;
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

    private Block genesisBlock;

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
    public static int difficulty = 5;
    public static float minimumTransaction = 0.1f;
    public static Wallet walletB;

    /** peer to peer data */

    private Thread miningThread;

    private Peer2Peer p2p;
    private int serverPort = 8889;
    private boolean shouldMine;

    public Node(Context context, Wallet wallet, Block genesisBlock){
        this.context = context;
        this.minerWallet = wallet;
        this.genesisBlock = genesisBlock;
    }

    public Node()
    {
        this(Start.localContext, Start.localWallet, GenesisBlock.getInstance().getBlock());
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
            walletB = new Wallet();
            addBlock(genesisBlock);
            UTXOs.put(GenesisBlock.genesisTransaction.outputs.get(0).id, GenesisBlock.genesisTransaction.outputs.get(0));

            Block block1 = new Block(genesisBlock.hash);
            System.out.println("\nMiner wallet balance is: " + minerWallet.getBalance());
            System.out.println("\nLocal wallet is Attempting to send funds (40) to WalletB...");
            block1.addTransaction(minerWallet.sendFunds(walletB.publicKey, 40f));
            addBlock(block1);
            System.out.println("\nMiner wallet balance is: " + minerWallet.getBalance());
            System.out.println("WalletB's balance is: " + walletB.getBalance());

            Block block2 = new Block(block1.hash);
            System.out.println("\nMiner wallet attempting to send more funds (1000) than it has...");
            block2.addTransaction(minerWallet.sendFunds(walletB.publicKey, 1000f));
            addBlock(block2);
            System.out.println("\nMiner wallet balance is: " + minerWallet.getBalance());
            System.out.println("WalletB's balance is: " + walletB.getBalance());

            Block block3 = new Block(block2.hash);
            System.out.println("\nWalletB is Attempting to send funds (20) to Miner wallet...");
            block3.addTransaction(walletB.sendFunds( minerWallet.publicKey, 20));
            System.out.println("\nMiner wallet balance is: " + minerWallet.getBalance());
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
