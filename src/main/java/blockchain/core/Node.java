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
import java.sql.Timestamp;
import java.time.LocalDate;
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
    public static Validators validators = new Validators();
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


    private float getNumberOfCurrentInstructions()
    {
        float numberOfTransactions = 0;
        for (Map.Entry<BigInteger, Transaction> entry : transactionVerifiedPool.entrySet())
        {
            BigInteger key = entry.getKey();
            Transaction trans = entry.getValue();
            numberOfTransactions += trans.getSumOfInstructionsInBlockchain();
        }
        return numberOfTransactions;
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

        System.out.println("\nMiner scheduling factor: " + getSchedulingFactorForPublicKey(minerWallet.getPublicKey()));
//        String BC = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//        System.out.println(BC);

        while(shouldMine) {
            boolean blockIsReady = false;
            Block block1 = null;
            if (getNumberOfCurrentInstructions() >= Block.minimumNumberOfInstruction) {
                block1 = new Block(blockchain.get(blockchain.size() - 1).hash);
                System.out.println("Number of operations or instructions needed to execute the tasks within the created block: "+getNumberOfCurrentInstructions());
                for (Map.Entry<BigInteger, Transaction> entry : transactionVerifiedPool.entrySet())
                {
                    BigInteger key = entry.getKey();
                    Transaction trans = entry.getValue();
                    block1.addTransaction(trans);
                    transactionVerifiedPool.remove(key);
                }
                blockIsReady = true;
            }
            if (Arrays.equals(validators.getLeader(), minerWallet.getPublicKey()) && blockIsReady) {
                System.out.println("\nMiner scheduling factor: " + getSchedulingFactorForPublicKey(minerWallet.getPublicKey()));
                addBlock(block1, minerWallet.getPublicKey());
                context.putBlock(block1);
                String blockJson = new GsonBuilder().setPrettyPrinting().create().toJson(block1);
                System.out.println("\nThe block: ");
                System.out.println(blockJson);
            }
        }
    }

    public static void addBlock(Block newBlock, byte[]  publicKey)
    {
        newBlock.mineBlock();
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
        Node.validators.update(transaction1);
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

    public static float getTrustFactor(byte[]  publicKey, int time, boolean forMe)
    {
        float total = 0;
        float totalMe = 0;
        Timestamp timestamp = new Timestamp(new Date().getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        cal.add(Calendar.DAY_OF_MONTH, - time);
        timestamp = new Timestamp(cal.getTime().getTime());
        for (Map.Entry<byte [], TransactionOutput> item: Node.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            Timestamp transactionTimestamp = new Timestamp(UTXO.timestamp);
            if(transactionTimestamp.after(timestamp)){
                if(UTXO.isMine(publicKey)) {
                    totalMe += UTXO.schedulingFactor ;
                } else {
                    total += UTXO.schedulingFactor ;
                }
            }
        }
        if (forMe) {
            return totalMe;
        } else {
            return total;
        }
    }
}
