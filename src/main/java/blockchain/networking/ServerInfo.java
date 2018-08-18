package blockchain.networking;

import com.sun.javaws.exceptions.InvalidArgumentException;

/**
 * Created by andrzejwilczynski on 18/08/2018.
 */
public class ServerInfo
{
    private String host;
    private int port;

    /**
     *
     * @param host
     * @param port
     */
    public ServerInfo(String host, int port)
    {
        this.host = host;
        this.port = port;
        if (!isValid()) {
            throw new IllegalArgumentException("Wrong host or port");
        }
    }

    public boolean isValid()
    {
        return (!"".equals(host) && port >= 1024 && port <= 65535);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
