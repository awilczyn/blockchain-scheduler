package blockchain.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

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

    public static byte[] getNowTimeStamp()
    {
        int dateInSec = (int) (System.currentTimeMillis() / 1000);
        return ByteBuffer.allocate(4).putInt(dateInSec).array();
    }

    public static byte[] shortToBytes(short input)
    {
        return ByteBuffer.allocate(2).putShort(input).array();
    }

    public static byte[] intToBytes(int input)
    {
        return ByteBuffer.allocate(4).putInt(input).array();
    }

    public static byte[] stringToBytes(String input)
    {
        return input.getBytes(Charset.forName("UTF-8"));
    }

    public static byte[] concatenateBytes(byte[] a, byte[] b)
    {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
