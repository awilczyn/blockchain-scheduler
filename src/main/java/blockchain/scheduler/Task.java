package blockchain.scheduler;

import java.io.Serializable;

/**
 * Created by andrzejwilczynski on 08/01/2019.
 */
public class Task implements Serializable
{
    public int id;
    public double numberOfOperations;

    public Task(int id, double numberOfOperations)
    {
        this.id = id;
        this.numberOfOperations = numberOfOperations;
    }

    public double getNumberOfOperations() {
        return numberOfOperations;
    }
}
