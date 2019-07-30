package blockchain.scheduler;

import blockchain.scheduler.RoundRobin.RoundRobinScheduler;

import java.util.ArrayList;

/**
 * Created by andrzejwilczynski on 08/01/2019.
 */
public class RoundRobinSchedule extends Schedule
{
    public RoundRobinSchedule(){
        super(null, null);
    }

    public RoundRobinSchedule(ArrayList<Task> tasks, ArrayList<Machine> machines)
    {
        super(tasks, machines);
        this.prepareSchedule();
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
}
