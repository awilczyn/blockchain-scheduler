package blockchain;

import blockchain.core.*;
import blockchain.db.Context;
import blockchain.networking.*;
import blockchain.scheduler.*;
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

import static blockchain.util.HashUtil.applyKeccak;

/**
 * Created by andrzejwilczynski on 24/07/2018.
 */
public class Node1
{
    public static String privateKeyString = "20a2790bfd13ec2af6ec1595f054dab53a5b0890524c6bd719530939cd974bbc";

    public static Node localNode;

    public static HashMap<ServerInfo, Date> serverStatus = new HashMap<ServerInfo, Date>();

    public static void main(String[] args) throws IOException {
        Security.addProvider(new BouncyCastleProvider());

        int localPort = 7001;
        prepareNodeList();

        //periodically send heartbeats
        new Thread(new PeriodicHeartBeat(serverStatus, localPort)).start();

        //periodically catchup
        //new Thread(new PeriodicCatchup(serverStatus, localPort)).start();

        Context context = new Context();
        ECKey keyPair = ECKey.fromPrivate(Hex.decode(privateKeyString));
        Wallet wallet = new Wallet(keyPair.getPrivKeyBytes());

        localNode = new blockchain.core.Node(context, wallet, serverStatus, localPort);
        localNode.start();

        localNode.addTransactionToPool(10, getDataToSchedule());

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(localPort);
            while (true) {
                Socket clientSocket = serverSocket.accept();
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
        serverStatus.put(new ServerInfo("127.0.0.1", 7004), new Date());
    }

    public static Schedule getDataToSchedule()
    {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Task(1,10000));
        tasks.add(new Task(2,5000));
        tasks.add(new Task(3,1000));
        tasks.add(new Task(4,15000));
        ArrayList<Machine> machines = new ArrayList<>();
        machines.add(new Machine(3000));
        machines.add(new Machine(30000));
        machines.add(new Machine(10000));

        return new AwsSchedule(tasks, machines);
    }
}

