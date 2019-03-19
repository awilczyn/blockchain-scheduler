package blockchain.scheduler;

import java.util.ArrayList;

/**
 * Created by andrzejwilczynski on 08/01/2019.
 */
public class AwsSchedule extends Schedule
{
    public AwsSchedule(){
        super(null, null);
    }

    public AwsSchedule(ArrayList<Task> tasks, ArrayList<Machine> machines)
    {
        super(tasks, machines);
        this.prepareSchedule();
    }

    public void prepareSchedule()
    {
        if (this.machines.size() > 0) {
            int[] tasksForMachine1 = {1, 3};
            this.machines.get(0).setTasksToExecute(tasksForMachine1);
            int[] tasksForMachine2 = {2};
            this.machines.get(1).setTasksToExecute(tasksForMachine2);
            int[] tasksForMachine3 = {4};
            this.machines.get(2).setTasksToExecute(tasksForMachine3);
            this.time = 15;
        }
    }
}
