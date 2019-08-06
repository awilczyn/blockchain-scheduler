package blockchain.scheduler;

import blockchain.scheduler.RoundRobin.RoundRobinScheduler;
import blockchain.scheduler.utils.GenerateSimulationData;
import org.cloudbus.cloudsim.Cloudlet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrzejwilczynski on 08/01/2019.
 */
public class RoundRobinSchedule extends Schedule
{
    public RoundRobinSchedule(ArrayList<Task> tasks, ArrayList<Machine> machines)
    {
        super(tasks, machines);
        this.prepareSchedule();
    }

    public void prepareSchedule()
    {
        RoundRobinScheduler scheduler = new RoundRobinScheduler(
                GenerateSimulationData.getTasks(),
                GenerateSimulationData.getMachines()
        );
        List<Cloudlet> list = scheduler.schedule();
        Cloudlet cloudlet;
        Machine machine;
        double maxFinishTime = 0;
        for (int i = 0; i < list.size(); i++) {
            cloudlet = list.get(i);
            machine = this.machines.get(cloudlet.getVmId());
            machine.addTaskToExecute(cloudlet.getCloudletId()+1);
            maxFinishTime = Math.max(maxFinishTime, cloudlet.getFinishTime());
        }
        System.out.println("Round Robin makespan: "+ maxFinishTime);
        this.time = maxFinishTime;
    }
}
