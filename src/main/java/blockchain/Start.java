package blockchain;

import blockchain.core.*;
import blockchain.db.Context;
import com.google.gson.GsonBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

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

        localContext = new Context();
        localWallet = new Wallet();
        localNode = new Node();

        Start.localNode.start();
    }
}
