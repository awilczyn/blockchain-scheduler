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
public class Node2PSO
{
    public static String privateKeyString = "a46d14de782ac98fe0b3dab21814aa4d67edd2ef51e25044662e313457635b9d";

    public static Node localNode;

    public static HashMap<ServerInfo, Date> serverStatus = new HashMap<ServerInfo, Date>();

    public static ECKey keyPair = ECKey.fromPrivate(Hex.decode(privateKeyString));

    public static Wallet wallet = new Wallet(keyPair.getPrivKeyBytes());

    public static void main(String[] args) throws IOException {
        new GenerateSimulationData();

        Security.addProvider(new BouncyCastleProvider());

        int localPort = 7102;
        prepareNodeList();

        //periodically send heartbeats
        new Thread(new PeriodicHeartBeat(serverStatus, localPort)).start();

        //periodically catchup
        //new Thread(new PeriodicCatchup(serverStatus, localPort)).start();

        Context context = new Context();

        localNode = new blockchain.core.Node(context, wallet, serverStatus, localPort);
        localNode.start();

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
        serverStatus.put(new ServerInfo("127.0.0.1", 7001), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7002), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7003), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7004), new Date());

        serverStatus.put(new ServerInfo("127.0.0.1", 7101), new Date());
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

}

