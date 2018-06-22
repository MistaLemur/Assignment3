/*
Author: Anthony SuVasquez
*/
package teambebop.teambebop_assignment3.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import teambebop.teambebop_assignment3.Controller.GameController;
import teambebop.teambebop_assignment3.Controller.GameThread;
import teambebop.teambebop_assignment3.Model.GameMap;
import teambebop.teambebop_assignment3.Model.MovingGameObject;
import teambebop.teambebop_assignment3.R;

public class Fireball extends MovingGameObject {

    public static Bitmap[] fireSprites;
    int velX=0, velY=0; //velocity x and y components
    double lifeTimer;
    double lifeDelay = 2;

    public boolean alive = true;
    int chargeCounter = 0;

    public FireMonster owner;

    public Fireball(int x, int y, int vx, int vy, Context _context) {
        speed *= 2;
        xPos = x;
        yPos = y;

        velX = vx;
        velY = vy;

        loadFireSprites(_context);

        icon = fireSprites[0];
        lifeTimer = GameThread.gameTime + lifeDelay;
    }


    public void update(GameController controller, GameMap map){
        if(chargeCounter < 10) {
            chargeCounter ++;
            return;
        }
        if(GameThread.gameTime > lifeTimer) death();

        if(this.collidesOtherObject(controller.digDug)) {
            controller.digDug.death();
        }

        if(alive){
            if(!moveTowards(xPos + (int)(velX * speed), yPos + (int)(velY * speed), map, false)){
                death();
            }
        }
    }

    public void death(){

        alive = false;
        owner.fire = null;
        owner = null;
    }


    public static void loadFireSprites(Context _context) {
        if(fireSprites == null) {
            fireSprites = new Bitmap[1];
            fireSprites[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.fireball1);
        }
    }
}
