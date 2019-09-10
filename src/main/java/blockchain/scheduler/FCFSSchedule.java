package blockchain.scheduler;

import blockchain.scheduler.FCFS.FCFSScheduler;
import blockchain.scheduler.utils.GenerateSimulationData;
import org.cloudbus.cloudsim.Cloudlet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrzejwilczynski on 08/01/2019.
 */
public class FCFSSchedule extends Schedule
{
    public FCFSSchedule(ArrayList<Task> tasks, ArrayList<Machine> machines)
    {
        super(tasks, machines);
        this.prepareSchedule();
    }

    public void prepareSchedule()
    {
        FCFSScheduler scheduler = new FCFSScheduler();
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
        System.out.println("FCFS makespan: "+ maxFinishTime);
        this.makespan = maxFinishTime;
        this.securityLevel = this.calculateSecurityLevel();
    }
}
