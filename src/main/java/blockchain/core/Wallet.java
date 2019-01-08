package blockchain.core;


import blockchain.scheduler.Machine;
import blockchain.scheduler.Schedule;
import blockchain.scheduler.Task;
import blockchain.util.ecdsa.ECKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrzejwilczynski on 31/07/2018.
 */
public class Wallet
{
    private ECKey keyPair;

    public HashMap<byte [],TransactionOutput> UTXOs = new HashMap<>();

    public Wallet()
    {
        this.keyPair = new ECKey();
    }

    public float getBalance()
    {
        float total = 0;
        for (Map.Entry<byte [], TransactionOutput> item: Node.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(keyPair.getPubKey())) { //if output belongs to me ( if coins belong to me )
                UTXOs.put(UTXO.id,UTXO); //add it to our list of unspent transactions.
                total += UTXO.value ;
            }
        }
        return total;
    }

    public Transaction sendDataToSchedule(byte [] recipient, float value, Schedule schedule)
    {
//        if(getBalance() < value) { //gather balance and check funds.
//            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
//            return null;
//        }
        //create array list of inputs
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total = 0;
        for (Map.Entry<byte[], TransactionOutput> item: UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break;
        }

        Transaction newTransaction = new Transaction(keyPair.getPrivKeyBytes(), keyPair.getPubKey(), recipient , value, schedule, inputs);

        for(TransactionInput input: inputs) {
            UTXOs.remove(input.transactionOutputId);
        }
        return newTransaction;
    }

    public byte[] getPublicKey()
    {
        return this.keyPair.getPubKey();
    }

    public byte[] getPrivateKey()
    {
        return this.keyPair.getPrivKeyBytes();
    }
}
