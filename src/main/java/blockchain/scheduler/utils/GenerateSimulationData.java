package blockchain.scheduler.utils;

import java.io.*;

public class GenerateSimulationData
{
    private static double[] tasks, machines;
    private File taskFile = new File("Tasks.txt");
    private File machineFile = new File("Machines.txt");

    public GenerateSimulationData() {
        tasks = new double[Constants.NO_OF_TASKS];
        machines = new double[Constants.NO_OF_VMS];
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

        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            tasks[i] = (Math.random() * (700 - 100)) + 100;
            taskBufferedWriter.write(String.valueOf(tasks[i]) + ' ');
            taskBufferedWriter.write('\n');
        }
        taskBufferedWriter.close();
    }

    private void initComputingCapacity() throws IOException {
        System.out.println("Initializing new machines...");
        BufferedWriter machineBufferedWriter = new BufferedWriter(new FileWriter(machineFile));

        for (int i = 0; i < Constants.NO_OF_VMS; i++) {
            machines[i] = (Math.random() * (4000 - 2000)) + 2000;
            machineBufferedWriter.write(String.valueOf(machines[i]) + ' ');
            machineBufferedWriter.write('\n');
        }

        machineBufferedWriter.close();
    }

    private void readWorkload() throws IOException {
        System.out.println("Reading the tasks...");
        BufferedReader execBufferedReader = new BufferedReader(new FileReader(taskFile));

        int i = 0;
        do {
            String line = execBufferedReader.readLine();
            for (String num : line.split(" ")) {
                tasks[i] = new Double(num);
            }
            ++i;
        } while (execBufferedReader.ready());
    }

    private void readComputingCapacity() throws IOException {
        System.out.println("Reading the machines...");
        BufferedReader capacityBufferedReader = new BufferedReader(new FileReader(machineFile));

        int i = 0;
        do {
            String line = capacityBufferedReader.readLine();
            for (String num : line.split(" ")) {
                machines[i] = new Double(num);
            }
            ++i;
        } while (capacityBufferedReader.ready());
    }

    public static double[] getTasks() {
        return tasks;
    }

    public static double[] getMachines() {
        return machines;
    }
}
