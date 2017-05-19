package teambebop.teambebop_assignment3.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import teambebop.teambebop_assignment3.R;

/**
 * Created by Byron on 5/17/2017.
 */

public class Rock extends MovingGameObject {
    public boolean shouldFall() {
        return false;
    }
    public static Bitmap rocksprites[];
    public void fall() {
        // rock falls algorithm - Byron
        /*
        In digdug, if the rock has nothing below, it falls
        when it falls, it has two options, kill the monster in one hit if it's in the way or fall out of the map


        *********** Ask Anthony Miguel Vasquez about how big the quadtree will be. ***********
        */
        if (!shouldFall()){

        }

    public static void loadrockSprites(Context _context) {

        rocksprites = new Bitmap[1];
        rocksprites[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.golemoddish);

    }
    }
}
