package blockchain.core;

import java.io.Serializable;

/**
 * Created by andrzejwilczynski on 31/07/2018.
 */
public class TransactionInput implements Serializable
{
    public String transactionOutputId;
    public TransactionOutput UTXO; //Contains the Unspent transaction output

    public TransactionInput(String transactionOutputId)
    {
        this.transactionOutputId = transactionOutputId;
    }
}
