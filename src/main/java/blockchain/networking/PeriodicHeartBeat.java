package blockchain.networking;

import java.util.Date;
import java.util.HashMap;

public class PeriodicHeartBeat implements Runnable {

    private HashMap<ServerInfo, Date> serverStatus;
    private int sequenceNumber;
    private int localPort;

    public PeriodicHeartBeat(HashMap<ServerInfo, Date> serverStatus, int localPort)
    {
        this.serverStatus = serverStatus;
        this.localPort = localPort;
    }

    @Override
    public void run() {
    	String message;
        while(true) {
            // broadcast HeartBeat message to all peers
            message = "hb|heartbeat from another peer working on port: " + String.valueOf(localPort);

            for (ServerInfo info : serverStatus.keySet()) {
                Thread thread = new Thread(new HeartBeatSender(info, message));
                thread.start();
            }

            // sleep for two seconds
            try {
                Thread.sleep(50000);
            } catch (InterruptedException e) {
            }
        }
    }
}
