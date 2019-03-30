package blockchain.scheduler;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by andrzejwilczynski on 08/01/2019.
 */
public class Machine implements Serializable
{
    public float numberOfOperationsPerSecond;
    public ArrayList<Integer> tasksToExecute;

    public Machine(float numberOfOperationsPerSecond)
    {
        this.numberOfOperationsPerSecond = numberOfOperationsPerSecond;
        this.tasksToExecute = new ArrayList<>();
    }

    public void addTaskToExecute(int taskId)
    {
        tasksToExecute.add(taskId);
    }

    public float getNumberOfOperationsPerSecond() {
        return numberOfOperationsPerSecond;
    }
}
