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
}
