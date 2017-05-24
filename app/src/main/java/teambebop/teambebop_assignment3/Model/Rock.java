package teambebop.teambebop_assignment3.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import teambebop.teambebop_assignment3.Controller.GameController;
import teambebop.teambebop_assignment3.R;

/**
 * Created by Byron on 5/17/2017.
 */

public class Rock extends MovingGameObject {
    public static Bitmap rockSprites[];

    private int rumble = 13;
    public boolean isFalling = false;

    public int crumbleCounter;

    public Rock(int newX, int newY, Context _context){

        speed *= 2;

        xPos = newX;
        yPos = newY;

        loadRockSprites(_context);
        icon = rockSprites[0];
    }


    public void update(GameMap map, GameController controller){
        if(crumbleCounter > 0) {
            moveTowards(xPos, yPos+40, map, false);
            crumbleCounter++;
            if(shouldFall(map)) {
                crumbleCounter = 0;
                isFalling = true;
                rumble = 0;
            }
        }else if(isFalling && rumble > 0){

            xPos += (rumble % 3 - 1) * 2;
            rumble--;
        }else if(isFalling){

            moveTowards(xPos, yPos+20, map, false);

            if(controller.digDug != null){
                if(this.collidesOtherObject(controller.digDug)){
                    controller.digDug.flatten();
                    controller.digDug.yPos = this.yPos + this.spriteHeight/2;
                }
            }
            for(Monster monster:controller.monsters){
                if(this.collidesOtherObject(monster)){
                    monster.flatten();
                    monster.yPos = this.yPos + this.spriteHeight/2;
                }
            }

            if(!shouldFall(map)){
                crumbleCounter++;
                isFalling = false;
                //this is ded
            }
        }else if(shouldFall(map)){
            isFalling = true;
            rumble = 10;
        }
    }

    public boolean shouldFall(GameMap map){
        int ax1 = xPos - collideSize/2;
        int ay1 = yPos - collideSize/2 + collideSize;;
        int ax2 = xPos + collideSize/2;
        int ay2 = yPos + collideSize/2 + collideSize;

        if(!map.collideDirtRect(ax1, ay1, ax2, ay2))
            return true;
        else
            return false;
    }

    public static void loadRockSprites(Context _context) {

        rockSprites = new Bitmap[1];
        rockSprites[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.golemoddish);

    }

}
