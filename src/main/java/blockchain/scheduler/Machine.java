package blockchain.scheduler;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by andrzejwilczynski on 08/01/2019.
 */
public class Machine implements Serializable
{
    public int id;
    public double numberOfOperationsPerSecond;
    public ArrayList<Integer> tasksToExecute;

    public Machine(int id, double numberOfOperationsPerSecond)
    {
        this.id = id;
        this.numberOfOperationsPerSecond = numberOfOperationsPerSecond;
        this.tasksToExecute = new ArrayList<>();
    }

    public void addTaskToExecute(int taskId)
    {
        tasksToExecute.add(taskId);
    }

    public double getNumberOfOperationsPerSecond() {
        return numberOfOperationsPerSecond;
    }

    public int getId()
    {
        return id;
    }
}
