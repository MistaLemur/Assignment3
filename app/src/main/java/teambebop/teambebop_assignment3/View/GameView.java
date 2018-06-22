/*
Author: Anthony "Miguel" SuVasquez

This file contains all application drawing functions and all touch input capturing.
*/
package teambebop.teambebop_assignment3.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.util.ArrayList;

import teambebop.teambebop_assignment3.Model.*;
import teambebop.teambebop_assignment3.Controller.*;
import teambebop.teambebop_assignment3.R;

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

    private int inputMode = 0;
    private Rect inputButtonRect;

    private static Bitmap[] inputButtonBitmaps;

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
        if(controller.rocks != null) {
            for (Rock rock : controller.rocks) {
                if(rock == null) continue;
                rock.drawToCanvas(offx, offy, canvas);
            }
        }
        if(controller.thunderShocks != null){
            for (Thundershock shock : controller.thunderShocks) {
                if(shock == null) continue;
                shock.drawToCanvas(offx, offy, canvas);
            }
        }

        if(controller.monsters != null) {
            for (Monster monster : controller.monsters) {
                if(monster == null) continue;
                monster.drawToCanvas(offx, offy, canvas);
            }
        }
        if(controller.fireballs != null){
            for (Fireball fire : controller.fireballs) {
                if(fire == null) continue;
                fire.drawToCanvas(offx, offy, canvas);
            }
        }

        if(controller.digDug != null) {
            controller.digDug.drawToCanvas(offx, offy, canvas);
        }


        //lastly, draw score, lives?, level?



        //the input buttons
        //first the move button (left/top)
        if(inputButtonBitmaps != null && inputButtonBitmaps[0] != null && inputButtonBitmaps[1] != null) {
            Rect buttRect = new Rect(inputButtonRect);
            Paint buttPaint = new Paint();
            if (screenHeight > screenWidth) {
                buttRect.right = (buttRect.right + buttRect.left) / 2;
            } else {
                buttRect.bottom = (buttRect.bottom + buttRect.top) / 2;
            }
            if (inputMode == 0) {
                buttPaint.setARGB(255, 255, 255, 255);
            } else {
                buttPaint.setARGB(64, 128, 128, 128);
            }
            canvas.drawBitmap(inputButtonBitmaps[0], null, buttRect, buttPaint);

            //now the attack button (right/bottom)

            buttRect = new Rect(inputButtonRect);
            if (screenHeight > screenWidth) {
                buttRect.left = (buttRect.right + buttRect.left) / 2;
            } else {
                buttRect.top = (buttRect.bottom + buttRect.top) / 2;
            }
            if (inputMode == 1) {
                buttPaint.setARGB(255, 255, 255, 255);
            } else {
                buttPaint.setARGB(64, 128, 128, 128);
            }
            canvas.drawBitmap(inputButtonBitmaps[1], null, buttRect, buttPaint);
        }

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

            if(controller.digDug != null) controller.digDug.rescale(ratio);
            if(controller.monsters != null){
                for(Monster monster:controller.monsters){
                    if(monster == null) continue;
                    monster.rescale(ratio);
                }
            }
            if(controller.rocks != null){
                for(Rock rock:controller.rocks){
                    if(rock == null) continue;
                    rock.rescale(ratio);
                }
            }
            if(controller.thunderShocks != null){
                for(Thundershock shock:controller.thunderShocks){
                    if(shock == null) continue;
                    shock.rescale(ratio);
                }
            }

        }
        computeButtonRect();

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

            if(inputButtonRect.contains(x,y)){
                System.out.println("INPUT TOGGLE");
                inputMode = (inputMode+1)%2;

                return false;
            }

            if(controller != null)
                controller.processInput(x - offx, y - offy, inputMode);
        }
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            if(controller != null)
                controller.processInput(x - offx, y - offy, inputMode);
        }
        if(event.getAction() == MotionEvent.ACTION_UP){
            if(controller != null)
                controller.processInput(x - offx, y - offy, -1);
        }

        return true;
    }

    public void gameInitialization(){
        System.out.println("NEW BACKDROP");
        backdrop = BitmapFactory.decodeResource(myContext.getApplicationContext().getResources(), R.drawable.dirt_background);

        System.out.println("NEW CONTROLLER");
        controller = new GameController(this);

        inputButtonBitmaps = new Bitmap[2];
        inputButtonBitmaps[0] = BitmapFactory.decodeResource(myContext.getApplicationContext().getResources(), R.drawable.move);
        inputButtonBitmaps[1] = BitmapFactory.decodeResource(myContext.getApplicationContext().getResources(), R.drawable.attack);

        computeOffsets();
    }

    public void computeOffsets(){
        screenWidth = getWidth();
        screenHeight = getHeight();

        mapWidth = Math.min(screenWidth,screenHeight);
        offx = (screenWidth-mapWidth) / 2;
        offy = (screenHeight-mapWidth) / 2;
    }

    public void computeButtonRect(){

        int cx, cy, w, h;
        if(screenWidth < screenHeight){ //portrait mode
            w = screenWidth * 1 / 3;
            h = (screenHeight - offy - mapWidth) * 1 / 2;

            cx = screenWidth / 2;
            cy = offy + mapWidth + h + h / 4;


        }else{ //landscape mode
            w = (screenWidth - offx - mapWidth) * 1 / 2;
            h = (screenHeight) * 1 / 2;

            cy = screenHeight / 2;
            cx = offx + mapWidth + w;

        }
        inputButtonRect = new Rect(cx-w/2, cy-h/2, cx+w/2, cy+h/2);
    }
}

