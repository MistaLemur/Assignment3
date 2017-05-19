package teambebop.teambebop_assignment3.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import teambebop.teambebop_assignment3.R;

/**
 * Created by Byron on 5/17/2017.
 */
/*
Monster:
1: Follows dig dug (speed normal dig dug speed) (I am probably going to make them start moving around)
2:
3:
 */
public class Monster extends MovingGameObject {

    protected boolean alive;
    public static Bitmap monster1[];

    public static void loadMonsterSprites(Context _context) {

        monster1 = new Bitmap[2];
        monster1[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.Joy);
        monster1[1] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.Celebi);


    }

    public void attack() {

    }
}