package blockchain.scheduler;

import java.io.Serializable;
import java.util.ArrayList;

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
}
