package teambebop.teambebop_assignment3.View;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import teambebop.teambebop_assignment3.Model.*;
import teambebop.teambebop_assignment3.Controller.*;
import teambebop.teambebop_assignment3.R;

/**
 * Created by Byron on 5/17/2017.
 */


/*
 * For reference, pixel scaling is ~2x.
 * Base resolution of the map will be ~1024x1024
 * so with a board size of "11x11" we will have "grid sizes" of approximately 48x48
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    /*
     These three variables are static because the GameView object is cleaned up and
     reinstantiated everytime the screen changes orientation.

     So, instead, I would prefer that the game changes orientation along with the screen rather than
     restarting the game everytime the screen changes orientation.
     */
    private static GameMap gameMap; //level

    private static Bitmap backdrop;

    private static GameController controller;

    private int offx, offy; //These offsets here are for centering the game map regardless of orientation.

    public GameView(Context context) {

        super(context);
    }


    private void drawDigDug(Canvas canvas) {
        // Draw DigDug

        //first draw the backdrop

        //then draw any tunnels over the backdrop

        //then iterate through each MovingGameObject, and draw them. Order doesn't matter

        //lastly, draw score, lives?, level?


    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // Draw according to the game objects
        drawDigDug(canvas);
        System.out.println("surface draw");

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //constructor

        if(controller == null){
            //start a new game. do this by first creating the controller
            controller = new GameController(this);
        }
        System.out.println("surface create");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        System.out.println("surface change");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        System.out.println("surface destroy");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // ..
        // controller.processInput();
        return false;
    }
    //soil sprites
    /*
    public void soilsprites(Context _context){
        soil[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.soil1);
        soil[1] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.soil2);
        soil[2] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.soil3);
    }
    */
}

