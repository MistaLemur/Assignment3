/*
Author: Anthony SuVasquez
*/
package teambebop.teambebop_assignment3.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;

import teambebop.teambebop_assignment3.Controller.GameController;
import teambebop.teambebop_assignment3.Controller.GameThread;
import teambebop.teambebop_assignment3.R;

import static teambebop.teambebop_assignment3.View.GameView.controller;

/*
Dig dug:
1: can move up down right and left
2: can attack with a wire/pump
3: once connect with wire on enemy; touch button three times to kill it. (must pause for 0.5 sec between pumps)
4: dies if enemy touches digdug
5: removes soil when hes on the array
6: slows down when digging (speed: 0.8*normal speed)
7:
 */
public class DigDug extends MovingGameObject {
    private boolean alive = true;
    private int direction;
    private boolean attacking;
    public static Bitmap DigDugman[];

    int digCounter = 0;

    double attackTimer = 0;
    double attackDelay = 0.5;


    public DigDug(int newX, int newY, Context _context){
        xPos = newX;
        yPos = newY;

        loadDigDugSprite(_context);

        icon = DigDugman[0];

        this.speed = 150;
    }
    public void digDugDig(int dx, int dy, GameMap gameMap){
        //This is the function that digdug calls when he's trying to dig.
        //dx and dy is the direction vector of where he's trying to dig.
        int digDepth = 16;
        if(Math.abs(dx) > Math.abs(dy)){
            dy = 0;
            if(dx > 0){
                dx = digDepth;
            }else{
                dx = -digDepth;
            }
        }else{
            dx = 0;
            if(dy > 0){
                dy = digDepth;
            }else{
                dy = -digDepth;
            }
        }

        int digPosX = xPos + dx;
        int digPosY = yPos + dy;

        digCounter++;
        int digRadius = (int)((Math.sin(digCounter) * 0.2 + 1) * (collideSize+1));
        gameMap.digTunnelCircle(digPosX, digPosY,digRadius);
    }

    public void update(int destX, int destY, int inputMode, boolean shouldMove, GameController controller, GameMap gameMap){
        if(!alive) return;

        int dx = destX - xPos;
        int dy = destY - yPos;
        if(shouldMove && inputMode == 0){
            if(!moveTowards(destX, destY, gameMap, false)){
                digDugDig(dx, dy, gameMap);
            }
        }

        if(shouldMove && inputMode == 1){
            //attack towards this position
            if(GameThread.gameTime > attackTimer) {
                if (Math.abs(dx) > Math.abs(dy)) {
                    dy = 0;
                } else {
                    dx = 0;
                }

                if (dx != 0 || dy != 0) {
                    controller.thunderShocks.add(new Thundershock(xPos, yPos, dx, dy, controller.gameView.getContext()));
                    attackTimer = GameThread.gameTime + attackDelay;
                }
            }
        }
    }

    public void flatten(){
        spriteHeight = 20;

        death();
    }

    public void death(){
        alive = false;
        icon = DigDugman[1];
    }

    public void stopAttack() {
        attacking = false;
    }
    public static void loadDigDugSprite(Context _context) {

        DigDugman = new Bitmap[2];
        DigDugman[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.pikachu2);
        DigDugman[1] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.dedpika);


    }
}
