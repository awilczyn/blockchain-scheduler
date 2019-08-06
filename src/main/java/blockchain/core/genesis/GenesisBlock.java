package blockchain.core.genesis;

import blockchain.Node1;
import blockchain.Node2;
import blockchain.Node3;
import blockchain.Node4;
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

            ECKey node1 = ECKey.fromPrivate(Hex.decode(Node1.privateKeyString));
            Wallet rrWallet = new Wallet(node1.getPrivKeyBytes());
            Schedule rrSchedule = getRRSchedule();
            rrTransaction = new Transaction(rrWallet.getPrivateKey(), rrWallet.getPublicKey(), localWallet.getPublicKey(), 100f, rrSchedule, null);
            rrTransaction.transactionId = ByteUtil.stringToBytes("1");
            rrTransaction.outputs.add(
                    new TransactionOutput(
                            rrTransaction.getSender(),
                            rrTransaction.value,
                            rrTransaction.transactionId,
                            rrSchedule.getSumOfInstruction(),
                            block.getTimeStamp()

                    )
            );

            ECKey node2 = ECKey.fromPrivate(Hex.decode(Node2.privateKeyString));
            Wallet psoWallet = new Wallet(node2.getPrivKeyBytes());
            Schedule psoSchedule = getRRSchedule();
            psoTransaction = new Transaction(psoWallet.getPrivateKey(), psoWallet.getPublicKey(), localWallet.getPublicKey(), 100f, psoSchedule, null);
            psoTransaction.transactionId = ByteUtil.stringToBytes("2");
            psoTransaction.outputs.add(
                    new TransactionOutput(
                            psoTransaction.getSender(),
                            psoTransaction.value,
                            psoTransaction.transactionId,
                            psoSchedule.getSumOfInstruction(),
                            block.getTimeStamp()

                    )
            );

            ECKey node3 = ECKey.fromPrivate(Hex.decode(Node3.privateKeyString));
            Wallet sjfWallet = new Wallet(node3.getPrivKeyBytes());
            Schedule sjfSchedule = getSJFSchedule();
            sjfTransaction = new Transaction(sjfWallet.getPrivateKey(), sjfWallet.getPublicKey(), localWallet.getPublicKey(), 100f, sjfSchedule, null);
            sjfTransaction.transactionId = ByteUtil.stringToBytes("3");
            sjfTransaction.outputs.add(
                    new TransactionOutput(
                            sjfTransaction.getSender(),
                            sjfTransaction.value,
                            sjfTransaction.transactionId,
                            sjfSchedule.getSumOfInstruction(),
                            block.getTimeStamp()

                    )
            );

            ECKey node4 = ECKey.fromPrivate(Hex.decode(Node4.privateKeyString));
            Wallet fcfsWallet = new Wallet(node4.getPrivKeyBytes());
            Schedule fcfsSchedule = getFCFSSchedule();
            fcfsTransaction = new Transaction(fcfsWallet.getPrivateKey(), fcfsWallet.getPublicKey(), localWallet.getPublicKey(), 100f, fcfsSchedule, null);
            fcfsTransaction.transactionId = ByteUtil.stringToBytes("4");
            fcfsTransaction.outputs.add(
                    new TransactionOutput(
                            fcfsTransaction.getSender(),
                            fcfsTransaction.value,
                            fcfsTransaction.transactionId,
                            fcfsSchedule.getSumOfInstruction(),
                            block.getTimeStamp()

                    )
            );

            System.out.println("Creating and Mining Genesis block... ");
            block.addTransaction(rrTransaction);
            block.addTransaction(psoTransaction);
            block.addTransaction(sjfTransaction);
            block.addTransaction(fcfsTransaction);
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
