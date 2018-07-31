package blockchain.core;

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
        System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
        System.out.println(StringUtil.getStringFromKey(walletA.publicKey));

        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
        transaction.generateSignature(walletA.privateKey);

        System.out.println("Checking signature...");
        assertTrue(transaction.verifiySignature());
    }
}