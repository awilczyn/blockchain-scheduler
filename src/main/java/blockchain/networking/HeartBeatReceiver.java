package blockchain.networking;


import blockchain.core.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.io.*;

public class HeartBeatReceiver implements Runnable{

    private Socket toClient;
    private HashMap<ServerInfo, Date> serverStatus;
    private int localPort;

    public HeartBeatReceiver(Socket toClient, HashMap<ServerInfo, Date> serverStatus, int localPort) {
        this.toClient = toClient;
        this.serverStatus = serverStatus;
        this.localPort = localPort;
    }

    @Override
    public void run() {
        try {
			heartBeatServerHandler(toClient.getInputStream());
			toClient.close();
        } catch (IOException e) {
    	}
    }
    
    public void heartBeatServerHandler(InputStream clientInputStream) {
    	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientInputStream));
        try {
        	while (true) {
            	String line = bufferedReader.readLine();
            	if (line == null) {
            		break;
            	}
            	
            	String[] tokens = line.split("\\|");
            	String remoteIP = (((InetSocketAddress) toClient.getRemoteSocketAddress()).getAddress()).toString().replace("/", "");
				int remotePort = Integer.valueOf(tokens[2]);

				ServerInfo serverInQuestion;
            	switch (tokens[0]) {
                	case "hb":
						System.out.println("receiving " + line);
						break;
                	case "tx":
						serverInQuestion = new ServerInfo(remoteIP, remotePort);
						this.serverHandler(serverInQuestion, tokens[1]);
						break;
					case "txv":
						this.transactionVerifiedHandler(tokens[1]);
                	default:
            	}
			}
        } catch (Exception e) {
    	}
    }

	public void serverHandler(ServerInfo serverInQuestion, String inputLine) {
		try {
		  Gson gson = new GsonBuilder().create();
          Transaction transaction = gson.fromJson(inputLine, Transaction.class);
          System.out.println("Checking transaction "+transaction);
          if (transaction.verifyTransaction()) {
			  String transactionVerified = "txv|"+transaction;
			  System.out.println("Transaction correct");
			  new Thread(new MessageSender(serverInQuestion, transactionVerified)).start();
		  }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void transactionVerifiedHandler(String inputLine) {
		System.out.println("transaction was verified");
	}
}
