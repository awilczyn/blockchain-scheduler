package blockchain;

import blockchain.core.*;
import blockchain.db.Context;
import blockchain.networking.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Security;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by andrzejwilczynski on 24/07/2018.
 */
public class Start
{
    public static Node localNode;
    public static Context localContext;
    public static Wallet localWallet;

    public static void main(String[] args)
    {
        Security.addProvider(new BouncyCastleProvider());

        int remotePort = 7000;
        int localPort = 8000;
        String remoteHost = "127.0.0.1";

        HashMap<ServerInfo, Date> serverStatus = new HashMap<>();
        serverStatus.put(new ServerInfo(remoteHost, remotePort), new Date());

        //periodically send heartbeats
        new Thread(new PeriodicHeartBeat(serverStatus, localPort)).start();

        //periodically catchup
        new Thread(new PeriodicCatchup(serverStatus, localPort)).start();

        localContext = new Context();
        localWallet = new Wallet();

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(localPort);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                localNode = new Node(clientSocket, serverStatus, localPort);
                new Thread(localNode).start();
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
}
