package blockchain.game;

import blockchain.scheduler.Schedule;

/**
 * Created by andrzejwilczynski on 12/03/2019.
 */
public class Player
{
    private float schedulingFactor;

    private float scaleSchedulingFactor;

    private Schedule schedule;

    public Player(float schedulingFactor, Schedule schedule)
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

    public float getSchedulingFactor() {
        return schedulingFactor;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public float getScaleSchedulingFactor() {
        return scaleSchedulingFactor;
    }

    public void setScaleSchedulingFactor(float scaleSchedulingFactor) {
        this.scaleSchedulingFactor = scaleSchedulingFactor;
    }
}
