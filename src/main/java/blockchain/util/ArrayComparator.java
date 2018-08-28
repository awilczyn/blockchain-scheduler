package blockchain.util;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by andrzejwilczynski on 28/08/2018.
 */
class ArrayComparator implements Comparator<byte[]> {
    @Override
    public int compare(byte[] byteArray1, byte[] byteArray2) {

        int result = 0;

        boolean areEquals = Arrays.equals(byteArray1, byteArray2);

        if (!areEquals) {
            result = -1;
        }

        return result;
    }
}
