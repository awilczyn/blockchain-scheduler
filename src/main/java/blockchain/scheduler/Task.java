package blockchain.scheduler;

/**
 * Created by andrzejwilczynski on 08/01/2019.
 */
public class Task
{
    public int id;
    public float numberOfOperations;

    public Task(int id, float numberOfOperations)
    {
        this.id = id;
        this.numberOfOperations = numberOfOperations;
    }
}
