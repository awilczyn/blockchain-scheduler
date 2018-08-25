package blockchain.core;

import java.io.Serializable;

/**
 * Created by andrzejwilczynski on 31/07/2018.
 */
public class TransactionInput implements Serializable
{
    public byte [] transactionOutputId;
    public TransactionOutput UTXO; //Contains the Unspent transaction output

    public TransactionInput(byte [] transactionOutputId)
    {
        this.transactionOutputId = transactionOutputId;
    }
}
