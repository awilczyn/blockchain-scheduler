package blockchain.networking;

import blockchain.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by andrzejwilczynski on 04/08/2018.
 */
public class Peer2Peer
{
    private int port;
    private ArrayList<Peer> peers;
    public Thread serverThread;
    private ServerSocket server;
    private boolean runningServer;
    private Socket socket = null;

    public Peer2Peer(int port){
        this.port = port;
        peers = new ArrayList<Peer>();
        serverThread = new Thread(new Runnable() {
            public void run() {
                try {
                    listen();
                    Log.log(Level.INFO, "Connection Ended");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void listen() throws IOException, SocketTimeoutException {
        Log.log(Level.INFO, "Server starting...");
        server = new ServerSocket(this.port);
        Log.log(Level.INFO, "Server started on port " + this.port);
        Peer peer;
        server.setSoTimeout(10000);
        while(runningServer){
            try{
                socket = server.accept();
                Log.log(Level.INFO, "Passed Accept");
                peer = new Peer(socket);
                Log.log(Level.INFO, "Connection received from: " + peer.toString());
                peers.add(peer);
                Log.log(Level.INFO, "New peer: " + peer.toString());
            } catch (SocketTimeoutException e) {
                //e.printStackTrace();
            }
        }
    }

    public void start(){
        if(serverThread.isAlive()){
            Log.log(Level.INFO, "Server is already running.");
            return;
        }
        runningServer = true;
        serverThread.start();
    }
}
