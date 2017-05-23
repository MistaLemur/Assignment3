package teambebop.teambebop_assignment3.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;

import teambebop.teambebop_assignment3.R;

/**
 * Created by Byron on 5/17/2017.
 */
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
    private boolean alive;
    private int direction;
    private boolean attacking;
    public static Bitmap DigDugman[];
    int action = event.getAction() & event.ACTION_MASK;

    public void moveLeft(int xPos) {
       xPos--;

    }

    public void moveRight(int xPos) {
         xPos++;

    }

    public void moveDown(int yPos) {
        yPos--;

    }

    public void moveUp (int yPos){
        yPos++;
    }

    // ...

    public void attack() {

    }

    public void stopAttack() {

    }
    public static void loadDigDugSprite(Context _context) {

        DigDugman = new Bitmap[1];
        //DigDugman[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.pikachu);

    }
}
