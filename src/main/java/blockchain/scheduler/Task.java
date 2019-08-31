package blockchain.scheduler;

import java.io.Serializable;

/**
 * Created by andrzejwilczynski on 08/01/2019.
 */
public class Task implements Serializable
{
    public int id;
    public double workload;
    public double securityDemand;

    public Task(int id, double workload, double securityDemand)
    {
        this.id = id;
        this.workload = workload;
        this.securityDemand = securityDemand;
    }

    public double getWorkload() {
        return workload;
    }

    public double getSecurityDemand()
    {
        return securityDemand;
    }
}
