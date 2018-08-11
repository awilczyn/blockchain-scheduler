package blockchain.serialization;

import blockchain.util.ByteUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrzejwilczynski on 11/08/2018.
 */
public class Serializer
{
    private static final byte PREFIXBYTE            = (byte)0b00001000;
    private static final byte PREFIXBYTEARRAY       = (byte)0b00001001;
    private static final byte PREFIXLISTBYTEARRAY   = (byte)0b10001001;
    private static final byte PREFIXINT             = (byte)0b00000100;
    private static final byte PREFIXBIGINT          = (byte)0b00001100;
    private static final byte PREFIXSTRING          = (byte)0b00000010;
    private static final byte PREFIXSTRINGARRAY     = (byte)0b00000011;

    public static byte[] createParcel(Object[] contents)
    {
        Object o;
        byte[] objectBytes;
        byte prefix = (byte) 0;
        byte[] length = ByteUtil.shortToBytes((short) 0);
        byte[] newParcel = new byte[]{};

        for (int i=0; i < contents.length; i++){
            o = contents[i];
            objectBytes = new byte[]{};

            if(o instanceof Integer) {
                objectBytes = ByteUtil.intToBytes((int) o);
                prefix = PREFIXINT;
                length = ByteUtil.shortToBytes((short) 4);
            } else if (o instanceof Byte) {
                objectBytes = new byte[]{(byte) o};
                prefix = PREFIXBYTE;
                length = ByteUtil.shortToBytes((short) 1);
            } else if(o instanceof Byte[] || o.getClass().isArray()) {
                objectBytes = (byte[]) o;
                prefix = PREFIXBYTEARRAY;
                length = ByteUtil.shortToBytes((short)objectBytes.length);
            } else if (o instanceof BigInteger) {
                objectBytes = ByteUtil.bigIntegerToBytes((BigInteger) o);
                prefix = PREFIXBIGINT;
                length = ByteUtil.shortToBytes((short)objectBytes.length);
            } else if (o instanceof String) {
                objectBytes = ByteUtil.stringToBytes((String) o);
                prefix = PREFIXSTRING;
                length = ByteUtil.shortToBytes((short)objectBytes.length);
            } else if(o instanceof String[]) {
                prefix = PREFIXSTRINGARRAY;
                String[] strArray = (String[]) o;
                for(int j=0; j<strArray.length; j++){
                    byte[] str = ByteUtil.stringToBytes( strArray[j] );
                    byte strlength = (byte) str.length;
                    objectBytes = ByteUtil.concatenateBytes(objectBytes, new byte[] {strlength});
                    objectBytes = ByteUtil.concatenateBytes(objectBytes, str);
                }
                length = ByteUtil.shortToBytes((short)objectBytes.length);
            } else if(o instanceof List<?>) {
                prefix = PREFIXLISTBYTEARRAY;
                List<byte[]> byteArr = (ArrayList<byte[]>) o;
                for (int j=0; j< byteArr.size(); j++) {
                    byte[] bytes = byteArr.get(j);
                    byte[] bytesLength = ByteUtil.shortToBytes((short) bytes.length);
                    objectBytes = ByteUtil.concatenateBytes(objectBytes, bytesLength);
                    objectBytes = ByteUtil.concatenateBytes(objectBytes, bytes);
                }
                length = ByteUtil.shortToBytes((short)objectBytes.length);
            }
            newParcel = addToParcel(newParcel, prefix, length, objectBytes);
        }
        return newParcel;
    }

    public static byte[] addToParcel(byte[] parcel ,byte prefix, byte[] length, byte[] data)
    {
        parcel = ByteUtil.concatenateBytes(parcel, new byte[] {prefix});
        parcel = ByteUtil.concatenateBytes(parcel, length);
        parcel = ByteUtil.concatenateBytes(parcel, data);
        return parcel;
    }
}
