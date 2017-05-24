package teambebop.teambebop_assignment3.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;

import teambebop.teambebop_assignment3.Controller.GameController;
import teambebop.teambebop_assignment3.Controller.GameThread;
import teambebop.teambebop_assignment3.R;

/**
 * Created by Byron on 5/17/2017.
 */

/*
Firemonster.
It does what monster does
1: it moves in real time. Thread **********
2: breathes fire
 */

public class FireMonster extends Monster {

    boolean shotFire = false;
    Fireball fire;
    double fireBallRate = 0.1;

    public FireMonster(int newX, int newY, Context _context){
        super.type = 2;
        xPos = newX;
        yPos = newY;

        super.loadMonsterSprites(_context);
        icon = super.monsterSprites[type];
    }

    public void update(int digDugX, int digDugY, GameMap map, GameController controller){
        if(!alive){
            death();
            deathCounter ++;
            return;
        }

        if(state == 1 || state == 0){
            if(fire == null && GameController.RNG.nextDouble() > fireBallRate){
                state = 3;
                shotFire = false;
            }

        }else if(state == 3){
            //shoot the fireball!

            if(fire == null){
                if(shotFire) state = 0;
                else{
                    int dx = digDugX - xPos;
                    int dy = digDugY - yPos;

                    if(Math.abs(dx) > Math.abs(dy)){
                        dy = 0;
                    }else{
                        dx = 0;
                    }

                    fire = new Fireball(xPos, yPos, dx, dy, controller.gameView.getContext());
                    controller.fireballs.add(fire);
                    shotFire = true;
                }
            }else {
                fire.update(controller, map);
            }
        }

        super.update(digDugX, digDugY, map);
    }
}