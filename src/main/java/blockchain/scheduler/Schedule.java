package blockchain.scheduler;

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
    public float time;

    public Schedule() {}

    public Schedule(ArrayList<Task> tasks, ArrayList<Machine> machines)
    {
        this.tasks = tasks;
        this.machines = machines;
    }

    public float getTime() {
        return time;
    }

    public void prepareSchedule()
    {
        if (this.machines.size() > 0) {
            for(int i=0; i<this.tasks.size();i++)
            {
                Machine machine = getRandomMachine(this.machines);
                machine.addTaskToExecute(tasks.get(i).id);
            }
            this.time = getRandomTime();
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
            numberOfInstruction += tasks.get(i).getNumberOfOperations();
        }
        return numberOfInstruction;
    }
}
