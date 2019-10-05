package blockchain.util;

import java.util.ArrayList;
import java.util.Arrays;

public class Statistics {

    /** Calculates the minimum value from a list of numeric values.
     *
     * @param values List of numeric values
     * @return Minimum value
     * @throws Exception
     */
    public static double Min(ArrayList<Double> values) throws Exception
    {
        if (values.size() == 0)
            throw new Exception("The list was empty, so Min could not be determined.");

        int indexOfMin = 0;

        for (int i = 1; i < values.size(); i++)
        {
            Double value = values.get(i);

            if (value < values.get(indexOfMin))
                indexOfMin = i;
        }

        return values.get(indexOfMin);
    }

    public static double Max(ArrayList<Double> values) throws Exception
    {
        ArrayList<Double> values2 = new ArrayList<Double>();
        for (Double value : values)
            if (!value.equals(Double.NaN))
                values2.add(value);

        if (values2.size() == 0)
            throw new Exception("The list was empty, so Max could not be determined.");

        int indexOfMax = 0;

        for (int i = 1; i < values2.size(); i++)
        {
            Double value = values2.get(i);

            if (value > values2.get(indexOfMax))
                indexOfMax = i;
        }

        return values2.get(indexOfMax);
    }



    /**
     * Retrive the quartile value from an array
     * .
     * @param values THe array of data
     * @param lowerPercent The percent cut off. For the lower quartile use 25,
     *      for the upper-quartile use 75
     * @return
     */
    public static double quartile(ArrayList<Double> values, double lowerPercent) {
        // Rank order the values
        double[] v = new double[values.size()];
        System.arraycopy(values, 0, v, 0, values.size());
        Arrays.sort(v);

        int n = (int) Math.round(v.length * lowerPercent / 100);

        return v[n];

    }
}
