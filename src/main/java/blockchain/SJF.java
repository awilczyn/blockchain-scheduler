package blockchain;

import blockchain.scheduler.SJF.SJFScheduler;
import blockchain.scheduler.utils.GenerateSimulationData;

public class SJF
{
    private static double[] tasks, machines;

    public static void main(String[] args)
    {
        new GenerateSimulationData();
        tasks = GenerateSimulationData.getTasks();
        machines = GenerateSimulationData.getMachines();
        SJFScheduler scheduler = new SJFScheduler(tasks, machines);
        scheduler.schedule();
    }
}
