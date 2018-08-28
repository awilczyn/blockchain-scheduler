package blockchain;

import blockchain.core.*;
import blockchain.db.Context;
import blockchain.networking.*;
import blockchain.util.ByteUtil;
import blockchain.util.HashUtil;
import blockchain.util.ecdsa.ECKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Security;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static blockchain.util.HashUtil.applyKeccak;

/**
 * Created by andrzejwilczynski on 24/07/2018.
 */
public class Node1
{

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
        Wallet wallet = new Wallet();

        Node localNode = new blockchain.core.Node(context, wallet, serverStatus, localPort);
        localNode.start();

        localNode.addTransactionToPool(10);

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
}

