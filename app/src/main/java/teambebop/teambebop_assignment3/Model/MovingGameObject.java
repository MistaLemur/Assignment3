package teambebop.teambebop_assignment3.Model;

/**
 * Created by Byron on 5/17/2017.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public abstract class MovingGameObject {
    protected int xPos, yPos;
    protected Bitmap icon;

    //These are doubles because, in hindsight, it was a mistake to make the quadtree dimensions be pixel-based.
    //Instead the quadtree should have been decimals from 0 to 1... :(

    protected int spriteSize = 48; //this is relative to the map size.
    protected int collideSize = 32; //this is relative to the map size.


    public void drawToCanvas(int xOff, int yOff, Canvas canvas){
        if(icon == null) return;

        canvas.drawBitmap(icon, null,
                new Rect(xPos-spriteSize/2 + xOff, yPos-spriteSize/2 + yOff, xPos+spriteSize/2 + xOff, yPos+spriteSize/2 + yOff),
                null);
    }

    public void rescale(double scalar){
        xPos *= scalar;
        yPos *= scalar;
        spriteSize *= scalar;
        collideSize *= scalar;
    }
}
