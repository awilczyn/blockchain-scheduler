package blockchain;

import blockchain.scheduler.RoundRobin.RoundRobinScheduler;
import blockchain.scheduler.utils.GenerateSimulationData;

public class RoundRobin
{
    private static double[] tasks, machines;

    public static void main(String[] args)
    {
        new GenerateSimulationData();
        tasks = GenerateSimulationData.getTasks();
        machines = GenerateSimulationData.getMachines();
        RoundRobinScheduler scheduler = new RoundRobinScheduler(tasks, machines);
        scheduler.schedule();
    }
}
