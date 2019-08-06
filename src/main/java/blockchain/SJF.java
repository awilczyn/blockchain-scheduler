package blockchain;

import blockchain.scheduler.Machine;
import blockchain.scheduler.SJFSchedule;
import blockchain.scheduler.Task;
import blockchain.scheduler.utils.Constants;
import blockchain.scheduler.utils.GenerateSimulationData;

import java.util.ArrayList;

public class SJF
{
    public static void main(String[] args)
    {
        double[] tasksData, machinesData;
        new GenerateSimulationData();
        tasksData = GenerateSimulationData.getTasks();
        machinesData = GenerateSimulationData.getMachines();

        ArrayList<Task> tasks = new ArrayList<>();
        for(int i=0; i<tasksData.length;i++)
        {
            tasks.add(new Task(i+1,tasksData[i]));
        }

        ArrayList<Machine> machines = new ArrayList<>();
        for(int i=0; i<machinesData.length;i++)
        {
            machines.add(new Machine(i+1,machinesData[i]));
        }

        SJFSchedule scheduler;
        double sumTime = 0;
        for(int i = 0; i< Constants.NO_OF_ATTEMPTS; i++) {
            scheduler = new SJFSchedule(tasks, machines);
            sumTime = sumTime + scheduler.getTime();
        }
        System.out.println("Average makespan: "+sumTime/Constants.NO_OF_ATTEMPTS);
    }
}
