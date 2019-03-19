package blockchain.scheduler;

import java.io.Serializable;

/**
 * Created by andrzejwilczynski on 08/01/2019.
 */
public class Machine implements Serializable
{
    public float numberOfOperationsPerSecond;
    public int tasksToExecute [];

    public Machine(float numberOfOperationsPerSecond)
    {
        this.numberOfOperationsPerSecond = numberOfOperationsPerSecond;
    }

    public void setTasksToExecute(int[] tasksToExecute)
    {
        this.tasksToExecute = tasksToExecute;
    }

    public float getNumberOfOperationsPerSecond() {
        return numberOfOperationsPerSecond;
    }
}
