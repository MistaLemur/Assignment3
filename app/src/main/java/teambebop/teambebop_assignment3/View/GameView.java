package teambebop.teambebop_assignment3.View;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
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

    public static GameController controller;

    private int offx, offy; //These offsets here are for centering the game map regardless of orientation.
    private int screenWidth, screenHeight, mapWidth;

    private Context myContext;

    public GameView(Context context) {

        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setWillNotDraw(true);

        myContext = context;
        System.out.println("CONSTRUCTOR");

        /*
        if(controller == null) {
            gameInitialization();
        }
        */
    }

    public void setGameMap(GameMap newMap){
        gameMap = newMap;
    }

    private void drawDigDug(Canvas canvas) {
        // Draw DigDug

        //first draw the backdrop
        Rect backdropRect = new Rect(offx, offy, offx+mapWidth, offy+mapWidth );
        canvas.drawBitmap(backdrop, null, backdropRect, null);

        //then draw any tunnels over the backdrop
        synchronized(controller.gameThread) {
            gameMap.drawToCanvas(canvas, offx, offy);
        }

        //then iterate through each MovingGameObject, and draw them. Order doesn't matter

        //lastly, draw score, lives?, level?


    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // Draw according to the game objects
        //recompute screen dimensions

        canvas.drawColor(Color.argb(255, 32, 32, 32));

        screenWidth = canvas.getWidth();
        screenHeight = canvas.getHeight();

        mapWidth = Math.min(screenWidth,screenHeight);
        offx = (screenWidth-mapWidth) / 2;
        offy = (screenHeight-mapWidth) / 2;

        if(backdrop != null){
            drawDigDug(canvas);
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //constructor

        System.out.println("surface create");
        if(controller == null){
            gameInitialization();
        }else{
            controller.setGameView(this);
            computeOffsets();

            double ratio = mapWidth / gameMap.getWidth();
            gameMap.rescale(ratio);

        }

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

        int x = (int) event.getX();
        int y = (int) event.getY();

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(gameMap != null){
                System.out.println(" ");
                System.out.println("DIGGING AT " + (x-offx) + ", " + (y-offy));
                int testRadius = 32;
                synchronized (controller.gameThread) {
                    gameMap.digTunnelCircle(x - offx, y - offy, testRadius);
                    //gameMap.digTunnelRect(x - offx - testRadius, y - offy - testRadius, x - offx + testRadius, y - offy + testRadius);
                }
            }
        }

        return false;
    }

    public void gameInitialization(){
        System.out.println("NEW BACKDROP");
        backdrop = BitmapFactory.decodeResource(myContext.getApplicationContext().getResources(), R.drawable.dirt_background);

        System.out.println("NEW CONTROLLER");
        controller = new GameController(this);

        computeOffsets();
    }

    public void computeOffsets(){
        screenWidth = getWidth();
        screenHeight = getHeight();

        mapWidth = Math.min(screenWidth,screenHeight);
        offx = (screenWidth-mapWidth) / 2;
        offy = (screenHeight-mapWidth) / 2;
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

