package blockchain.util;

/**
 * Created by andrzejwilczynski on 07/08/2018.
 */
public class ByteArrayKey
{
    private final byte[] data;

    public ByteArrayKey(byte[] data)
    {
        if (data == null)
        {
            throw new NullPointerException();
        }
        this.data = data;
    }
}
