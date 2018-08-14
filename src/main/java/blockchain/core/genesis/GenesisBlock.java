package blockchain.core.genesis;

import blockchain.Start;
import blockchain.core.*;
import blockchain.db.Context;
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

    private GenesisBlock(Context context)
    {
        this.context = context;
        this.createGenesisBlock();
    }

    public void createGenesisBlock()
    {
        // get first block from database
        //block = context.getBlock(this.getGenesisHash());

        if (block == null) {
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
            block = new Block( "0");
            block.addTransaction(genesisTransaction);
            block.mineBlock(difficulty);
            block.genesisBlock(getGenesisHash());
            // put genesis block to database
            //context.putBlock(block);
        }
    }

    public static GenesisBlock getInstance(Context context)
    {
        if(instance == null) {
            instance = new GenesisBlock(context);
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
