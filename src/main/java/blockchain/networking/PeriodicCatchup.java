package blockchain.networking;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Base64;
import java.util.Collections;

public class PeriodicCatchup implements Runnable {
	private HashMap<ServerInfo, Date> serverStatus;
	private int localPort;
	
	public PeriodicCatchup(HashMap<ServerInfo, Date> serverStatus, int localPort) {
		this.serverStatus = serverStatus;
		this.localPort = localPort;
	}
	
	@Override
	public void run() {
		while(true) {
			String LBmessage = "lb|" + String.valueOf(localPort) + "still working ...";

			if (serverStatus.size() <= 5) {
				this.broadcast(LBmessage);
				
			} else {
				//select 5 random peers
				ArrayList<ServerInfo> targetPeers = new ArrayList<ServerInfo>();
				ArrayList<ServerInfo> allPeers = new ArrayList(serverStatus.keySet());
				
				for (int i = 0; i < 5; i++) {
					Collections.shuffle(allPeers);
					targetPeers.add(allPeers.remove(0));
				}
				this.multicast(targetPeers, LBmessage);
				
			}

			//sleep for 2 secs
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}
		}
	}
	
    public void broadcast(String message) {
    	ArrayList<Thread> threadArrayList = new ArrayList<Thread>();
    	for (ServerInfo info: this.serverStatus.keySet()) {
            Thread thread = new Thread(new MessageSender(info, message));
            thread.start();
            threadArrayList.add(thread);
        }
    }
    
    public void multicast(ArrayList<ServerInfo> toPeers, String message) {
    	ArrayList<Thread> threadArrayList = new ArrayList<Thread>();
    	for (int i = 0; i < toPeers.size(); i++) {
    		Thread thread = new Thread(new MessageSender(toPeers.get(i), message));
    		thread.start();
    		threadArrayList.add(thread);
    	}
    }
}