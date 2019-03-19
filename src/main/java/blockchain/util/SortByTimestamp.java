package blockchain.util;

import blockchain.core.Block;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * Created by andrzejwilczynski on 19/03/2019.
 */
public class SortByTimestamp implements Comparator<Block> {

    public int compare(Block a, Block b) {
        if ( a.getTimeStamp() < b.getTimeStamp() ) return -1;
        else if ( a.getTimeStamp() == b.getTimeStamp() ) return 0;
        else return 1;
    }
}
