package blockchain;

import blockchain.scheduler.FCFSSchedule;
import blockchain.scheduler.Machine;
import blockchain.scheduler.Task;
import blockchain.scheduler.utils.Constants;
import blockchain.scheduler.utils.GenerateSimulationData;

import java.util.ArrayList;

public class FCFS
{
    public static void main(String[] args)
    {
        double[][] tasksData, machinesData;
        new GenerateSimulationData();
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

        FCFSSchedule scheduler;
        double sumTime = 0;
        for(int i=0; i< Constants.NO_OF_ATTEMPTS; i++) {
            scheduler = new FCFSSchedule(tasks, machines);
            sumTime = sumTime + scheduler.getMakespan();
        }
        System.out.println("Average makespan: "+sumTime/Constants.NO_OF_ATTEMPTS);
    }
}
