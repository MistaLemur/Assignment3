package teambebop.teambebop_assignment3.Model;

/**
 * Created by Byron on 5/17/2017.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import teambebop.teambebop_assignment3.Controller.GameThread;

public abstract class MovingGameObject {
    protected int xPos, yPos;
    protected Bitmap icon;

    //These are doubles because, in hindsight, it was a mistake to make the quadtree dimensions be pixel-based.
    //Instead the quadtree should have been decimals from 0 to 1... :(

    protected int spriteSize = 48; //this is relative to the map size.
    protected int collideSize = 32; //this is relative to the map size.
    protected double speed = 1.0;

    protected double subX, subY; //these are leftover coords


    public void drawToCanvas(int xOff, int yOff, Canvas canvas){
        if(icon == null) return;

        canvas.drawBitmap(icon, null,
                new Rect(xPos-spriteSize/2 + xOff, yPos-spriteSize/2 + yOff, xPos+spriteSize/2 + xOff, yPos+spriteSize/2 + yOff),
                null);
    }

    public boolean moveTowards(int destX, int destY, GameMap gameMap, boolean reversePriority){
        int xdestvect = destX - xPos;
        int ydestvect = destY - yPos;

        int ax1 = xPos - collideSize/2;
        int ay1 = yPos - collideSize/2;
        int ax2 = xPos + collideSize/2;
        int ay2 = yPos + collideSize/2;

        //move this in the cardinal direction closest to the target.
        if(Math.abs(xdestvect) < Math.abs(ydestvect) || (reversePriority && Math.abs(xdestvect) > Math.abs(ydestvect))){

            double vx =  speed * GameThread.deltaTime;

            if (xdestvect > 0){
                int vdx = (int) vx;
                subX += vx - vdx;

                vdx += (int)(subX);
                subX -= (int)(subX);
                if(!gameMap.collideDirtRect(ax1 + vdx,ay1, ax2 + vdx, ay2)) {
                    xPos++;
                    return true;
                }
            }else if(xdestvect < 0) {
                vx *= -1;

                int vdx = (int) vx;
                subX += vx - vdx;

                vdx += (int)(subX);
                subX -= (int)(subX);
                if(!gameMap.collideDirtRect(ax1 + vdx,ay1, ax2 + vdx, ay2)) {
                    xPos--;
                    return true;
                }
            }

        }else{
            double vy =  speed * GameThread.deltaTime;

            if(ydestvect >0){
                int vdy = (int) (vy);
                subY += vy - vdy;

                vdy += (int)(subY);
                subY -= (int)(subY);

                if(!gameMap.collideDirtRect(ax1,ay1 + vdy, ax2, ay2 + vdy)) {
                    yPos++;
                    return true;
                }
            } else if(ydestvect < 0){
                vy *= -1;

                int vdy = (int) (vy);
                subY += vy - vdy;

                vdy += (int)(subY);
                subY -= (int)(subY);

                if(!gameMap.collideDirtRect(ax1,ay1 + vdy, ax2, ay2 + vdy)) {
                    yPos--;
                    return true;
                }
            }
        }

        return false;
    }

    public void rescale(double scalar){
        xPos *= scalar;
        yPos *= scalar;
        spriteSize *= scalar;
        collideSize *= scalar;
    }
}
