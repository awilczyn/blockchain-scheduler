package blockchain.game;

import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by andrzejwilczynski on 12/03/2019.
 */
public class StackelbergGame
{
    private Player follower;

    private Player leader;

    public StackelbergGame(Player follower, Player leader)
    {
        this.follower = follower;
        this.leader = leader;
        this.scaleSchedulingFactors();
    }

    private void scaleSchedulingFactors()
    {
        double followerSchedulingFactor = this.follower.getSchedulingFactor();
        double leaderSchedulingFactor = this.leader.getSchedulingFactor();

        double[] nums ={followerSchedulingFactor, leaderSchedulingFactor};
        Arrays.sort(nums);
        if (nums[nums.length-1] == followerSchedulingFactor)
        {
            this.follower.setScaleSchedulingFactor(1);
        } else {
            this.follower.setScaleSchedulingFactor(followerSchedulingFactor/leaderSchedulingFactor);
        }
        if (nums[nums.length-1] == leaderSchedulingFactor) {
            this.leader.setScaleSchedulingFactor(1);
        } else {
            this.leader.setScaleSchedulingFactor(leaderSchedulingFactor/followerSchedulingFactor);
        }
    }

    public boolean isLeaderHasBetterSchedule()
    {
        System.out.println("Leader criterion: "+this.leader.getSchedule().getCriterion());
        System.out.println("Leader scheduling factor: "+this.leader.getSchedulingFactor()+", scaled scheduling factor: "+this.leader.getScaleSchedulingFactor());
        System.out.println("Follower criterion: "+this.follower.getSchedule().getCriterion());
        System.out.println("Follower scheduling factor: "+this.follower.getSchedulingFactor()+", scaled scheduling factor: "+this.follower.getScaleSchedulingFactor());

        double s1Coefficient = leader.getSchedule().getCriterion()*
                leader.getScaleSchedulingFactor();
        double s2Coefficient = follower.getSchedule().getCriterion()*
                follower.getScaleSchedulingFactor();

        LinearObjectiveFunction f = new LinearObjectiveFunction(
                new double[] { s1Coefficient, s2Coefficient }, 0
        );
        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(
                new double[] { 1, 1 }, Relationship.EQ,  1)
        );
        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(
                new MaxIter(100),
                f,
                new LinearConstraintSet(constraints),
                GoalType.MAXIMIZE,
                new NonNegativeConstraint(true)
        );


        double y = solution.getPoint()[1];
//        double x = solution.getPoint()[0];
//        double max = solution.getValue();
//
//        System.out.println("s1: "+ x +", s2: "+ y +", max: "+max);
        if (y == 1) {
            return false;
        } else {
            return true;
        }
    }
}
