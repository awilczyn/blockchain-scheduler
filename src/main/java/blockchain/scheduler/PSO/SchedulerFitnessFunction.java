package blockchain.scheduler.PSO;

import blockchain.scheduler.utils.Constants;
import blockchain.scheduler.utils.GenerateSimulationData;
import net.sourceforge.jswarm_pso.FitnessFunction;

public class SchedulerFitnessFunction extends FitnessFunction {
    private static double[] tasks;

    SchedulerFitnessFunction() {
        super(false);
        tasks = GenerateSimulationData.getTasks();
    }

    @Override
    public double evaluate(double[] position) {
        double alpha = 0.3;
        return alpha * calcTotalTime(position) + (1 - alpha) * calcMakespan(position);
//        return calcMakespan(position);
    }

    private double calcTotalTime(double[] position) {
        double totalCost = 0;
        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            int dcId = (int) position[i];
            totalCost += tasks[i];
        }
        return totalCost;
    }

    public double calcMakespan(double[] position) {
        double makespan = 0;
        double[] dcWorkingTime = new double[Constants.NO_OF_VMS];

        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            int dcId = (int) position[i];
            if(dcWorkingTime[dcId] != 0) --dcWorkingTime[dcId];
            dcWorkingTime[dcId] += tasks[i];
            makespan = Math.max(makespan, dcWorkingTime[dcId]);
        }
        return makespan;
    }
}
