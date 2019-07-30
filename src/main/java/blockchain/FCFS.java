package blockchain;

import blockchain.scheduler.FCFS.FCFSScheduler;
import blockchain.scheduler.utils.GenerateSimulationData;

public class FCFS
{
    private static double[] tasks, machines;

    public static void main(String[] args)
    {
        new GenerateSimulationData();
        tasks = GenerateSimulationData.getTasks();
        machines = GenerateSimulationData.getMachines();
        FCFSScheduler scheduler = new FCFSScheduler(tasks, machines);
        scheduler.schedule();
    }
}
