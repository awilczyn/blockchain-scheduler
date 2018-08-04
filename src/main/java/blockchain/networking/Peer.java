package blockchain.networking;

import blockchain.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

/**
 * Created by andrzejwilczynski on 04/08/2018.
 */
public class Peer
{
    public final Socket socket;
    private Thread peerThread;

    public Peer(final Socket socket)  {
        this.socket = socket;
        peerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    listen();
                    Log.log(Level.INFO, "Closing connection to " + socket.getInetAddress() + ":" + socket.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        peerThread.start();
    }

    public void listen() throws IOException {
        String command;
        while(true){
            try{
                DataInputStream in = new DataInputStream(this.socket.getInputStream());
                DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
//                command = receive(in);
//                send(serve(command), out);
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            }


        }
    }
}
