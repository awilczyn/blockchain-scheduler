package blockchain.core;

import blockchain.core.genesis.GenesisBlock;
import blockchain.db.Context;
import blockchain.networking.MessageSender;
import blockchain.networking.ServerInfo;
import blockchain.util.Log;

import java.util.*;
import java.util.logging.Level;

/**
 * Created by andrzejwilczynski on 04/08/2018.
 */
public class Node implements Runnable
{
    private Context context;

    private Wallet minerWallet;

    private Block genesisBlock;

    private Block currentBlock;

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
    public static int difficulty = 5;
    public static float minimumTransaction = 0.1f;
    public static Wallet walletB;

    /** peer to peer data */

    private Thread miningThread;
    private boolean shouldMine;

    HashMap<ServerInfo, Date> serverStatus = new HashMap<ServerInfo, Date>();

    public Node(Context context, Wallet wallet, Block genesisBlock){
        this.context = context;
        this.minerWallet = wallet;
        this.genesisBlock = genesisBlock;
    }

    public Node(Context localContext, Wallet localWallet)
    {
        this(localContext, localWallet, GenesisBlock.getInstance(localContext, localWallet).getBlock());
    }

    public void start()
    {
        if(miningThread == null){
            miningThread = new Thread(this);
        }

        if(shouldMine){
            Log.log(Level.INFO, "Node already running");
            return;
        }

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
            blockchain.add(genesisBlock);
            UTXOs.put(genesisBlock.transactions.get(0).outputs.get(0).id, genesisBlock.transactions.get(0).outputs.get(0));

//            Block block1 = new Block(genesisBlock.hash);
//            System.out.println("\nMiner wallet balance is: " + minerWallet.getBalance());
//            System.out.println("\nLocal wallet is Attempting to send funds (40) to WalletB...");
//            block1.addTransaction(minerWallet.sendFunds(walletB.publicKey, 40f));
//            addBlock(block1);
//            //context.putBlock(block1);
//            System.out.println("\nMiner wallet balance is: " + minerWallet.getBalance());
//            System.out.println("WalletB's balance is: " + walletB.getBalance());
//
//            Block block2 = new Block(block1.hash);
//            System.out.println("\nMiner wallet attempting to send more funds (1000) than it has...");
//            block2.addTransaction(minerWallet.sendFunds(walletB.publicKey, 1000f));
//            addBlock(block2);
//            System.out.println("\nMiner wallet balance is: " + minerWallet.getBalance());
//            System.out.println("WalletB's balance is: " + walletB.getBalance());
//
//            Block block3 = new Block(block2.hash);
//            System.out.println("\nWalletB is Attempting to send funds (20) to Miner wallet...");
//            block3.addTransaction(walletB.sendFunds( minerWallet.publicKey, 20));
//            System.out.println("\nMiner wallet balance is: " + minerWallet.getBalance());
//            System.out.println("WalletB's balance is: " + walletB.getBalance());
//
//            String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//            System.out.println("\nThe block chain: ");
//            System.out.println(blockchainJson);
        }
    }

    public static void addBlock(Block newBlock)
    {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

    public void broadcast(String message) {
        ArrayList<Thread> threadArrayList = new ArrayList<Thread>();
        for (ServerInfo info: this.serverStatus.keySet()) {
            Thread thread = new Thread(new MessageSender(info, message));
            thread.start();
            threadArrayList.add(thread);
        }
    }

    public void multicast(ArrayList<ServerInfo> toPeers, String message) {
        ArrayList<Thread> threadArrayList = new ArrayList<Thread>();
        for (int i = 0; i < toPeers.size(); i++) {
            Thread thread = new Thread(new MessageSender(toPeers.get(i), message));
            thread.start();
            threadArrayList.add(thread);
        }
    }
}