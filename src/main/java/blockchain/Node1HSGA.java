package blockchain;

import blockchain.core.Node;
import blockchain.core.Wallet;
import blockchain.db.Context;
import blockchain.networking.HeartBeatReceiver;
import blockchain.networking.PeriodicHeartBeat;
import blockchain.networking.ServerInfo;
import blockchain.scheduler.*;
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
public class Node1HSGA
{
    public static String privateKeyString = "a46d14de782ac98fe0b3dab21814aa4d67edd2ef51e25044662e313457635b9d";

    public static Node localNode;

    public static HashMap<ServerInfo, Date> serverStatus = new HashMap<ServerInfo, Date>();

    public static ECKey keyPair = ECKey.fromPrivate(Hex.decode(privateKeyString));

    public static Wallet wallet = new Wallet(keyPair.getPrivKeyBytes());

    public static double sumTime = 0;
    public static double counter = 0;

    public static void main(String[] args) throws IOException {
        new GenerateSimulationData();

        Security.addProvider(new BouncyCastleProvider());

        int localPort = 7101;
        prepareNodeList();

        //periodically send heartbeats
        new Thread(new PeriodicHeartBeat(serverStatus, localPort)).start();

        //periodically catchup
        //new Thread(new PeriodicCatchup(serverStatus, localPort)).start();

        Context context = new Context();

        localNode = new Node(context, wallet, serverStatus, localPort);
        localNode.start();

        ServerSocket serverSocket = null;
        boolean addTransaction = true;
        try {
            serverSocket = new ServerSocket(localPort);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (addTransaction) {
//                    localNode.addTransactionToPool(5, getDataToSchedule());
//                    localNode.addTransactionToPool(10, getDataToSchedule());
//                    localNode.addTransactionToPool(15, getDataToSchedule());
//                    localNode.addTransactionToPool(20, getDataToSchedule());
//                    localNode.addTransactionToPool(30, getDataToSchedule());
//                    localNode.addTransactionToPool(35, getDataToSchedule());
//                    localNode.addTransactionToPool(40, getDataToSchedule());
//                    localNode.addTransactionToPool(50, getDataToSchedule());
//                    localNode.addTransactionToPool(60, getDataToSchedule());
//                    localNode.addTransactionToPool(70, getDataToSchedule());
//                    localNode.addTransactionToPool(80, getDataToSchedule());
//                    localNode.addTransactionToPool(90, getDataToSchedule());
//                    localNode.addTransactionToPool(100, getDataToSchedule());
//                    localNode.addTransactionToPool(110, getDataToSchedule());
//                    localNode.addTransactionToPool(120, getDataToSchedule());
//                    localNode.addTransactionToPool(130, getDataToSchedule());
                    System.out.println("Average makespan blockchain: "+sumTime/counter);
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
        serverStatus.put(new ServerInfo("127.0.0.1", 7001), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7002), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7003), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7004), new Date());

        serverStatus.put(new ServerInfo("127.0.0.1", 7102), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7103), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7104), new Date());

        serverStatus.put(new ServerInfo("127.0.0.1", 7201), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7202), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7203), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7204), new Date());

        serverStatus.put(new ServerInfo("127.0.0.1", 7301), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7302), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7303), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7304), new Date());
    }

    public static Schedule getDataToSchedule()
    {
        double[][] tasksData, machinesData;
        tasksData = GenerateSimulationData.getTasks();
        machinesData = GenerateSimulationData.getMachines();

        ArrayList<Task> tasks = new ArrayList<>();
        for(int i=0; i<tasksData.length;i++)
        {
            tasks.add(new Task(i+1,tasksData[i][0], tasksData[i][1]));
        }

        ArrayList<Machine> machines = new ArrayList<>();
        for(int i=0; i<machinesData.length;i++)
        {
            machines.add(new Machine(i+1,machinesData[i][0], machinesData[i][1]));
        }

        Schedule schedule = new HSGASchedule(tasks, machines);
        counter++;
        sumTime = sumTime + schedule.getMakespan();
        return schedule;
    }
}

