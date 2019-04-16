package blockchain.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by andrzejwilczynski on 16/04/2019.
 */
public class Validators
{
    public static int numberOfDayLimit = 30;

    public List<byte []> list = new CopyOnWriteArrayList<byte []>();

    public void update(Transaction transaction) {
        if (transaction.value > 0) {
            if (!list.contains(transaction.getSender())) {
                list.add(transaction.getSender());
            }
        }
    }

    public byte[] getLeader()
    {
        byte[] maxTrustFactorNode = null;
        float maxTrustFactor = 0;
        for (byte[] validator : list) {
            float TF = Node.getTrustFactor(validator, numberOfDayLimit, true);
            if (TF > maxTrustFactor) {
                maxTrustFactor = TF;
                maxTrustFactorNode = validator;
            }
        }
        return maxTrustFactorNode;
    }
}
