package blockchain.scheduler;

/**
 * Created by andrzejwilczynski on 08/01/2019.
 */
public class Machine
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
}
