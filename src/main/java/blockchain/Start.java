package blockchain;

import blockchain.core.*;
import blockchain.db.Context;
import blockchain.networking.ServerInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

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
        ServerInfo serverInfo = new ServerInfo(remoteHost, remotePort);

        localContext = new Context();
        localWallet = new Wallet();
        localNode = new Node(serverInfo, localPort);

        Start.localNode.start();
    }
}
