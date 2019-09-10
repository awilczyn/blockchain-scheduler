package blockchain.scheduler.utils;

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
        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            tasks[i][0] = new Random().nextInt((1000 - 100) + 1) + 100;
            tasks[i][1] = Math.random()*1;
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
        for (int i = 0; i < Constants.NO_OF_VMS; i++) {
            machines[i][0] = (Math.random() * (10 - 1)) + 1;
            machines[i][1] = Math.random()*1;
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
}
