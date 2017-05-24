package teambebop.teambebop_assignment3.Model;

/**
 * Created by Byron on 5/17/2017.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import teambebop.teambebop_assignment3.Controller.GameThread;

public abstract class MovingGameObject {
    public int xPos, yPos;
    public int minX = 0, minY = -40, maxX = 0, maxY = 40;
    protected Bitmap icon;

    //These are doubles because, in hindsight, it was a mistake to make the quadtree dimensions be pixel-based.
    //Instead the quadtree should have been decimals from 0 to 1... :(

    protected int spriteWidth = 48;
    protected int spriteHeight = 48;
    protected int collideSize = 32; //this is relative to the map size.
    protected double speed = 100;

    protected boolean faceEast = true;

    protected double subX, subY; //these are leftover coords

    protected boolean isSolid = true;


    public void drawToCanvas(int xOff, int yOff, Canvas canvas){
        if(icon == null) return;

        Rect spriteRect = new Rect(xPos-spriteWidth/2 + xOff, yPos-spriteHeight/2 + yOff, xPos+spriteWidth/2 + xOff, yPos+spriteHeight/2 + yOff);
        /*
        if(!faceEast){
            int tmp = spriteRect.left;
            spriteRect.left = spriteRect.right;
            spriteRect.right = tmp;
        }
        */

        canvas.drawBitmap(icon, null, spriteRect, null);
    }

    public boolean moveTowards(int destX, int destY, GameMap gameMap, boolean reversePriority){

        if(destX < minX) destX = minX;
        if(destX > gameMap.getWidth() + maxX) destX = (int)gameMap.getWidth() + maxX;
        if(destY < minY) destY = minY;
        if(destY > gameMap.getHeight() + maxY) destY = (int)gameMap.getHeight() + maxY;

        int xdestvect = destX - xPos;
        int ydestvect = destY - yPos;

        if(xdestvect == 0 && ydestvect == 0) return true;

        int ax1 = xPos - collideSize/2;
        int ay1 = yPos - collideSize/2;
        int ax2 = xPos + collideSize/2;
        int ay2 = yPos + collideSize/2;

        //System.out.println("DELTA: " + xdestvect + ", " + ydestvect);

        //move this in the cardinal direction closest to the target.
        if(Math.abs(xdestvect) > Math.abs(ydestvect) || (reversePriority && Math.abs(xdestvect) < Math.abs(ydestvect))){

            double vx =  Math.min(speed * GameThread.deltaTime, Math.abs(xdestvect));

            if (xdestvect > 0){
                int vdx = (int) vx;

                subX += vx - vdx;

                vdx += (int)(subX);
                subX -= (int)(subX);

                if(!this.isSolid || !gameMap.collideDirtRect(ax1 + vdx,ay1, ax2 + vdx, ay2)) {
                    xPos+= vdx;
                    faceEast = true;
                    return true;
                }
            }else if(xdestvect < 0) {
                vx *= -1;

                int vdx = (int) vx;

                subX += vx - vdx;

                vdx += (int)(subX);
                subX -= (int)(subX);

                if(!this.isSolid || !gameMap.collideDirtRect(ax1 + vdx,ay1, ax2 + vdx, ay2)) {
                    xPos+= vdx;
                    faceEast = false;
                    return true;
                }
            }

        }else{
            double vy =  Math.min(speed * GameThread.deltaTime, Math.abs(ydestvect));

            if(ydestvect >0){
                int vdy = (int) (vy);

                subY += vy - vdy;

                vdy += (int)(subY);
                subY -= (int)(subY);

                if(!this.isSolid || !gameMap.collideDirtRect(ax1,ay1 + vdy, ax2, ay2 + vdy)) {
                    yPos+= vdy;
                    return true;
                }

            } else if(ydestvect < 0){
                vy *= -1;

                int vdy = (int) (vy);

                subY += vy - vdy;

                vdy += (int)(subY);
                subY -= (int)(subY);

                if(!this.isSolid || !gameMap.collideDirtRect(ax1,ay1 + vdy, ax2, ay2 + vdy)) {
                    yPos+= vdy;
                    return true;
                }

            }
        }

        return false;
    }

    public boolean collidesOtherObject(MovingGameObject other){
        Rect A = new Rect(this.xPos - this.collideSize/2, this.yPos - this.collideSize/2,
                            this.xPos + this.collideSize/2, this.yPos + this.collideSize/2);

        Rect B = new Rect(other.xPos - other.collideSize/2, other.yPos - other.collideSize/2,
                other.xPos + other.collideSize/2, other.yPos + other.collideSize/2);

        if(A.intersect(B) || A.contains(B) || B.contains(A)) return true;
        return false;
    }

    public void rescale(double scalar){
        xPos *= scalar;
        yPos *= scalar;
        spriteWidth *= scalar;
        spriteHeight *= scalar;
        collideSize *= scalar;
    }
}
