package blockchain.core;

import blockchain.serialization.Serializer;
import blockchain.util.HashUtil;
import blockchain.util.StringUtil;
import blockchain.util.ecdsa.ECKey;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

/**
 * Created by andrzejwilczynski on 31/07/2018.
 */
public class Transaction implements Serializable
{
    public String transactionId;
    private final byte[] sender;
    private final byte[] recipient;
    public float value;
    public byte[] signature;
    /* this transaction signed by the sender, with separate values for r, s and v */
    private final byte[] r;
    private final byte[] s;
    private final byte[] v = new byte[1];
    public int sequence;

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    public Transaction(byte[] sender, byte[] recipient, float value,  ArrayList<TransactionInput> inputs)
    {
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
        this.inputs = inputs;
    }

    public byte[] getParcelled() {
        return Serializer.createParcel(new Object[]{  this.sender, this.recipient, this.value, this.sequence});
    }

    private byte[] calculateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return HashUtil.applySha256(getParcelled());
    }

    public ECKey.ECDSASignature generateSignature(byte[] privateKey)
    {
        return (ECKey.fromPrivate(privateKey)).sign(HashUtil.applyKeccak(getParcelledSansSig()));
    }

    public byte[] getParcelledSansSig() {
        return Serializer.createParcel(new Object[]{this.sender, this.recipient, this.value});
    }

    public boolean verifiySignature() {
        boolean verified = false;
        try {
            ECKey.ECDSASignature sig = ECKey.ECDSASignature.fromComponents(getR(), getS(), getV());
            ECKey temp = ECKey.fromPublicOnly(ECKey.signatureToKeyBytes(HashUtil.applyKeccak(getParcelledSansSig()), sig));
            if(ECKey.verifyWithRecovery(HashUtil.applyKeccak(getParcelledSansSig()), sig) && Arrays.equals(getSender(),temp.getAddress())) verified = true;
        } catch (Exception e) {
            verified = false;
        }
        return verified;
    }

    public boolean processTransaction()
    {
        if(verifiySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        //gather transaction inputs (Make sure they are unspent):
        for(TransactionInput i : inputs) {
            i.UTXO = Node.UTXOs.get(i.transactionOutputId);
        }

        //check if transaction is valid:
        if(getInputsValue() < Node.minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        //generate transaction outputs:
        float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
        transactionId = calculateHash();
        outputs.add(new TransactionOutput( this.recipient, value,transactionId)); //send value to recipient
        outputs.add(new TransactionOutput( this.sender, leftOver,transactionId)); //send the left over 'change' back to sender

        //add outputs to Unspent list
        for(TransactionOutput o : outputs) {
            Node.UTXOs.put(o.id , o);
        }

        //remove transaction inputs from UTXO lists as spent:
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it
            Node.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    public float getInputsValue()
    {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it
            total += i.UTXO.value;
        }
        return total;
    }

    public float getOutputsValue()
    {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }

    public String getTransactionId()
    {
        return transactionId;
    }

    public byte[] getR() {
        return r;
    }

    public byte[] getS() {
        return s;
    }

    public byte getV() {
        return v[0];
    }

    public byte[] getSender(){
        return this.sender;
    }
}
