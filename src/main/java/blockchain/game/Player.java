package blockchain.game;

import blockchain.scheduler.Schedule;

/**
 * Created by andrzejwilczynski on 12/03/2019.
 */
public class Player
{
    private double schedulingFactor;

    private double scaleSchedulingFactor;

    private Schedule schedule;

    public Player(double schedulingFactor, Schedule schedule)
    {
        this.schedulingFactor = schedulingFactor;
        this.schedule = schedule;
        if (this.schedulingFactor == 0) {
            this.randomSchedulingFactor();
        }
    }

    private void randomSchedulingFactor()
    {
        ;
        this.schedulingFactor = (float) Math.random() * 9999 + 1;
    }

    public double getSchedulingFactor() {
        return schedulingFactor;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public double getScaleSchedulingFactor() {
        return scaleSchedulingFactor;
    }

    public void setScaleSchedulingFactor(double scaleSchedulingFactor) {
        this.scaleSchedulingFactor = scaleSchedulingFactor;
    }
}
