package teambebop.teambebop_assignment3.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import teambebop.teambebop_assignment3.Controller.GameThread;
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
    public static Bitmap monsterSprites[];
    int type = 0;

    public Monster(){
        if(monsterSprites != null){
            type = (int)(System.currentTimeMillis()%2);
            icon = monsterSprites[type];
        }
    }

    public Monster(int newX, int newY, Context _context){
        xPos = newX;
        yPos = newY;

        loadMonsterSprites(_context);

        type = (int)(System.currentTimeMillis()%2);
        icon = monsterSprites[type];
    }

    public static void loadMonsterSprites(Context _context) {
        if (monsterSprites == null) {
            monsterSprites = new Bitmap[3];
            monsterSprites[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.ditto);
            monsterSprites[1] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.celebi);
            // FIRE BREATHER
            monsterSprites[2] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.charizard);
        }
    }

    public void setType(int newType){
        type = newType;
        icon = monsterSprites[type];
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