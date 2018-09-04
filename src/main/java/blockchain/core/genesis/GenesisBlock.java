package blockchain.core.genesis;

import blockchain.core.*;
import blockchain.db.Context;
import blockchain.util.ByteUtil;
import blockchain.util.StringUtil;

import static blockchain.core.Node.difficulty;

/**
 * Created by andrzejwilczynski on 07/08/2018.
 */
public class GenesisBlock
{
    private static GenesisBlock instance;
    private Block block;
    private Context context;

    public static Transaction genesisTransaction;

    private Wallet wallet;
    private Wallet localWallet;

    private GenesisBlock(Context context, Wallet localWallet)
    {
        this.context = context;
        this.localWallet = localWallet;
        this.createGenesisBlock();
    }

    public void createGenesisBlock()
    {
        // get first block from database
        System.out.println("Searching genesis block in database... ");
        block = context.getBlock(this.getGenesisHash());


        if (block == null) {
            wallet = new Wallet();
            genesisTransaction = new Transaction(wallet.getPrivateKey(), wallet.getPublicKey(), localWallet.getPublicKey(), 100f, null);
            genesisTransaction.transactionId = ByteUtil.stringToBytes("0");
            genesisTransaction.outputs.add(
                    new TransactionOutput(
                            genesisTransaction.getRecipient(),
                            genesisTransaction.value,
                            genesisTransaction.transactionId
                    )
            );

            System.out.println("Creating and Mining Genesis block... ");
            block = new Block( "0");
            block.addTransaction(genesisTransaction);
            block.mineBlock(difficulty);
            block.genesisBlock(getGenesisHash());
            // put genesis block to database
            context.putBlock(block);
        } else {
            System.out.println("Genesis block found in database");
        }
    }

    public static GenesisBlock getInstance(Context context, Wallet localWallet)
    {
        if(instance == null) {
            instance = new GenesisBlock(context, localWallet);
        }
        return instance;
    }

    public Block getBlock()
    {
        return block;
    }

    private String getGenesisHash() {
        return StringUtil.applySha256("scheduler");
    }
}
