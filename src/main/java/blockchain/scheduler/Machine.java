package blockchain.scheduler;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by andrzejwilczynski on 08/01/2019.
 */
public class Machine implements Serializable
{
    public int id;
    public double computingCapacity;
    public double trustLevel;
    public ArrayList<Integer> tasksToExecute;

    public Machine(int id, double numberOfOperationsPerSecond, double trustLevel)
    {
        this.id = id;
        this.computingCapacity = numberOfOperationsPerSecond;
        this.trustLevel = trustLevel;
        this.tasksToExecute = new ArrayList<>();
    }

    public void addTaskToExecute(int taskId)
    {
        tasksToExecute.add(taskId);
    }

    public double getComputingCapacity() {
        return computingCapacity;
    }

    public int getId()
    {
        return id;
    }

    public double getTrustLevel()
    {
        return trustLevel;
    }
}
