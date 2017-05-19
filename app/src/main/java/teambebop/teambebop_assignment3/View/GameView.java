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

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private DigDug digDug;
    private Monster[] monsters;
    private Rock[] rocks;
    private GameMap gameMap; //level
    private Bitmap[] soil;

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
    //soil sprites
    public void soilsprites(Context _context){
        soil[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.soil1);
        soil[1] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.soil2);
        soil[2] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.soil3);

    }
}

