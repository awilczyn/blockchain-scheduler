package blockchain.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by andrzejwilczynski on 04/08/2018.
 */
public class Log
{
    private static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void log(Level level, String message)
    {
        LOGGER.log(level, message);
    }
}
