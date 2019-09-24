package blockchain.scheduler.utils;

import org.apache.commons.math3.distribution.NormalDistribution;

import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

public class GenerateSimulationData
{
    private static double[][] tasks, machines;
    private File taskFile = new File("Tasks.txt");
    private File machineFile = new File("Machines.txt");

    public GenerateSimulationData() {
        tasks = new double[Constants.NO_OF_TASKS][2];
        machines = new double[Constants.NO_OF_VMS][2];
        try {
            if (taskFile.exists()) {
                readWorkload();
            } else {
                initWorkload();
            }
            if (machineFile.exists()) {
                readComputingCapacity();
            } else {
                initComputingCapacity();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initWorkload() throws IOException {
        System.out.println("Initializing new tasks...");
        BufferedWriter taskBufferedWriter = new BufferedWriter(new FileWriter(taskFile));

        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.CEILING);
        DecimalFormat df2 = new DecimalFormat("#.##");
        df2.setRoundingMode(RoundingMode.CEILING);
        double standardDeviation = 300;
        double mean = 600;
        NormalDistribution dist = new NormalDistribution(mean, standardDeviation);
        double standardDeviationSD = 0.15;
        double meanSD = 0.8;
        NormalDistribution distSD = new NormalDistribution(meanSD, standardDeviationSD);
        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            tasks[i][0] = this.getValueInRange(dist, 100, 1000);
            tasks[i][1] = this.getValueInRange(distSD, 0.6, 0.9);
            taskBufferedWriter.write(String.valueOf(df.format(tasks[i][0])) + ' ');
            taskBufferedWriter.write(String.valueOf(df2.format(tasks[i][1])) + ' ');
            taskBufferedWriter.write('\n');
        }
        taskBufferedWriter.close();
    }

    private void initComputingCapacity() throws IOException {
        System.out.println("Initializing new machines...");
        BufferedWriter machineBufferedWriter = new BufferedWriter(new FileWriter(machineFile));

        DecimalFormat df = new DecimalFormat("#.######");
        df.setRoundingMode(RoundingMode.CEILING);
        DecimalFormat df2 = new DecimalFormat("#.##");
        df2.setRoundingMode(RoundingMode.CEILING);
        double standardDeviation = 2;
        double mean = 7;
        NormalDistribution dist = new NormalDistribution(mean, standardDeviation);
        double standardDeviationTL = 0.2;
        double meanTL = 0.5;
        NormalDistribution distTl = new NormalDistribution(meanTL, standardDeviationTL);
        for (int i = 0; i < Constants.NO_OF_VMS; i++) {
            machines[i][0] = this.getValueInRange(dist, 1, 12);
            machines[i][1] = this.getValueInRange(distTl, 0.2, 1);
            machineBufferedWriter.write(String.valueOf(df.format(machines[i][0])) + ' ');
            machineBufferedWriter.write(String.valueOf(df2.format(machines[i][1])) + ' ');
            machineBufferedWriter.write('\n');
        }

        machineBufferedWriter.close();
    }

    private void readWorkload() throws IOException {
        System.out.println("Reading the tasks...");
        BufferedReader execBufferedReader = new BufferedReader(new FileReader(taskFile));

        int i = 0;
        int j = 0;
        do {
            String line = execBufferedReader.readLine();
            j = 0;
            for (String num : line.split(" ")) {
                tasks[i][j] = new Double(num);
                ++j;
            }
            ++i;
        } while (execBufferedReader.ready());
    }

    private void readComputingCapacity() throws IOException {
        System.out.println("Reading the machines...");
        BufferedReader capacityBufferedReader = new BufferedReader(new FileReader(machineFile));

        int i = 0;
        int j = 0;
        do {
            String line = capacityBufferedReader.readLine();
            j = 0;
            for (String num : line.split(" ")) {
                machines[i][j] = new Double(num);
                ++j;
            }
            ++i;
        } while (capacityBufferedReader.ready());
    }

    public static double[][] getTasks() {
        return tasks;
    }

    public static double[][] getMachines() {
        return machines;
    }

    private double getValueInRange(NormalDistribution dist, double min, double max)
    {
        double value = dist.sample();
        if (value >= min && value <= max) {
            return value;
        } else {
            return getValueInRange(dist, min, max);
        }
    }
}
