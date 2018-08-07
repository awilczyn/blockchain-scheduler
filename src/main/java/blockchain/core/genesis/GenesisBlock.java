package blockchain.core.genesis;

import blockchain.Start;
import blockchain.core.*;

/**
 * Created by andrzejwilczynski on 07/08/2018.
 */
public class GenesisBlock
{
    private static GenesisBlock instance;
    private final Block block;

    public static Transaction genesisTransaction;

    private Wallet wallet;

    private GenesisBlock()
    {
        wallet = new Wallet();
        genesisTransaction = new Transaction(wallet.publicKey, Start.localWallet.publicKey, 100f, null);
        genesisTransaction.generateSignature(wallet.privateKey);
        genesisTransaction.transactionId = "0";
        genesisTransaction.outputs.add(
                new TransactionOutput(
                        genesisTransaction.recipient,
                        genesisTransaction.value,
                        genesisTransaction.transactionId
                )
        );

        System.out.println("Creating and Mining Genesis block... ");
        block = new Block("0");
        block.addTransaction(genesisTransaction);
    }

    public static GenesisBlock getInstance()
    {
        if(instance == null) {
            instance = new GenesisBlock();
        }
        return instance;
    }

    public Block getBlock()
    {
        return block;
    }
}
