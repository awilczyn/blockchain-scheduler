package blockchain;

import blockchain.game.Player;
import blockchain.scheduler.Schedule;

/**
 * Created by andrzejwilczynski on 24/07/2018.
 */
public class StackelbergGame
{
    public static void main(String[] args)
    {
        Schedule leaderSchedule = new Schedule();
        Schedule followerSchedule = new Schedule();
        leaderSchedule.makespan = 6000;
        followerSchedule.makespan = 5700;
        Player leader = new Player(284e6, leaderSchedule);
        Player follower = new Player(300e6, followerSchedule);

        blockchain.game.StackelbergGame stackelbergGame = new blockchain.game.StackelbergGame(follower, leader);

        if (stackelbergGame.isLeaderHasBetterSchedule()) {
            System.out.println("Leader schedule accepted");
        } else {
            System.out.println("Leader schedule rejected");
        }
    }
}
