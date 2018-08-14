package blockchain.db;

import blockchain.core.Block;

import java.util.HashMap;

/**
 * Created by andrzejwilczynski on 04/08/2018.
 */
public class Context
{
    public HashMap<String, Block> blocks;

    public Context()
    {
        this.blocks = new HashMap<>();
    }

    /**
     *
     * @param block
     */
    public void putBlock(Block block)
    {
        blocks.put(block.hash, block);
        Storage.getInstance().put(StorageTypes.BLOCKS, block.hash, blocks.get(block.hash));
    }

    public Block getBlock(String index)
    {
        return (Block) Storage.getInstance().get(StorageTypes.BLOCKS, index);
    }

    public void saveBlocksToDB()
    {
        for( String key : blocks.keySet() ) {
            Storage.getInstance().put(StorageTypes.BLOCKS, key, blocks.get(key));
        }
    }
}
