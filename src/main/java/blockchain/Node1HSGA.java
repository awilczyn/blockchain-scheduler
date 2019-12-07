package blockchain;

import blockchain.core.Node;
import blockchain.core.Wallet;
import blockchain.db.Context;
import blockchain.networking.HeartBeatReceiver;
import blockchain.networking.PeriodicHeartBeat;
import blockchain.networking.ServerInfo;
import blockchain.scheduler.*;
import blockchain.scheduler.utils.Constants;
import blockchain.scheduler.utils.GenerateSimulationData;
import blockchain.util.Wilcoxon;
import blockchain.util.ecdsa.ECKey;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Security;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by andrzejwilczynski on 24/07/2018.
 */
public class Node1HSGA
{
    public static String privateKeyString = "a46d14de782ac98fe0b3dab21814aa4d67edd2ef51e25044662e313457635b9d";

    public static Node localNode;

    public static HashMap<ServerInfo, Date> serverStatus = new HashMap<ServerInfo, Date>();

    public static ECKey keyPair = ECKey.fromPrivate(Hex.decode(privateKeyString));

    public static Wallet wallet = new Wallet(keyPair.getPrivKeyBytes());

    public static ArrayList<Double> makespan = new ArrayList<Double>();
    public static ArrayList<Double> flowtime = new ArrayList<Double>();
    public static ArrayList<Double> economicCost = new ArrayList<Double>();
    public static ArrayList<Double> resourceUtilization = new ArrayList<Double>();

    public static void main(String[] args) throws IOException {
        new GenerateSimulationData();

        Security.addProvider(new BouncyCastleProvider());

        int localPort = 7101;
        prepareNodeList();

        //periodically send heartbeats
        new Thread(new PeriodicHeartBeat(serverStatus, localPort)).start();

        //periodically catchup
        //new Thread(new PeriodicCatchup(serverStatus, localPort)).start();

        Context context = new Context();

        localNode = new Node(context, wallet, serverStatus, localPort);
        localNode.start();

        ServerSocket serverSocket = null;
        boolean addTransaction = true;
        try {
            serverSocket = new ServerSocket(localPort);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (addTransaction) {
//                    int value = 0;
//                    for(int i=0; i<Constants.NO_OF_ATTEMPTS*2;i++) {
//                        value = value + 5;
//                        localNode.addTransactionToPool(value, getDataToSchedule());
//                    }
//                    double[] makespanArray = new double[makespan.size()];
//                    for (int i = 0; i < makespan.size(); i++) {
//                        makespanArray[i] = makespan.get(i).doubleValue();
//                    }
//                    Wilcoxon.dataForWilcoxon(makespanArray);
//                    double[] flowtimeArray = new double[flowtime.size()];
//                    for (int i = 0; i < flowtime.size(); i++) {
//                        flowtimeArray[i] = flowtime.get(i).doubleValue();
//                    }
//                    double[] economicCostArray = new double[economicCost.size()];
//                    for (int i = 0; i < economicCost.size(); i++) {
//                        economicCostArray[i] = economicCost.get(i).doubleValue();
//                    }
//                    double[] resourceUtilizationArray = new double[resourceUtilization.size()];
//                    for (int i = 0; i < resourceUtilization.size(); i++) {
//                        resourceUtilizationArray[i] = resourceUtilization.get(i).doubleValue();
//                    }
//                    DescriptiveStatistics daMakespan = new DescriptiveStatistics(makespanArray);
//                    Median medianMakespan = new Median();
//                    DescriptiveStatistics daFlowtime = new DescriptiveStatistics(flowtimeArray);
//                    Median medianFlowtime = new Median();
//                    DescriptiveStatistics daEconomicCost = new DescriptiveStatistics(economicCostArray);
//                    Median medianEconomicCost = new Median();
//                    DescriptiveStatistics daResourceUtilization = new DescriptiveStatistics(resourceUtilizationArray);
//                    Median medianResourceUtilization = new Median();
//                    DecimalFormat df = new DecimalFormat("#####0.000");
//                    DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
//                    dfs.setDecimalSeparator(',');
//                    df.setDecimalFormatSymbols(dfs);
//                    System.out.println("Criterion Min, Q1, Median, Quartile 3, Max");
//                    System.out.println("makespan: "+
//                            df.format(daMakespan.getMin()) + ";" +
//                            df.format(daMakespan.getPercentile(25))+";"+
//                            df.format(medianMakespan.evaluate(makespanArray)) + ";" +
//                            df.format(daMakespan.getPercentile(75)) + ";" +
//                            df.format(daMakespan.getMax()));
//                    System.out.println("flowtime: "+
//                            df.format(daFlowtime.getMin()) + ";" +
//                            df.format(daFlowtime.getPercentile(25))+";"+
//                            df.format(medianFlowtime.evaluate(flowtimeArray)) + ";" +
//                            df.format(daFlowtime.getPercentile(75)) + ";" +
//                            df.format(daFlowtime.getMax()));
//                    System.out.println("economic cost: "+
//                            df.format(daEconomicCost.getMin()) + ";" +
//                            df.format(daEconomicCost.getPercentile(25))+";"+
//                            df.format(medianEconomicCost.evaluate(economicCostArray)) + ";" +
//                            df.format(daEconomicCost.getPercentile(75)) + ";" +
//                            df.format(daEconomicCost.getMax()));
//                    System.out.println("resource utilization: "+
//                            df.format(daResourceUtilization.getMin()) + ";" +
//                            df.format(daResourceUtilization.getPercentile(25))+";"+
//                            df.format(medianResourceUtilization.evaluate(resourceUtilizationArray)) + ";" +
//                            df.format(daResourceUtilization.getPercentile(75)) + ";" +
//                            df.format(daResourceUtilization.getMax()));
                }
                addTransaction = false;
                new Thread(new HeartBeatReceiver(clientSocket, serverStatus, localPort)).start();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
            }
        }
    }

    public static void prepareNodeList()
    {
        serverStatus.put(new ServerInfo("127.0.0.1", 7001), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7002), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7003), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7004), new Date());

        serverStatus.put(new ServerInfo("127.0.0.1", 7102), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7103), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7104), new Date());

        serverStatus.put(new ServerInfo("127.0.0.1", 7201), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7202), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7203), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7204), new Date());

        serverStatus.put(new ServerInfo("127.0.0.1", 7301), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7302), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7303), new Date());
        serverStatus.put(new ServerInfo("127.0.0.1", 7304), new Date());
    }

    public static Schedule getDataToSchedule()
    {
        double[][] tasksData, machinesData;
        tasksData = GenerateSimulationData.getTasks();
        machinesData = GenerateSimulationData.getMachines();

        ArrayList<Task> tasks = new ArrayList<>();
        for(int i=0; i<tasksData.length;i++)
        {
            tasks.add(new Task(i+1,tasksData[i][0], tasksData[i][1]));
        }

        ArrayList<Machine> machines = new ArrayList<>();
        for(int i=0; i<machinesData.length;i++)
        {
            machines.add(new Machine(i+1,machinesData[i][0], machinesData[i][1]));
        }

        Schedule schedule = new HSGASchedule(tasks, machines);
        makespan.add(schedule.getMakespan());
        flowtime.add(schedule.getFlowtime());
        economicCost.add(schedule.getEconomicCost());
        resourceUtilization.add(schedule.getResourceUtilization());
        return null;
    }
}

