package blockchain.db;

import blockchain.core.Block;
import blockchain.util.ByteArrayKey;
import blockchain.util.ByteUtil;

import java.math.BigInteger;
import java.util.HashMap;

/**
 * Created by andrzejwilczynski on 04/08/2018.
 */
public class Context
{
    public HashMap<ByteArrayKey, Block> blocks;

    public Context()
    {
        this.blocks = new HashMap<ByteArrayKey, Block>();
    }

    public void putBlock(Block block)
    {
        //byte[] indexBytes = ByteUtil.bigIntegerToBytes( BigInteger.valueOf(block.header.getIndex()));
        //blocks.put(new ByteArrayKey(indexBytes) , block);
    }

    public void saveBlocksToDB()
    {
//        for( ByteArrayKey key : blocks.keySet() ) {
//            Storage.getInstance().put(StorageTypes.BLOCKS, key.toByteArray(), blocks.get(key).getEncoded());
//        }
    }
}
