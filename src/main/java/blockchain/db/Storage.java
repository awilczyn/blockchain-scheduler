package blockchain.db;

import blockchain.core.Block;
import blockchain.util.ByteArrayKey;
import blockchain.util.DirectoryUtil;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.util.HashMap;

/**
 * Created by andrzejwilczynski on 07/08/2018.
 */
public class Storage
{
    private static Storage instance;

    private String pathDB = DirectoryUtil.getPathForDbFiles("db-files");

    private String pathBlocksDB = pathDB + "/blocks.db";

    private DB blocksDB;

    private HTreeMap<byte[], byte[]> blocksMap;

    public HashMap<ByteArrayKey, Block> blocks;

    private Storage()
    {
        blocksDB            = getDB(pathBlocksDB,false,true);
        blocksMap           = blocksDB.hashMap("map").keySerializer(Serializer.BYTE_ARRAY).valueSerializer(Serializer.BYTE_ARRAY).createOrOpen();
    }

    /**
     *
     * @param path
     * @param safeMode
     * @param autoCleanup
     * @return
     */
    private DB getDB(String path, boolean safeMode, boolean autoCleanup)
    {
        DBMaker.Maker dbConnection = DBMaker.fileDB(path).fileMmapEnable();
        if(safeMode){
            dbConnection = dbConnection.transactionEnable();
        }
        if (autoCleanup){
            dbConnection.closeOnJvmShutdown();
        }
        return dbConnection.make();
    }

    public static Storage getInstance()
    {
        if(instance == null){
            instance = new Storage();
        }
        return instance;
    }

    /**
     *
     * @param map
     * @param key
     * @param value
     * @return
     */
    public DB put(StorageTypes map, byte[] key, byte[] value)
    {
        if(map == StorageTypes.BLOCKS) {
            blocksMap.put(key,value);
            return blocksDB;
        }
        return null;
    }
}
