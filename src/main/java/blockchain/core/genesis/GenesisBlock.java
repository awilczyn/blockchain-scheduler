package blockchain.core.genesis;

import blockchain.Node1;
import blockchain.core.*;
import blockchain.db.Context;
import blockchain.scheduler.*;
import blockchain.scheduler.utils.GenerateSimulationData;
import blockchain.util.ByteUtil;
import blockchain.util.StringUtil;
import blockchain.util.ecdsa.ECKey;
import com.google.gson.GsonBuilder;
import org.bouncycastle.util.encoders.Hex;

import java.util.ArrayList;

/**
 * Created by andrzejwilczynski on 07/08/2018.
 */
public class GenesisBlock
{
    private static GenesisBlock instance;
    private Block block;
    private Context context;

    public static Transaction sjfTransaction;
    public static Transaction rrTransaction;
    public static Transaction psoTransaction;
    public static Transaction fcfsTransaction;

    private Wallet localWallet;

    private GenesisBlock(Context context, Wallet localWallet)
    {
        this.context = context;
        this.localWallet = localWallet;
        this.createGenesisBlock();
    }

    public void createGenesisBlock()
    {
        // get first block from database
        System.out.println("Searching genesis block in database... ");
        block = context.getBlock(this.getGenesisHash());


        if (block == null) {
            block = new Block( "0");

            ECKey keyPair = ECKey.fromPrivate(Hex.decode(Node1.privateKeyString));
            Wallet rrWallet = new Wallet(keyPair.getPrivKeyBytes());
            rrTransaction = new Transaction(rrWallet.getPrivateKey(), rrWallet.getPublicKey(), localWallet.getPublicKey(), 100f, getRRSchedule(), null);
            rrTransaction.transactionId = ByteUtil.stringToBytes("0");
            rrTransaction.outputs.add(
                    new TransactionOutput(
                            rrTransaction.getSender(),
                            rrTransaction.value,
                            rrTransaction.transactionId,
                            0,
                            block.getTimeStamp()

                    )
            );

            System.out.println("Creating and Mining Genesis block... ");
            block.addTransaction(rrTransaction);
            block.mineBlock();
            block.genesisBlock(getGenesisHash());
            String blockJson = new GsonBuilder().setPrettyPrinting().create().toJson(block);
            System.out.println(blockJson);
            // put genesis block to database
            context.putBlock(block);
        } else {
            System.out.println("Genesis block found in database");
        }
    }

    public static GenesisBlock getInstance(Context context, Wallet localWallet)
    {
        if(instance == null) {
            instance = new GenesisBlock(context, localWallet);
        }
        return instance;
    }

    public Block getBlock()
    {
        return block;
    }

    private String getGenesisHash() {
        return StringUtil.applySha256("scheduler");
    }

    public Schedule getRRSchedule()
    {
        double[] tasksData, machinesData;
        new GenerateSimulationData();
        tasksData = GenerateSimulationData.getTasks();
        machinesData = GenerateSimulationData.getMachines();

        ArrayList<Task> tasks = new ArrayList<>();
        for(int i=0; i<tasksData.length;i++)
        {
            tasks.add(new Task(i+1,tasksData[i]));
        }

        ArrayList<Machine> machines = new ArrayList<>();
        for(int i=0; i<machinesData.length;i++)
        {
            machines.add(new Machine(i+1,machinesData[i]));
        }

        return new RoundRobinSchedule(tasks, machines);
    }

    public Schedule getSJFSchedule()
    {
        double[] tasksData, machinesData;
        new GenerateSimulationData();
        tasksData = GenerateSimulationData.getTasks();
        machinesData = GenerateSimulationData.getMachines();

        ArrayList<Task> tasks = new ArrayList<>();
        for(int i=0; i<tasksData.length;i++)
        {
            tasks.add(new Task(i+1,tasksData[i]));
        }

        ArrayList<Machine> machines = new ArrayList<>();
        for(int i=0; i<machinesData.length;i++)
        {
            machines.add(new Machine(i+1,machinesData[i]));
        }

        return new SJFSchedule(tasks, machines);
    }

    public Schedule getFCFSSchedule()
    {
        double[] tasksData, machinesData;
        new GenerateSimulationData();
        tasksData = GenerateSimulationData.getTasks();
        machinesData = GenerateSimulationData.getMachines();

        ArrayList<Task> tasks = new ArrayList<>();
        for(int i=0; i<tasksData.length;i++)
        {
            tasks.add(new Task(i+1,tasksData[i]));
        }

        ArrayList<Machine> machines = new ArrayList<>();
        for(int i=0; i<machinesData.length;i++)
        {
            machines.add(new Machine(i+1,machinesData[i]));
        }

        return new FCFSSchedule(tasks, machines);
    }

    public Schedule getPSOSchedule()
    {
        double[] tasksData, machinesData;
        new GenerateSimulationData();
        tasksData = GenerateSimulationData.getTasks();
        machinesData = GenerateSimulationData.getMachines();

        ArrayList<Task> tasks = new ArrayList<>();
        for(int i=0; i<tasksData.length;i++)
        {
            tasks.add(new Task(i+1,tasksData[i]));
        }

        ArrayList<Machine> machines = new ArrayList<>();
        for(int i=0; i<machinesData.length;i++)
        {
            machines.add(new Machine(i+1,machinesData[i]));
        }

        return new PSOSchedule(tasks, machines);
    }
}
