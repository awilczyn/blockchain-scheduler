package blockchain.core;

import blockchain.Node1;
import blockchain.Node2;
import blockchain.Node3;
import blockchain.Node4;
import blockchain.game.Player;
import blockchain.game.StackelbergGame;
import blockchain.scheduler.*;
import blockchain.serialization.Serializer;
import blockchain.util.ByteUtil;
import blockchain.util.HashUtil;
import blockchain.util.ecdsa.ECKey;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by andrzejwilczynski on 31/07/2018.
 */
public class Transaction implements Serializable
{
    public byte[] transactionId;
    private final byte[] sender;
    private final byte[] recipient;
    public float value;
    public Schedule schedule;

    /* this transaction signed by the sender, with separate values for r, s and v */
    private final byte[] r;
    private final byte[] s;
    private final byte[] v = new byte[1];
    public int sequence;

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    public int numberOfVerification = 0;

    public Transaction(byte[] privateKey, byte[] sender, byte[] recipient, float value, Schedule schedule, ArrayList<TransactionInput> inputs)
    {
        this.sender = sender;
        this.recipient = recipient;
        this.value = value;
        this.schedule = schedule;
        this.inputs = inputs;
        ECKey.ECDSASignature temp = generateSignature(privateKey);
        this.r = temp.r.toByteArray();
        this.s = temp.s.toByteArray();
        this.v[0] = temp.v;
        this.transactionId = calculateHash();
    }

    public byte[] getParcelled() {
        return Serializer.createParcel(new Object[]{  this.sender, this.recipient, this.value, this.schedule, this.sequence});
    }

    private byte[] calculateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return HashUtil.applySha256(getParcelled());
    }

    public static ECKey.ECDSASignature generateSignature(byte[] privateKey)
    {
        return (ECKey.fromPrivate(privateKey)).sign(HashUtil.applyKeccak(getParcelledSansSig()));
    }

    public static byte[] getParcelledSansSig() {
        return ByteUtil.stringToBytes("test");
      //  return Serializer.createParcel(new Object[]{this.sender, this.recipient, this.value});
    }

    public boolean verifiySignature() {
        boolean verified = false;
        try {
            ECKey.ECDSASignature sig = ECKey.ECDSASignature.fromComponents(getR(), getS(), getV());
            ECKey temp = ECKey.fromPublicOnly(ECKey.signatureToKeyBytes(HashUtil.applyKeccak(getParcelledSansSig()), sig));
            //if(ECKey.verifyWithRecovery(HashUtil.applyKeccak(getParcelledSansSig()), sig) && Arrays.equals(getSender(),temp.getAddress())) verified = true;
            if(ECKey.verifyWithRecovery(HashUtil.applyKeccak(getParcelledSansSig()), sig)) verified = true;
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
        outputs.add(new TransactionOutput( this.recipient, value, transactionId)); //send value to recipient
        outputs.add(new TransactionOutput( this.sender, leftOver, transactionId)); //send the left over 'change' back to sender

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

    public byte[] getTransactionId()
    {
        return transactionId;
    }

    public byte[] getR() {
        return this.r;
    }

    public byte[] getS() {
        return this.s;
    }

    public byte getV() {
        return this.v[0];
    }

    public byte[] getSender(){
        return this.sender;
    }

    public byte[] getRecipient()
    {
        return this.recipient;
    }

    public boolean verifyTransaction() throws IOException {
        if(!this.verifiySignature()) return false;

        if (!this.verifySchedule()) return false;

        //Transaction is verified;
        return true;
    }

    public String toString()
    {
        return ByteUtil.bytesToString(this.transactionId);
    }

    private boolean verifySchedule() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.addAll(this.schedule.tasks);
        ArrayList<Machine> machines = new ArrayList<>();
        for (int i=0; i<this.schedule.machines.size(); i++) {
            machines.add(new Machine(this.schedule.machines.get(i).getNumberOfOperationsPerSecond()));
        }
        String nodeName = System.getProperty("sun.java.command");
        Schedule ownSchedule;
        boolean betterSchedule = false;

        Player leader = new Player(this.getSchedulingFactorForPublicKey(sender), schedule.getTime());
//        Player leader = new Player(1500, 15);
        Player follower = null;
        if (nodeName.equals("blockchain.Node1")) {
            ownSchedule = new AwsSchedule(tasks, machines);
            follower = new Player(
                    this.getSchedulingFactorForPublicKey(Node1.wallet.getPublicKey()),
                    ownSchedule.getTime()
            );
        }
        if (nodeName.equals("blockchain.Node2")) {
            ownSchedule = new AzureSchedule(tasks, machines);
            follower = new Player(
                    this.getSchedulingFactorForPublicKey(Node2.wallet.getPublicKey()),
                    ownSchedule.getTime()
            );
        }
        if (nodeName.equals("blockchain.Node3")) {
            ownSchedule = new IbmSchedule(tasks, machines);
            follower = new Player(
                    this.getSchedulingFactorForPublicKey(Node3.wallet.getPublicKey()),
                    ownSchedule.getTime()
            );
        }
        if (nodeName.equals("blockchain.Node4")) {
            ownSchedule = new OtherSchedule(tasks, machines);
            follower = new Player(
                    this.getSchedulingFactorForPublicKey(Node4.wallet.getPublicKey()),
                    ownSchedule.getTime()
            );
        }
//        Player follower2 = new Player(8000, 6);
//        StackelbergGame stackelbergGame = new StackelbergGame(follower2, leader);
        StackelbergGame stackelbergGame = new StackelbergGame(follower, leader);
        if (stackelbergGame.isFollowerHasBetterSchedule()) {
            return false;
        } else {
            return true;
        }
    }

    public float getSchedulingFactorForPublicKey(byte[]  publicKey)
    {
        float total = 0;
        for (Map.Entry<byte [], TransactionOutput> item: Node.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) { //if output belongs to me ( if coins belong to me )
                Node.UTXOs.put(UTXO.id,UTXO); //add it to our list of unspent transactions.
                total += UTXO.value ;
            }
        }
        return total;
    }
}
