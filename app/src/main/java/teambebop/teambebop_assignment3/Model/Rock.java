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
    } //false == rock does not fall    ; true == the rock falls
    public static Bitmap rockSprites[];

    public Rock(int newX, int newY, Context _context){

        xPos = newX;
        yPos = newY;

        loadRockSprites(_context);
        icon = rockSprites[0];
    }

    public void fall() {
        // rock falls algorithm - Byron
        /*
        In digdug, if the rock has nothing below, it falls
        when it falls, it has two options, kill the monster in one hit if it's in the way or fall out of the map


        *********** Ask Anthony Miguel Vasquez about how big the quadtree will be. ***********
        */
        if (!shouldFall()) {

        }
    }

    public static void loadRockSprites(Context _context) {

        rockSprites = new Bitmap[1];
        rockSprites[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.golemoddish);

    }

}
