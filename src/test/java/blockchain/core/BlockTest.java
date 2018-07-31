package blockchain.core;

import org.junit.Test;

import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * Created by andrzejwilczynski on 24/07/2018.
 */
public class BlockTest
{

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 3;
    public static Wallet walletA;
    public static Wallet walletB;

    public BlockTest()
    {
        blockchain.add(new Block("Hi im the first block", "0"));
        blockchain.get(0).mineBlock(difficulty);
        blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size()-1).hash));
        blockchain.get(1).mineBlock(difficulty);
        blockchain.add(new Block("Hey im the third block", blockchain.get(blockchain.size()-1).hash));
        blockchain.get(2).mineBlock(difficulty);
    }

    @Test
    public void isChainValid()
    {
        Block currentBlock;
        Block previousBlock;
        Boolean valid = true;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            //compare registered hash and calculated hash:
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                valid = false;
            }
            //compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Previous Hashes not equal");
                valid = false;
            }
            //check if hash is solved
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                valid = false;
            }
        }
        assertTrue(valid);
    }
}
