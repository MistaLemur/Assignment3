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
1: Follows dig dug (speed normal dig dug speed) (I am probably going to make them start by moving around)
2:
3:
 */
public class Monster extends MovingGameObject {

    protected boolean alive;
    public static Bitmap monster1[];

    public static void loadMonsterSprites(Context _context) {
        if (monster1 == null) {
            monster1 = new Bitmap[3];
            monster1[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.Joy);
            monster1[1] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.Celebi);
            // FIRE BREATHER
            monster1[2] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.Charizard);

        }
    }
    //collideDirtRect(int ax1, int ay1, int ax2, int ay2)
    //making mosnter move
    //public boolean collideDirtCircle(int ax1, int ay1, int r)

    //colliding with dirt. we stop wut
    public void

    //following digdug and touch digdug
    public void attack( int ax1, int ay1, int r) {

        if( GameMap.collideDirtCircle(ax1,ay1,r) == true ){

        }
    }
}