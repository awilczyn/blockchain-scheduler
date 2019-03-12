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
        float followerSchedulingFactor = this.follower.getSchedulingFactor();
        float leaderSchedulingFactor = this.leader.getSchedulingFactor();

        float[] nums ={followerSchedulingFactor, leaderSchedulingFactor};
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

    public boolean isFollowerHasBetterSchedule()
    {
        float s1Coefficient = leader.getTimeOfSchedule()*leader.getScaleSchedulingFactor();
        float s2Coefficient = follower.getTimeOfSchedule()*follower.getScaleSchedulingFactor();

        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { s1Coefficient, s2Coefficient }, 0);
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
        if (y == 1) {
            return true;
        } else {
            return false;
        }
    }
}
