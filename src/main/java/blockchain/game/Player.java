package blockchain.game;

/**
 * Created by andrzejwilczynski on 12/03/2019.
 */
public class Player
{
    private float schedulingFactor;

    private float scaleSchedulingFactor;

    private float timeOfSchedule;

    public Player(float schedulingFactor, float timeOfSchedule)
    {
        this.schedulingFactor = schedulingFactor;
        this.timeOfSchedule = timeOfSchedule;
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

    public float getTimeOfSchedule() {
        return timeOfSchedule;
    }

    public float getScaleSchedulingFactor() {
        return scaleSchedulingFactor;
    }

    public void setScaleSchedulingFactor(float scaleSchedulingFactor) {
        this.scaleSchedulingFactor = scaleSchedulingFactor;
    }
}
