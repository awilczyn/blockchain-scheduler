package blockchain;

import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by andrzejwilczynski on 24/07/2018.
 */
public class Simplex
{
    public static void main(String[] args)
    {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 0.5, 2 }, 0);
        Collection<LinearConstraint> constraints = new
                ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] { 1, 1 }, Relationship.EQ,  1));
        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(
                new MaxIter(100),
                f,
                new LinearConstraintSet(constraints),
                GoalType.MAXIMIZE,
                new NonNegativeConstraint(true)
        );

        double x = solution.getPoint()[0];
        double y = solution.getPoint()[1];
        double max = solution.getValue();
        System.out.println("x: "+x+", y: "+y+", max: "+max);
    }
}
