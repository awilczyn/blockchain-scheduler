package blockchain.core;

import blockchain.core.genesis.GenesisBlock;
import blockchain.db.Context;
import blockchain.networking.MessageSender;
import blockchain.networking.ServerInfo;
import blockchain.scheduler.Schedule;
import blockchain.serialization.PublicKeyDeserizlizer;
import blockchain.util.ByteUtil;
import blockchain.util.Log;
import blockchain.util.SortByTimestamp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Created by andrzejwilczynski on 04/08/2018.
 */
public class Node implements Runnable
{
    private static int NUMBEROFNODES = 4;

    private Context context;

    private Wallet minerWallet;

    private Block genesisBlock;

    private Block currentBlock;

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<byte [], TransactionOutput> UTXOs = new HashMap<>();
    public static int difficulty = 5;
    public static float minimumTransaction = 0.1f;

    /** peer to peer data */

    private Thread miningThread;
    private boolean shouldMine;

    private Wallet testWallet;

    private int localPort;

    public static HashMap<ServerInfo, Date> serverStatus = new HashMap<ServerInfo, Date>();

    public static ConcurrentHashMap<BigInteger, Transaction> pool = new ConcurrentHashMap<BigInteger, Transaction>();

    public static ConcurrentHashMap<BigInteger, Transaction> transactionVerifiedPool = new ConcurrentHashMap<BigInteger, Transaction>();;

    public Node(Context context, Wallet wallet, Block genesisBlock, HashMap<ServerInfo, Date> serverStatus){
        this.context = context;
        this.minerWallet = wallet;
        this.genesisBlock = genesisBlock;
        this.testWallet = new Wallet();
        this.serverStatus = serverStatus;
    }

    public Node(Context localContext, Wallet localWallet, HashMap<ServerInfo, Date> serverStatus, int localPort)
    {
        this(localContext, localWallet, GenesisBlock.getInstance(localContext, localWallet).getBlock(), serverStatus);
        this.localPort = localPort;
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
        for (Map.Entry<String, Block> entry : context.blocks.entrySet()) {
            Block currentBlock = entry.getValue();
            for(int t=0; t <currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);
                //add outputs to Unspent list
                for(TransactionOutput o : currentTransaction.outputs) {
                    UTXOs.put(o.id , o);
                }
            }
            blockchain.add(currentBlock);
        }
        Collections.sort(blockchain, new SortByTimestamp());

        String BC = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(BC);

        while(shouldMine) {
            if (transactionVerifiedPool.size() >= Block.minimumNumberOfTransaction) {
                Block block1 = new Block(blockchain.get(blockchain.size() - 1).hash);
                for (Map.Entry<BigInteger, Transaction> entry : transactionVerifiedPool.entrySet())
                {
                    BigInteger key = entry.getKey();
                    Transaction trans = entry.getValue();
                    if (Block.maxNumberOfTransaction > block1.transactions.size())
                    {
                        block1.addTransaction(trans);
                        transactionVerifiedPool.remove(key);
                    }
                }
                addBlock(block1, minerWallet.getPublicKey());
                System.out.println("\nMiner scheduling factor: " + getSchedulingFactorForPublicKey(minerWallet.getPublicKey()));
                context.putBlock(block1);
                String blockJson = new GsonBuilder().setPrettyPrinting().create().toJson(block1);
                System.out.println("\nThe block: ");
                System.out.println(blockJson);
            }

//            Block block1 = new Block(genesisBlock.hash);
//            System.out.println("\nMiner wallet balance is: " + minerWallet.getBalance());
//            System.out.println("\nLocal wallet is Attempting to send funds (40) to WalletB...");
//
//            Transaction transaction1 = minerWallet.sendFunds(walletB.getPublicKey(), 40f);
//            block1.addTransaction(transaction1);
//            addBlock(block1);
//            //context.putBlock(block1);
//            System.out.println("\nMiner wallet balance is: " + minerWallet.getBalance());
//            System.out.println("WalletB's balance is: " + walletB.getBalance());
//
//            Block block2 = new Block(block1.hash);
//            System.out.println("\nMiner wallet attempting to send more funds (1000) than it has...");
//            block2.addTransaction(minerWallet.sendFunds(walletB.getPublicKey(), 1000f));
//            addBlock(block2);
//            System.out.println("\nMiner wallet balance is: " + minerWallet.getBalance());
//            System.out.println("WalletB's balance is: " + walletB.getBalance());
//
//            Block block3 = new Block(block2.hash);
//            System.out.println("\nWalletB is Attempting to send funds (20) to Miner wallet...");
//            block3.addTransaction(walletB.sendFunds( minerWallet.getPublicKey(), 20));
//            System.out.println("\nMiner wallet balance is: " + minerWallet.getBalance());
//            System.out.println("WalletB's balance is: " + walletB.getBalance());
//
//            String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//            System.out.println("\nThe block chain: ");
//            System.out.println(blockchainJson);
        }
    }

    public static void addBlock(Block newBlock, byte[]  publicKey)
    {
        newBlock.mineBlock(difficulty, publicKey);
        blockchain.add(newBlock);
    }

    public void broadcast(String message) {
        for (ServerInfo info: this.serverStatus.keySet()) {
            message = message+"|"+localPort;
            new Thread(new MessageSender(info, message)).start();
        }
    }

    public void addTransactionToPool(float value, Schedule schedule) throws IOException {
        Transaction transaction1 = minerWallet.sendDataToSchedule(testWallet.getPublicKey(), value, schedule);
        pool.put(ByteUtil.bytesToBigInteger(transaction1.getTransactionId()), transaction1);
        String transactionJson = new GsonBuilder().create().toJson(transaction1);
        System.out.println("Sending transaction to peers for accept... ");
        broadcast("tx|"+transactionJson);
    }

    public static double getMinimumNumberOfConfirmation()
    {
        return NUMBEROFNODES*0.50;
    }

    public static float getSchedulingFactorForPublicKey(byte[]  publicKey)
    {
        float total = 0;
        for (Map.Entry<byte [], TransactionOutput> item: Node.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) { //if output belongs to me ( if coins belong to me )
                Node.UTXOs.put(UTXO.id,UTXO); //add it to our list of unspent transactions.
                total += UTXO.schedulingFactor ;
            }
        }
        return total;
    }
}
