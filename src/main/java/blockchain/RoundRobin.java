package blockchain;

import blockchain.scheduler.Machine;
import blockchain.scheduler.RoundRobinSchedule;
import blockchain.scheduler.Task;
import blockchain.scheduler.utils.Constants;
import blockchain.scheduler.utils.GenerateSimulationData;

import java.util.ArrayList;

public class RoundRobin
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

        RoundRobinSchedule scheduler;
        double sumTime = 0;
        double securityLevel = 0;
        double counter = 0;
        for(int i = 0; i< Constants.NO_OF_ATTEMPTS; i++) {
            scheduler = new RoundRobinSchedule(tasks, machines);
            if (scheduler.getSecurityLevel() >= Constants.SECURITY_LEVEL) {
                counter++;
                sumTime = sumTime + scheduler.getMakespan();
                securityLevel = securityLevel + scheduler.getSecurityLevel();
            }
        }
        System.out.println("Average makespan: "+sumTime/counter);
        System.out.println("Average security level: "+securityLevel/counter);
    }
}
