package blockchain.core;

import blockchain.Start;
import blockchain.core.genesis.GenesisBlock;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by andrzejwilczynski on 24/07/2018.
 */
public class BlockTest
{

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 3;
    public static Wallet walletB;

    private Block genesisBlock;

    public BlockTest()
    {
        Security.addProvider(new BouncyCastleProvider());

        Start.localWallet = new Wallet();
        this.genesisBlock = GenesisBlock.getInstance().getBlock();

        walletB = new Wallet();
        addBlock(genesisBlock);
        Node.UTXOs.put(GenesisBlock.genesisTransaction.outputs.get(0).id, GenesisBlock.genesisTransaction.outputs.get(0));

        Block block1 = new Block(genesisBlock.hash);
        block1.addTransaction(Start.localWallet.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);

        Block block2 = new Block(block1.hash);
        block2.addTransaction(Start.localWallet.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);

        Block block3 = new Block(block2.hash);
        block3.addTransaction(walletB.sendFunds( Start.localWallet.publicKey, 20));
    }

    @Test
    public void isChainValid()
    {
        Block currentBlock;
        Block previousBlock;
        Boolean valid = true;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>();
        tempUTXOs.put(GenesisBlock.genesisTransaction.outputs.get(0).id, GenesisBlock.genesisTransaction.outputs.get(0));

        for(int i=1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("#Current Hashes not equal");
                valid = false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("#Previous Hashes not equal");
                valid = false;
            }
            //check if hash is solved
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                valid = false;
            }

            //loop thru blockchains transactions:
            TransactionOutput tempOutput;
            for(int t=0; t <currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if(!currentTransaction.verifiySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    valid = false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    valid = false;
                }

                for(TransactionInput input: currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        valid = false;
                    }

                    if(input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        valid = false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for(TransactionOutput output: currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if( currentTransaction.outputs.get(0).recipient != currentTransaction.recipient) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    valid = false;
                }
                if( currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    valid = false;
                }
            }
        }
        System.out.println("Blockchain is valid");
        assertTrue(true);
    }

    public static void addBlock(Block newBlock)
    {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}
