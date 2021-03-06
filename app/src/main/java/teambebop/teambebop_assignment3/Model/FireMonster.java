/*
Author: Anthony SuVasquez
*/

package teambebop.teambebop_assignment3.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;

import teambebop.teambebop_assignment3.Controller.GameController;
import teambebop.teambebop_assignment3.Controller.GameThread;
import teambebop.teambebop_assignment3.R;

/*
Firemonster.
It does what monster does
1: it moves in real time. 
2: breathes fire
 */

public class FireMonster extends Monster {

    boolean shotFire = false;
    Fireball fire;
    double fireBallRate = 0.02;

    public FireMonster(int newX, int newY, Context _context){
        super.type = 2;
        xPos = newX;
        yPos = newY;

        super.loadMonsterSprites(_context);
        icon = super.monsterSprites[type];
    }

    @Override
    public void update(int digDugX, int digDugY, GameMap map, GameController controller){
        if(!alive){
            death();
            deathCounter ++;
            return;
        }

        if(state == 1 || state == 0){
            if(fire == null && GameController.RNG.nextDouble() < fireBallRate && shockCounter == 0){
                state = 3;
                shotFire = false;
            }

        }else if(state == 3){
            //shoot the fireball!

            if(fire == null){
                if(shotFire) {
                    state = 0;
                }
                else if(shockCounter == 0){
                    int dx = digDugX - xPos;
                    int dy = digDugY - yPos;

                    if(Math.abs(dx) > Math.abs(dy)){
                        dy = 0;
                    }else{
                        dx = 0;
                    }

                    System.out.println("SHOOT FIREBALL: " + dx + ", " + dy);
                    fire = new Fireball(xPos, yPos, dx, dy, controller.gameView.getContext());
                    controller.fireballs.add(fire);
                    fire.owner = this;
                    shotFire = true;
                }
            }else {
                fire.update(controller, map);
            }
        }

        super.update(digDugX, digDugY, map, controller);
    }
}
