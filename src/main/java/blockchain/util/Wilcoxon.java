package blockchain.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Wilcoxon {

    public static void dataForWilcoxon(double[] criterionArray) {
        System.out.println("Values of criterion:");
        for (int i = 0; i < criterionArray.length; i++) {
            System.out.println(Math.round(criterionArray[i]));
        }
        standardDeviation(criterionArray);
    }

    public static void dataForWilcoxonDecimal(double[] criterionArray) {
        System.out.println("Values of criterion:");
        DecimalFormat df = new DecimalFormat("#.###");
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        dfs.setDecimalSeparator(',');
        df.setDecimalFormatSymbols(dfs);
        for (int i = 0; i < criterionArray.length; i++) {
            System.out.println(df.format(criterionArray[i]));
        }
        standardDeviation(criterionArray);
    }


    public static double standardDeviation(double[] criterionArray) {
        double standardDeviation = 0;
        double average = 0;
        int counter = 0;
        for (int i = 0; i < criterionArray.length; i++) {
            average = average+criterionArray[i];
            counter++;
        }
        average = average/counter;
        double sum = 0;
        for (int i = 0; i < criterionArray.length; i++) {
            sum = sum + Math.pow(criterionArray[i]-average, 2);
        }
        standardDeviation = Math.sqrt(sum/counter);
        System.out.println("Average of values: "+average);
        System.out.println("Standard deviation: "+ standardDeviation);
        return standardDeviation;
    }
}
