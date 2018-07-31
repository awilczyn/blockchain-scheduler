package blockchain.core;

/**
 * Created by andrzejwilczynski on 31/07/2018.
 */
public class TransactionInput
{
    public String transactionOutputId;
    public TransactionOutput UTXO; //Contains the Unspent transaction output

    public TransactionInput(String transactionOutputId)
    {
        this.transactionOutputId = transactionOutputId;
    }
}
