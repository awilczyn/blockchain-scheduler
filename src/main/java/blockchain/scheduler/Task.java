package blockchain.scheduler;

import java.io.Serializable;

/**
 * Created by andrzejwilczynski on 08/01/2019.
 */
public class Task implements Serializable
{
    public int id;
    public float numberOfOperations;

    public Task(int id, float numberOfOperations)
    {
        this.id = id;
        this.numberOfOperations = numberOfOperations;
    }

    public float getNumberOfOperations() {
        return numberOfOperations;
    }
}
