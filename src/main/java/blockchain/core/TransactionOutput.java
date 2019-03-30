package blockchain.core;

import blockchain.serialization.Serializer;
import blockchain.util.HashUtil;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by andrzejwilczynski on 31/07/2018.
 */
public class TransactionOutput implements Serializable
{
    public byte[] id;
    public byte[] sender;
    public float value;
    public byte[] parentTransactionId;
    public float schedulingFactor;
    public long timestamp;

    public TransactionOutput(byte[] sender, float value, byte[] parentTransactionId, float schedulingFactor, long timestamp)
    {
        this.sender = sender;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = HashUtil.applySha256(getParcelled());
        this.schedulingFactor = schedulingFactor;
        this.timestamp = timestamp;
    }

    public byte[] getParcelled() {
        return Serializer.createParcel(new Object[]{this.sender, this.value, this.parentTransactionId, this.schedulingFactor});
    }

    public boolean isMine(byte[]  publicKey)
    {
        return Arrays.equals(publicKey,sender);
    }
}
