package blockchain;

import blockchain.scheduler.*;
import blockchain.scheduler.utils.Constants;
import blockchain.scheduler.utils.GenerateSimulationData;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class HSGA {
    public static void main(String[] args)
    {
        double[][] tasksData, machinesData;
        new GenerateSimulationData();
        tasksData = GenerateSimulationData.getTasks();
        machinesData = GenerateSimulationData.getMachines();

        ArrayList<Task> tasks = new ArrayList<>();
        for(int i=0; i<tasksData.length;i++)
        {
            tasks.add(new Task(i+1,tasksData[i][0], tasksData[i][1]));
        }

        ArrayList<Machine> machines = new ArrayList<>();
        for(int i=0; i<machinesData.length;i++)
        {
            machines.add(new Machine(i+1,machinesData[i][0], machinesData[i][1]));
        }

        HSGASchedule scheduler;
        ArrayList<Double> makespan = new ArrayList<Double>();
        ArrayList<Double> flowtime = new ArrayList<Double>();
        ArrayList<Double> economicCost = new ArrayList<Double>();
        ArrayList<Double> resourceUtilization = new ArrayList<Double>();
        ArrayList<Double> securityLevel = new ArrayList<Double>();
        for(int i = 0; i< Constants.NO_OF_ATTEMPTS; i++) {
            scheduler = new HSGASchedule(tasks, machines);
            scheduler.setPfake(0.5);
            scheduler.setPhacking(0.5);
            scheduler.calculateSecurityLevel();
            if (scheduler.getSecurityLevel() >= Constants.SECURITY_LEVEL) {
                makespan.add(scheduler.getMakespan());
                flowtime.add(scheduler.getFlowtime());
                economicCost.add(scheduler.getEconomicCost());
                resourceUtilization.add(scheduler.getResourceUtilization());
                securityLevel.add(scheduler.getSecurityLevel());
            }
        }
        double[] makespanArray = new double[makespan.size()];
        for (int i = 0; i < makespan.size(); i++) {
            makespanArray[i] = makespan.get(i).doubleValue();
        }
        double[] flowtimeArray = new double[flowtime.size()];
        for (int i = 0; i < flowtime.size(); i++) {
            flowtimeArray[i] = flowtime.get(i).doubleValue();
        }
        double[] economicCostArray = new double[economicCost.size()];
        for (int i = 0; i < economicCost.size(); i++) {
            economicCostArray[i] = economicCost.get(i).doubleValue();
        }
        double[] resourceUtilizationArray = new double[resourceUtilization.size()];
        for (int i = 0; i < resourceUtilization.size(); i++) {
            resourceUtilizationArray[i] = resourceUtilization.get(i).doubleValue();
        }
        double[] securityLevelArray = new double[securityLevel.size()];
        for (int i = 0; i < securityLevel.size(); i++) {
            securityLevelArray[i] = securityLevel.get(i).doubleValue();
        }
        DescriptiveStatistics daMakespan = new DescriptiveStatistics(makespanArray);
        Median medianMakespan = new Median();
        DescriptiveStatistics daFlowtime = new DescriptiveStatistics(flowtimeArray);
        Median medianFlowtime = new Median();
        DescriptiveStatistics daEconomicCost = new DescriptiveStatistics(economicCostArray);
        Median medianEconomicCost = new Median();
        DescriptiveStatistics daResourceUtilization = new DescriptiveStatistics(resourceUtilizationArray);
        Median medianResourceUtilization = new Median();
        DescriptiveStatistics daSecurityLevel = new DescriptiveStatistics(securityLevelArray);
        Median medianSecurityLevel = new Median();
        DecimalFormat df = new DecimalFormat("#####0.000");
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        dfs.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(dfs);
        System.out.println("Criterion Min, Q1, Median, Quartile 3, Max");
        System.out.println("makespan: "+
                df.format(daMakespan.getMin()) + ";" +
                df.format(daMakespan.getPercentile(25))+";"+
                df.format(medianMakespan.evaluate(makespanArray)) + ";" +
                df.format(daMakespan.getPercentile(75)) + ";" +
                df.format(daMakespan.getMax()));
        System.out.println("flowtime: "+
                df.format(daFlowtime.getMin()) + ";" +
                df.format(daFlowtime.getPercentile(25))+";"+
                df.format(medianFlowtime.evaluate(flowtimeArray)) + ";" +
                df.format(daFlowtime.getPercentile(75)) + ";" +
                df.format(daFlowtime.getMax()));
        System.out.println("economic cost: "+
                df.format(daEconomicCost.getMin()) + ";" +
                df.format(daEconomicCost.getPercentile(25))+";"+
                df.format(medianEconomicCost.evaluate(economicCostArray)) + ";" +
                df.format(daEconomicCost.getPercentile(75)) + ";" +
                df.format(daEconomicCost.getMax()));
        System.out.println("resource utilization: "+
                df.format(daResourceUtilization.getMin()) + ";" +
                df.format(daResourceUtilization.getPercentile(25))+";"+
                df.format(medianResourceUtilization.evaluate(resourceUtilizationArray)) + ";" +
                df.format(daResourceUtilization.getPercentile(75)) + ";" +
                df.format(daResourceUtilization.getMax()));
        System.out.println("security level: "+
                df.format(daSecurityLevel.getMin()) + ";" +
                df.format(daSecurityLevel.getPercentile(25))+";"+
                df.format(medianSecurityLevel.evaluate(securityLevelArray)) + ";" +
                df.format(daSecurityLevel.getPercentile(75)) + ";" +
                df.format(daSecurityLevel.getMax()));
    }
}
