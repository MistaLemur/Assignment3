package teambebop.teambebop_assignment3.Model;

/**
 * Created by Byron on 5/17/2017.
 */

public class Rock extends MovingGameObject {
    public boolean shouldFall() {
        return false;
    }

    public void fall() {
        // rock falls algorithm - Byron
        /*
        In digdug, if the rock has nothing below, it falls
        when it falls, it has two options, kill the monster in one hit if it's in the way or fall out of the map


        *********** Ask Anthony Miguel Vasquez about how big the quadtree will be. ***********
        */
        if (!shouldFall()){

        }


    }
}
