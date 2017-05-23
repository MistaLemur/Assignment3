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
    int xPos =0;
    int yPos = 0;

    public static void loadMonsterSprites(Context _context) {
        if (monster1 == null) {
            monster1 = new Bitmap[3];
            //monster1[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.Joy);
            //monster1[1] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.Celebi);
            // FIRE BREATHER
            //monster1[2] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.Charizard);

        }
    }
    //collideDirtRect(int ax1, int ay1, int ax2, int ay2)
    //making mosnter move
    /*
    Compute the direction vector from the monster to digdug
You do this by subtracting the coordinates from one to another
And then you look at the x and y components of the direction vector
To determine what direction to move along
     */

    // ax1 = digdug position
    //ay1 == digdug position

    public void moveauto(int ax1, int ay1){
        int xdestvect = ax1 - xPos;
        int ydestvect = ay1 - yPos;

        if (xdestvect > 0 && ydestvect >0){
            xPos++;
        }else if ( xdestvect > 0 && ydestvect < 0){
            yPos++;
        } else if(xdestvect < 0 && ydestvect >0){
            yPos++;
        } else{
            xPos--;
        }

    }


    //colliding with dirt. we stop wut
   // public void

    //following digdug and touch digdug
    public void attack( int ax1, int ay1, int r) {
        /*

        if( GameMap.collideDirtCircle(ax1,ay1,r) == true ){

        }
        */
    }

}