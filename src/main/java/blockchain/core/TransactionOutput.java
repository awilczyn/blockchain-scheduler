package blockchain.core;

import blockchain.serialization.Serializer;
import blockchain.util.HashUtil;
import blockchain.util.StringUtil;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.Arrays;

/**
 * Created by andrzejwilczynski on 31/07/2018.
 */
public class TransactionOutput implements Serializable
{
    public byte[] id;
    public byte[] recipient;
    public float value;
    public byte[] parentTransactionId;


    public TransactionOutput(byte[] recipient, float value, byte[] parentTransactionId)
    {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = HashUtil.applySha256(getParcelled());
    }

    public byte[] getParcelled() {
        return Serializer.createParcel(new Object[]{this.recipient, this.value, this.parentTransactionId});
    }

    public boolean isMine(byte[]  publicKey)
    {
        return Arrays.equals(publicKey,recipient);
    }
}
