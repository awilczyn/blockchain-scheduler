package blockchain.util;

import java.math.BigInteger;

/**
 * Created by andrzejwilczynski on 07/08/2018.
 */
public class ByteUtil
{
    /**
     *
     * @param value
     * @return
     */
    public static byte[] bigIntegerToBytes(BigInteger value)
    {
        if (value == null)
            return null;

        byte[] data = value.toByteArray();

        if (data.length != 1 && data[0] == 0) {
            byte[] tmp = new byte[data.length - 1];
            System.arraycopy(data, 1, tmp, 0, tmp.length);
            data = tmp;
        }
        return data;
    }
}
