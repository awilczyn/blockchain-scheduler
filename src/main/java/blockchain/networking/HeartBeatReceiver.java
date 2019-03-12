package blockchain.networking;


import blockchain.core.Node;
import blockchain.core.Transaction;
import blockchain.util.ByteUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
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

				ServerInfo serverInQuestion;
            	switch (tokens[0]) {
                	case "hb":
						System.out.println("receiving " + line);
						break;
                	case "tx":
						int remotePort = Integer.valueOf(tokens[2]);
						serverInQuestion = new ServerInfo(remoteIP, remotePort);
						this.serverHandler(serverInQuestion, tokens[1]);
						break;
					case "txv":
						this.transactionVerifiedHandler(tokens[1]);
						break;
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

          BigInteger key = ByteUtil.bytesToBigInteger(transaction.transactionId);
          if(!Node.pool.containsKey(key)) {
			  System.out.println("Sending transaction to peers for accept... ");

			  System.out.println("Checking transaction "+transaction);
			  if (transaction.verifyTransaction()) {
				  String transactionVerified = "txv|"+inputLine;
				  System.out.println("Schedule correct, transaction verified");
				  //broadcast("txv|" + inputLine);
				  new Thread(new MessageSender(serverInQuestion, transactionVerified)).start();
			  } else {
				  System.out.println("Schedule incorrect, transaction rejected");
				  String transactionVerified = "tx|"+inputLine;
				  //broadcast("tx|" + inputLine);
				  new Thread(new MessageSender(serverInQuestion, transactionVerified)).start();
			  }

		  }

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void transactionVerifiedHandler(String inputLine)
	{
		Gson gson = new GsonBuilder().create();
		Transaction transaction = gson.fromJson(inputLine, Transaction.class);
		BigInteger key = ByteUtil.bytesToBigInteger(transaction.transactionId);
		if(Node.pool.containsKey(key)) {
			Transaction trans = Node.pool.get(key);
			trans.numberOfVerification++;
			if (trans.numberOfVerification >= Node.getMinimumNumberOfConfirmation()) {
				Node.transactionVerifiedPool.put(key, trans);
			}
		}
    	System.out.println("transaction was verified");
	}

	public void broadcast(String message) {
		for (ServerInfo info: this.serverStatus.keySet()) {
			message = message+"|"+localPort;
			new Thread(new MessageSender(info, message)).start();
		}
	}
}
