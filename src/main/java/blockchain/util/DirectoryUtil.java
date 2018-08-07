package blockchain.util;

import java.io.File;

/**
 * Created by andrzejwilczynski on 07/08/2018.
 */
public class DirectoryUtil
{
    public static String getPathForDbFiles(String path)
    {
        File file = new File(path);
        file.mkdirs();
        return file.getAbsolutePath();
    }
}
