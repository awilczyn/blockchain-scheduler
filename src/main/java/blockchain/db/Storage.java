package blockchain.db;

import blockchain.util.DirectoryUtil;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

/**
 * Created by andrzejwilczynski on 07/08/2018.
 */
public class Storage<T>
{
    private static Storage instance;

    private String pathDB = DirectoryUtil.getPathForDbFiles("db-files");

    private String pathBlocksDB = pathDB + "/blocks.db";

    private DB blocksDB;

    private HTreeMap blocksMap;

    private Storage()
    {
        blocksDB = getDB(pathBlocksDB,false,true);
        blocksMap = blocksDB.hashMap("map").createOrOpen();
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
        DBMaker.Maker dbConnection = DBMaker.fileDB(path).fileMmapEnable().fileLockDisable();
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
     * @return
     */
    public DB put(StorageTypes map, String key, T object)
    {
        if(map == StorageTypes.BLOCKS) {
            blocksMap.put(key,object);
            return blocksDB;
        }
        return null;
    }

    public Object get(StorageTypes map, String key)
    {
        if(map == StorageTypes.BLOCKS){
            return blocksMap.get(key);
        }
        return null;
    }


    public HTreeMap<byte[], byte[]> getMapForType(StorageTypes map) {
        if(map == StorageTypes.BLOCKS){
            return blocksMap;
        }
        return null;
    }
}
