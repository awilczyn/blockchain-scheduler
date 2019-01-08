package blockchain.core;

import blockchain.Node1;
import blockchain.util.ByteUtil;
import org.junit.Test;
import blockchain.util.StringUtil;

import java.security.Security;

import static org.junit.Assert.*;

/**
 * Created by andrzejwilczynski on 31/07/2018.
 */
public class TransactionTest
{
    public static Wallet walletA;
    public static Wallet walletB;

    @Test
    public void isSignatureValid()
    {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        walletA = new Wallet();
        walletB = new Wallet();

        System.out.println("Private and public keys:");
        System.out.println(ByteUtil.bytesToString(walletA.getPrivateKey()));
        System.out.println(ByteUtil.bytesToString(walletA.getPublicKey()));

        Transaction transaction = new Transaction(walletA.getPrivateKey(), walletA.getPublicKey(), walletB.getPublicKey(), 5, Node1.getDataToSchedule(), null);
        transaction.generateSignature(walletA.getPrivateKey());

        System.out.println("Checking signature...");
        assertTrue(transaction.verifiySignature());
    }
}