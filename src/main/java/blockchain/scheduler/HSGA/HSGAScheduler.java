package blockchain.scheduler.HSGA;

import blockchain.scheduler.utils.Constants;
import blockchain.scheduler.utils.DatacenterCreator;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.*;

public class HSGAScheduler {
    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmList;
    private static Datacenter[] datacenter;
    private double[] tasks, machines;

    public HSGAScheduler(double[] tasks, double[] machines)
    {
        this.tasks = tasks;
        this.machines = machines;
    }

    private List<Vm> createVM(int userId, int vms) {
        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<Vm> list = new LinkedList<Vm>();

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 4096; //vm memory (MB)
        long bw = 1000;
        int pesNumber = 4; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        Vm[] vm = new Vm[vms];

        for (int i = 0; i < vms; i++) {
            double mips =  machines[i];
            vm[i] = new Vm(i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }

        return list;
    }

    private List<Cloudlet> createCloudlet(int userId, int cloudlets, int idShift) {
        // Creates a container to store Cloudlets
        LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

        //cloudlet parameters
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet[] cloudlet = new Cloudlet[cloudlets];

        for (int i = 0; i < cloudlets; i++) {
            int dcId = (int) (Math.random() * Constants.NO_OF_VMS);
            long length = (long) tasks[i];
            cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            // setting the owner of these Cloudlets
            cloudlet[i].setUserId(userId);
            cloudlet[i].setVmId(dcId);
            list.add(cloudlet[i]);
        }
        return list;
    }

    public List<Cloudlet> schedule()
    {
        Log.printLine("Starting HSGA Scheduler...");

        try {
            int num_user = 1;   // number of grid users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;  // mean trace events

            CloudSim.init(num_user, calendar, trace_flag);

            // Second step: Create Datacenters
            datacenter = new Datacenter[Constants.NO_OF_DATA_CENTERS];
            for (int i = 0; i < Constants.NO_OF_DATA_CENTERS; i++) {
                datacenter[i] = DatacenterCreator.createDatacenter("Datacenter_" + i);
            }

            //Third step: Create Broker
            HSGADatacenterBroker broker = createBroker("Broker_0");
            int brokerId = broker.getId();

            //Fourth step: Create VMs and Cloudlets and send them to broker
            vmList = createVM(brokerId, Constants.NO_OF_VMS);
            cloudletList = createCloudlet(brokerId, Constants.NO_OF_TASKS, 0);

            List<Cloudlet> sortedList = new ArrayList<>();
            for(Cloudlet cloudlet:cloudletList){
                sortedList.add(cloudlet);
            }
            int numCloudlets=sortedList.size();
            for(int i=0;i<numCloudlets;i++){
                Cloudlet tmp=sortedList.get(i);
                int idx=i;
                for(int j=i+1;j<numCloudlets;j++)
                {
                    if(sortedList.get(j).getCloudletLength()<tmp.getCloudletLength())
                    {
                        idx=j;
                        tmp=sortedList.get(j);
                    }
                }
                Cloudlet tmp2 = sortedList.get(i);
                sortedList.set(i, tmp);
                sortedList.set(idx,tmp2);
            }

            ArrayList<Vm> sortedListVm = new ArrayList<Vm>();
            ArrayList<Vm> toBeUsedVm = new ArrayList<Vm>();
            ArrayList<Vm> leftOutVm = new ArrayList<Vm>();
            for(Vm vm: vmList){
                sortedListVm.add(vm);
            }
            int numVms=sortedListVm.size();

            for(int i=0;i<numVms;i++){
                Vm tmp=sortedListVm.get(i);
                int idx=i;
                if(i<numCloudlets)
                    toBeUsedVm.add(tmp);
                else
                    leftOutVm.add(tmp);
                for(int j=i+1;j<numVms;j++)
                {
                    if(sortedListVm.get(j).getMips()>tmp.getMips())
                    {
                        idx=j;
                        tmp=sortedListVm.get(j);
                    }
                }
                Vm tmp2 = sortedListVm.get(i);
                sortedListVm.set(i, tmp);
                sortedListVm.set(idx,tmp2);
            }
            ArrayList<Chromosomes> initialPopulation = new ArrayList<Chromosomes>();
            for(int j=0;j<numCloudlets;j++)
            {
                ArrayList<Gene> firstChromosome = new ArrayList<Gene>();

                for(int i=0;i<numCloudlets;i++)
                {
                    int k=(i+j)%numVms;
                    k=(k+numCloudlets)%numCloudlets;
                    Gene geneObj = new Gene(sortedList.get(i),sortedListVm.get(k));
                    firstChromosome.add(geneObj);
                }
                Chromosomes chromosome = new Chromosomes(firstChromosome);
                initialPopulation.add(chromosome);
            }

            int populationSize=initialPopulation.size();
            Random random = new Random();
            for(int itr=0;itr<20;itr++)
            {
                int index1,index2;
                index1=random.nextInt(populationSize) % populationSize;
                index2=random.nextInt(populationSize) % populationSize;
                ArrayList<Gene> l1= new ArrayList<Gene>();
                l1=initialPopulation.get(index1).getGeneList();
                Chromosomes chromosome1 = new Chromosomes(l1);
                ArrayList<Gene> l2= new ArrayList<Gene>();
                l2=initialPopulation.get(index2).getGeneList();
                Chromosomes chromosome2 = new Chromosomes(l2);
                double rangeMin = 0.0f;
                double rangeMax = 1.0f;
                Random r = new Random();
                double crossProb = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
                if(crossProb<0.5)
                {
                    int i,j;
                    i=random.nextInt(numCloudlets) % numCloudlets;
                    j=random.nextInt(numCloudlets) % numCloudlets;
                    Vm vm1 = l1.get(i).getVmFromGene();
                    Vm vm2 = l2.get(j).getVmFromGene();
                    chromosome1.updateGene(i, vm2);
                    chromosome2.updateGene(j, vm1);
                    initialPopulation.set(index1, chromosome1);
                    initialPopulation.set(index2, chromosome2);
                }
                double mutProb = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
                if(mutProb<0.5)
                {
                    int i;
                    i=random.nextInt(populationSize) % populationSize;
                    ArrayList<Gene> l= new ArrayList<Gene>();
                    l=initialPopulation.get(i).getGeneList();
                    Chromosomes mutchromosome = new Chromosomes(l);
                    int j;
                    j=random.nextInt(numCloudlets) % numCloudlets;
                    Vm vm1 = sortedListVm.get(0);
                    mutchromosome.updateGene(j,vm1);
                }
            }
            int fittestIndex=0;
            double time=1000000;

            for(int i=0;i<populationSize;i++)
            {
                ArrayList<Gene> l= new ArrayList<Gene>();
                l=initialPopulation.get(i).getGeneList();
                double sum=0;
                for(int j=0;j<numCloudlets;j++)
                {
                    Gene g = l.get(j);
                    Cloudlet c = g.getCloudletFromGene();
                    Vm v = g.getVmFromGene();
                    double temp = c.getCloudletLength()/v.getMips();
                    sum+=temp;
                }
                if(sum<time)
                {
                    time=sum;
                    fittestIndex=i;
                }
            }

            ArrayList<Gene> result = new ArrayList<Gene>();
            result = initialPopulation.get(fittestIndex).getGeneList();

            List<Cloudlet> finalcloudletList = new ArrayList<Cloudlet>();
            List<Vm> finalvmlist = new ArrayList<Vm>();


            for(int i=0;i<result.size();i++)
            {
                finalcloudletList.add(result.get(i).getCloudletFromGene());
                finalvmlist.add(result.get(i).getVmFromGene());
                Vm vm=result.get(i).getVmFromGene();
                //Log.printLine("############### VM FROM GENE  "+vm.getId());
            }

            broker.submitVmList(finalvmlist);
            broker.submitCloudletList(finalcloudletList);

            // Fifth step: Starts the simulation
            CloudSim.startSimulation();

            // Final step: Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            //newList.addAll(globalBroker.getBroker().getCloudletReceivedList());

            CloudSim.stopSimulation();

            Log.printLine(HSGAScheduler.class.getName() + " finished!");
            return newList;
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
        return null;
    }

    private static HSGADatacenterBroker createBroker(String name) throws Exception {
        return new HSGADatacenterBroker(name);
    }
}
