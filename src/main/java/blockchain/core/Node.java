package blockchain.core;

import blockchain.core.genesis.GenesisBlock;
import blockchain.db.Context;
import blockchain.networking.MessageSender;
import blockchain.networking.ServerInfo;
import blockchain.scheduler.Schedule;
import blockchain.scheduler.utils.Constants;
import blockchain.serialization.PublicKeyDeserizlizer;
import blockchain.util.ByteUtil;
import blockchain.util.Log;
import blockchain.util.SortByTimestamp;
import blockchain.util.Wilcoxon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Created by andrzejwilczynski on 04/08/2018.
 */
public class Node implements Runnable
{
    private static int NUMBEROFNODES = 16;

    private Context context;

    private Wallet minerWallet;

    private Block genesisBlock;

    private Block currentBlock;

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<byte [], TransactionOutput> UTXOs = new HashMap<>();
    public static Validators validators = new Validators();
    public static int difficulty = 5;
    public static float minimumTransaction = 0.1f;

    public static double totalNumberOfTransaction = 0;
    public static ArrayList<Double> makespan = new ArrayList<Double>();
    public static ArrayList<Double> flowtime = new ArrayList<Double>();
    public static ArrayList<Double> economicCost = new ArrayList<Double>();
    public static ArrayList<Double> resourceUtilization = new ArrayList<Double>();
    public static ArrayList<Double> pFailure = new ArrayList<Double>();
    public static ArrayList<Double> pFake = new ArrayList<Double>();
    public static ArrayList<Double> pHacking = new ArrayList<Double>();
    public static ArrayList<Double> securityLevel = new ArrayList<Double>();

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

    private float getNumberOfCurrentInstructionsInBlock(Block block)
    {
        float numberOfTransactions = 0;
        for(int t=0; t <block.transactions.size(); t++) {
            Transaction trans = block.transactions.get(t);
            numberOfTransactions += trans.getSumOfInstructionsInBlockchain();
        }
        return numberOfTransactions;
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
                    double TF = Node.getTrustFactor(minerWallet.getPublicKey(), Validators.numberOfDayLimit, true);
                    double BC = Node.getTrustFactor(minerWallet.getPublicKey(), Validators.numberOfDayLimit, false);
                    System.out.println("Number of workload in BC: "+BC);
                    if (TF >= (BC*1/2)) {
                        trans.schedule.setPhacking(0.5);
                    } else {
                        trans.schedule.setPhacking(TF/BC);
                    }
                    trans.schedule.calculateSecurityLevel();
                    if (trans.schedule.getSecurityLevel() >= Constants.SECURITY_LEVEL) {
                        block1.addTransaction(trans);
                    }
                    transactionVerifiedPool.remove(key);
                }
                if (getNumberOfCurrentInstructionsInBlock(block1) >= Block.minimumNumberOfInstruction) {
                    blockIsReady = true;
                }
            }
            long startTime = System.nanoTime();
            if (Arrays.equals(validators.getLeader(), minerWallet.getPublicKey()) && blockIsReady) {
                System.out.println("\nValidator scheduling factor: " + getSchedulingFactorForPublicKey(minerWallet.getPublicKey()));
                addBlock(block1, minerWallet.getPublicKey());
                final long duration = System.nanoTime() - startTime;
                System.out.println("Time of mining " + duration + " [NS].");
                context.putBlock(block1);
                for (int t = 0; t < block1.transactions.size(); t++) {
                    Transaction currentTransaction = block1.transactions.get(t);
                    makespan.add(currentTransaction.schedule.makespan);
                    flowtime.add(currentTransaction.schedule.flowtime);
                    economicCost.add(currentTransaction.schedule.economicCost);
                    resourceUtilization.add(currentTransaction.schedule.resourceUtilization);
                    pFailure.add(currentTransaction.schedule.getPfailure());
                    pFake.add(currentTransaction.schedule.getPfake());
                    pHacking.add(currentTransaction.schedule.getPhacking());
                    securityLevel.add(currentTransaction.schedule.securityLevel);
                }
//                String blockJson = new GsonBuilder().setPrettyPrinting().create().toJson(block1);
//                System.out.println("\nThe block: ");
//                System.out.println(blockJson);
                System.out.println("Number of transactions in block: " + block1.transactions.size());
                System.out.println("Number of criteria: "+makespan.size());
            }
            if (makespan.size() >= Constants.NO_OF_ATTEMPTS) {
                double[] makespanArray = new double[makespan.size()];
                for (int i = 0; i < makespan.size(); i++) {
                    makespanArray[i] = makespan.get(i).doubleValue();
                }
                System.out.println("Makespan wilcoxon:");
                Wilcoxon.dataForWilcoxon(makespanArray);
                double[] flowtimeArray = new double[flowtime.size()];
                for (int i = 0; i < flowtime.size(); i++) {
                    flowtimeArray[i] = flowtime.get(i).doubleValue();
                }
                double[] economicCostArray = new double[economicCost.size()];
                for (int i = 0; i < economicCost.size(); i++) {
                    economicCostArray[i] = economicCost.get(i).doubleValue();
                }
                double[] resourceUtilizationArray = new double[resourceUtilization.size()];
                for (int i = 0; i < resourceUtilization.size(); i++) {
                    resourceUtilizationArray[i] = resourceUtilization.get(i).doubleValue();
                }
                double[] pFailureArray = new double[pFailure.size()];
                for (int i = 0; i < pFailure.size(); i++) {
                    pFailureArray[i] = pFailure.get(i).doubleValue();
                }
                double[] pFakeArray = new double[pFake.size()];
                for (int i = 0; i < pFake.size(); i++) {
                    pFakeArray[i] = pFake.get(i).doubleValue();
                }
                double[] pHackingArray = new double[pHacking.size()];
                for (int i = 0; i < pHacking.size(); i++) {
                    pHackingArray[i] = pHacking.get(i).doubleValue();
                }
                double[] securityLevelArray = new double[securityLevel.size()];
                for (int i = 0; i < securityLevel.size(); i++) {
                    securityLevelArray[i] = securityLevel.get(i).doubleValue();
                }
                System.out.println("Security level wilcoxon:");
                Wilcoxon.dataForWilcoxonDecimal(securityLevelArray);
                DescriptiveStatistics daMakespan = new DescriptiveStatistics(makespanArray);
                Median medianMakespan = new Median();
                DescriptiveStatistics daFlowtime = new DescriptiveStatistics(flowtimeArray);
                Median medianFlowtime = new Median();
                DescriptiveStatistics daEconomicCost = new DescriptiveStatistics(economicCostArray);
                Median medianEconomicCost = new Median();
                DescriptiveStatistics daResourceUtilization = new DescriptiveStatistics(resourceUtilizationArray);
                Median medianResourceUtilization = new Median();
                DescriptiveStatistics daSecurityLevel = new DescriptiveStatistics(securityLevelArray);
                Median medianSecurityLevel = new Median();
                DescriptiveStatistics daPfailure = new DescriptiveStatistics(pFailureArray);
                Median medianPfailure = new Median();
                DescriptiveStatistics daPfake = new DescriptiveStatistics(pFakeArray);
                Median medianPfake = new Median();
                DescriptiveStatistics daPhacking = new DescriptiveStatistics(pHackingArray);
                Median medianPHacking = new Median();
                DecimalFormat df = new DecimalFormat("#####0.000");
                DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
                dfs.setDecimalSeparator(',');
                df.setDecimalFormatSymbols(dfs);
                System.out.println("Criterion Min, Q1, Median, Quartile 3, Max");
                System.out.println("makespan: " +
                        df.format(daMakespan.getMin()) + ";" +
                        df.format(daMakespan.getPercentile(25)) + ";" +
                        df.format(medianMakespan.evaluate(makespanArray)) + ";" +
                        df.format(daMakespan.getPercentile(75)) + ";" +
                        df.format(daMakespan.getMax()));
                System.out.println("flowtime: " +
                        df.format(daFlowtime.getMin()) + ";" +
                        df.format(daFlowtime.getPercentile(25)) + ";" +
                        df.format(medianFlowtime.evaluate(flowtimeArray)) + ";" +
                        df.format(daFlowtime.getPercentile(75)) + ";" +
                        df.format(daFlowtime.getMax()));
                System.out.println("economic cost: " +
                        df.format(daEconomicCost.getMin()) + ";" +
                        df.format(daEconomicCost.getPercentile(25)) + ";" +
                        df.format(medianEconomicCost.evaluate(economicCostArray)) + ";" +
                        df.format(daEconomicCost.getPercentile(75)) + ";" +
                        df.format(daEconomicCost.getMax()));
                System.out.println("resource utilization: " +
                        df.format(daResourceUtilization.getMin()) + ";" +
                        df.format(daResourceUtilization.getPercentile(25)) + ";" +
                        df.format(medianResourceUtilization.evaluate(resourceUtilizationArray)) + ";" +
                        df.format(daResourceUtilization.getPercentile(75)) + ";" +
                        df.format(daResourceUtilization.getMax()));
                System.out.println("P failure: " +
                        df.format(daPfailure.getMin()) + ";" +
                        df.format(daPfailure.getPercentile(25)) + ";" +
                        df.format(medianPfailure.evaluate(pFailureArray)) + ";" +
                        df.format(daPfailure.getPercentile(75)) + ";" +
                        df.format(daPfailure.getMax()));
                System.out.println("P fake: " +
                        df.format(daPfake.getMin()) + ";" +
                        df.format(daPfake.getPercentile(25)) + ";" +
                        df.format(medianPfake.evaluate(pFakeArray)) + ";" +
                        df.format(daPfake.getPercentile(75)) + ";" +
                        df.format(daPfake.getMax()));
                System.out.println("P hacking: " +
                        df.format(daPhacking.getMin()) + ";" +
                        df.format(daPhacking.getPercentile(25)) + ";" +
                        df.format(medianPHacking.evaluate(pHackingArray)) + ";" +
                        df.format(daPhacking.getPercentile(75)) + ";" +
                        df.format(daPhacking.getMax()));
                System.out.println("security level: " +
                        df.format(daSecurityLevel.getMin()) + ";" +
                        df.format(daSecurityLevel.getPercentile(25)) + ";" +
                        df.format(medianSecurityLevel.evaluate(securityLevelArray)) + ";" +
                        df.format(daSecurityLevel.getPercentile(75)) + ";" +
                        df.format(daSecurityLevel.getMax()));
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
        if (schedule != null) {
            Transaction transaction1 = minerWallet.sendDataToSchedule(testWallet.getPublicKey(), value, schedule);
            pool.put(ByteUtil.bytesToBigInteger(transaction1.getTransactionId()), transaction1);
            Node.validators.update(transaction1);
            String transactionJson = new GsonBuilder().create().toJson(transaction1);
            System.out.println("Sending transaction to peers for accept... ");
            broadcast("tx|"+transactionJson);
        }
    }

    public static double getMinimumNumberOfConfirmation()
    {
        return NUMBEROFNODES*0.5;
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
                    total += UTXO.schedulingFactor ;
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
