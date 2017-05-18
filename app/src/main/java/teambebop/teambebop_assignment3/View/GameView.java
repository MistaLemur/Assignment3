package teambebop.teambebop_assignment3.View;


import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import teambebop.teambebop_assignment3.Model.*;
import teambebop.teambebop_assignment3.Controller.*;

/**
 * Created by Byron on 5/17/2017.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private DigDug digDug;
    private Monster[] monsters;
    private Rock[] rocks;
    private GameMap gameMap; //level

    private GameController controller;

    public GameView(Context context) {
        super(context);
    }


    private void drawDigDug(Canvas canvas) {
        // Draw DigDug
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // Draw according to the game objects
        drawDigDug(canvas);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // ..
        // controller.processInput();
        return false;
    }
}

