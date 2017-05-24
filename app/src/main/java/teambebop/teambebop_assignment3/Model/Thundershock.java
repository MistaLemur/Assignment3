package teambebop.teambebop_assignment3.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import teambebop.teambebop_assignment3.Controller.GameController;
import teambebop.teambebop_assignment3.Controller.GameThread;
import teambebop.teambebop_assignment3.R;

/**
 * Created by Admin on 5/23/2017.
 */

/*
 this class is essentially equivalent to the inflating attack from the original game
 */

public class Thundershock extends MovingGameObject {
    public static Bitmap[] thunderSprites;
    int velX=0, velY=0; //velocity x and y components
    double lifeTimer;
    double lifeDelay = 1.5;

    public boolean alive = true;

    public Thundershock(int x, int y, int vx, int vy, Context _context){
        speed *= 4;
        xPos = x;
        yPos = y;

        velX = vx;
        velY = vy;

        loadThunderSprites(_context);

        icon = thunderSprites[0];
        lifeTimer = GameThread.gameTime + lifeDelay;
    }

    public void update(GameController controller, GameMap map){
        if(GameThread.gameTime > lifeTimer) death();

        for(Monster monster:controller.monsters){
            if(this.collidesOtherObject(monster)){
                monster.shocked();
                death();
            }
        }

        if(alive){
            if(!moveTowards(xPos + (int)(velX * speed), yPos + (int)(velY * speed), map, false)){
                death();
            }
        }
    }

    public void death(){
        alive = false;
    }


    public static void loadThunderSprites(Context _context) {
        if(thunderSprites == null) {
            thunderSprites = new Bitmap[1];
            thunderSprites[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.thundershock);
        }
    }
}
