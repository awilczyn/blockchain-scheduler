package blockchain.scheduler;

import blockchain.scheduler.utils.Constants;
import org.cloudbus.cloudsim.Cloudlet;

import java.io.Serializable;
import java.util.*;

/**
 * Created by andrzejwilczynski on 08/01/2019.
 */
public class Schedule implements Serializable
{
    public ArrayList<Task> tasks = new ArrayList<>();
    public ArrayList<Machine> machines = new ArrayList<>();
    public double makespan;
    public double securityLevel;
    public double flowtime;
    public double economicCost;
    public double resourceUtilization;

    public Schedule() {}

    public Schedule(ArrayList<Task> tasks, ArrayList<Machine> machines)
    {
        this.tasks = tasks;
        this.machines = machines;
    }

    public double getCriterion()
    {
        return makespan;
    }

    public double getMakespan() {
        return makespan;
    }

    public void prepareSchedule()
    {
        if (this.machines.size() > 0) {
            for(int i=0; i<this.tasks.size();i++)
            {
                Machine machine = getRandomMachine(this.machines);
                machine.addTaskToExecute(tasks.get(i).id);
            }
            this.makespan = getRandomTime();
        }
    }

    public Machine getRandomMachine(ArrayList<Machine> items) {
        return items.get(new Random().nextInt(items.size()));
    }

    public int getRandomTime()
    {
        return this.tasks.size() + (int)(Math.random() * getSumOfInstruction()/1000);
    }

    public float getSumOfInstruction()
    {
        float numberOfInstruction = 0;
        for(int i=0; i<this.tasks.size();i++)
        {
            numberOfInstruction += tasks.get(i).getWorkload();
        }
        return numberOfInstruction;
    }

    public double getSecurityLevel()
    {
        return securityLevel;
    }

    public double calculateSecurityLevel()
    {
        double Pfailure = 0;
        double localPfailure = 0;
        double counter = 0;
        for (Machine machine : machines) {
            for (Integer taskId : machine.tasksToExecute) {
                Task task = this.tasks.get(taskId-1);
                if (task.securityDemand > machine.trustLevel) {
                    counter++;
                    localPfailure = 1 - Math.pow(Math.E, -Constants.FAILURE_COEFFICIENT*(task.securityDemand - machine.trustLevel));
                    Pfailure = Pfailure + localPfailure;
                }
            }
        }
        return  1-(Pfailure/counter);
    }

    public double getFlowtime() {
        return flowtime;
    }

    public double getEconomicCost() {
        return economicCost;
    }

    public double getResourceUtilization() {
        return resourceUtilization;
    }

    protected void calculateRestMetrics(List<Cloudlet> list)
    {
        double economicCost = 0;
        double resourceUtilizationTime = 0;
        HashMap<Integer, Double> hmap = new HashMap<Integer, Double>();
        for (int i = 0; i < list.size(); i++) {
            if (hmap.get(list.get(i).getVmId()) != null && hmap.get(list.get(i).getVmId()) < list.get(i).getFinishTime()) {
                hmap.replace(list.get(i).getVmId(), list.get(i).getFinishTime());
            } else if (hmap.get(list.get(i).getVmId()) == null ) {
                hmap.put(list.get(i).getVmId(), list.get(i).getFinishTime());
            }
        }
        Iterator it = hmap.entrySet().iterator();
        int counter = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            economicCost = economicCost + (double)pair.getValue() * Constants.COST_OF_UTILIZATION;
            resourceUtilizationTime = resourceUtilizationTime + (double)pair.getValue();
            counter++;
            it.remove(); // avoids a ConcurrentModificationException
        }
        this.resourceUtilization = resourceUtilizationTime/(this.makespan*counter);
        this.economicCost = economicCost;
    }
}
