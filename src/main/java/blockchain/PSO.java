package blockchain;

import blockchain.scheduler.PSO.PSOScheduler;
import blockchain.scheduler.utils.GenerateSimulationData;

public class PSO
{
    private static double[] tasks, machines;

    public static void main(String[] args)
    {
        new GenerateSimulationData();
        tasks = GenerateSimulationData.getTasks();
        machines = GenerateSimulationData.getMachines();
        PSOScheduler scheduler = new PSOScheduler(tasks, machines);
        scheduler.schedule();
    }
}
