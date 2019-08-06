package blockchain;

import blockchain.core.*;
import blockchain.db.Context;
import blockchain.networking.*;
import blockchain.scheduler.*;
import blockchain.scheduler.RoundRobinSchedule;
import blockchain.scheduler.utils.GenerateSimulationData;
import blockchain.util.ecdsa.ECKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by andrzejwilczynski on 24/07/2018.
 */
public class Node1
{
    public static String privateKeyString = "20a2790bfd13ec2af6ec1595f054dab53a5b0890524c6bd719530939cd974bbc";

    public static Node localNode;

    public static HashMap<ServerInfo, Date> serverStatus = new HashMap<ServerInfo, Date>();

    public static Wallet wallet;

    public static void main(String[] args) throws IOException {
        new GenerateSimulationData();

        Security.addProvider(new BouncyCastleProvider());

        int localPort = 7001;
        prepareNodeList();

        //periodically send heartbeats
        new Thread(new PeriodicHeartBeat(serverStatus, localPort)).start();

        //periodically catchup
        //new Thread(new PeriodicCatchup(serverStatus, localPort)).start();

        Context context = new Context();
        ECKey keyPair = ECKey.fromPrivate(Hex.decode(privateKeyString));
        wallet = new Wallet(keyPair.getPrivKeyBytes());

        localNode = new blockchain.core.Node(context, wallet, serverStatus, localPort);
        localNode.start();

        ServerSocket serverSocket = null;
        boolean addTransaction = true;
        try {
            serverSocket = new ServerSocket(localPort);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (addTransaction) {
//                    localNode.addTransactionToPool(5, getFirstTransactionDataToSchedule());
//                    localNode.addTransactionToPool(10, getSecondTransactionDataToSchedule());
//                    localNode.addTransactionToPool(15, getThirdTransactionDataToSchedule());
//                    localNode.addTransactionToPool(20, getFourthTransactionDataToSchedule());
//                    localNode.addTransactionToPool(30, getFifthTransactionDataToSchedule());
//                    localNode.addTransactionToPool(35, getSixthTransactionDataToSchedule());
//                    localNode.addTransactionToPool(40, getSeventhTransactionDataToSchedule());
//                    localNode.addTransactionToPool(45, getEigthTransactionDataToSchedule());
                }
                addTransaction = false;
                new Thread(new HeartBeatReceiver(clientSocket, serverStatus, localPort)).start();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
            }
        }
    }

    public static void prepareNodeList()
    {
        serverStatus.put(new ServerInfo("127.0.0.1", 7002), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7003), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7004), new Date());
    }

    public static Schedule getDataToSchedule()
    {
        double[] tasksData, machinesData;
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

    public static Schedule getFirstTransactionDataToSchedule()
    {
        return getDataToSchedule();
    }

    public static Schedule getSecondTransactionDataToSchedule()
    {
        return getDataToSchedule();
    }

    public static Schedule getThirdTransactionDataToSchedule()
    {
        return getDataToSchedule();
    }

    public static Schedule getFourthTransactionDataToSchedule()
    {
        return getDataToSchedule();
    }

    public static Schedule getFifthTransactionDataToSchedule()
    {
        return getDataToSchedule();
    }

    public static Schedule getSixthTransactionDataToSchedule()
    {
        return getDataToSchedule();
    }

    public static Schedule getSeventhTransactionDataToSchedule()
    {
        return getDataToSchedule();
    }

    public static Schedule getEigthTransactionDataToSchedule()
    {
        return getDataToSchedule();
    }
}

