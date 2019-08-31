package blockchain.scheduler;

import blockchain.scheduler.utils.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by andrzejwilczynski on 08/01/2019.
 */
public class Schedule implements Serializable
{
    public ArrayList<Task> tasks = new ArrayList<>();
    public ArrayList<Machine> machines = new ArrayList<>();
    public double makespan;
    public double securityLevel;

    public Schedule() {}

    public Schedule(ArrayList<Task> tasks, ArrayList<Machine> machines)
    {
        this.tasks = tasks;
        this.machines = machines;
    }

    public double getMakespan() {
        return makespan;
    }

    public void prepareSchedule()
    {
        if (this.machines.size() > 0) {
            for(int i=0; i<this.tasks.size();i++)
            {
                Machine machine = getRandomMachine(this.machines);
                machine.addTaskToExecute(tasks.get(i).id);
            }
            this.makespan = getRandomTime();
        }
    }

    public Machine getRandomMachine(ArrayList<Machine> items) {
        return items.get(new Random().nextInt(items.size()));
    }

    public int getRandomTime()
    {
        return this.tasks.size() + (int)(Math.random() * getSumOfInstruction()/1000);
    }

    public float getSumOfInstruction()
    {
        float numberOfInstruction = 0;
        for(int i=0; i<this.tasks.size();i++)
        {
            numberOfInstruction += tasks.get(i).getWorkload();
        }
        return numberOfInstruction;
    }

    public double getSecurityLevel()
    {
        return securityLevel;
    }

    public double calculateSecurityLevel()
    {
        double Pfailure = 0;
        double localPfailure = 0;
        for (Machine machine : machines) {
            for (Integer taskId : machine.tasksToExecute) {
                Task task = this.tasks.get(taskId-1);
                if (task.securityDemand > machine.trustLevel) {
                    localPfailure = 1 - Math.pow(Math.E, -Constants.FAILURE_COEFFICIENT*(task.securityDemand - machine.trustLevel));
                    Pfailure = Pfailure + localPfailure;
                }
            }
        }
        return  1-(Pfailure/tasks.size());
    }
}
