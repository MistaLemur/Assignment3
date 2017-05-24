package teambebop.teambebop_assignment3.Controller;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import teambebop.teambebop_assignment3.View.GameView;

/**
 * Created by Byron on 5/17/2017.
 */

public class GameThread extends Thread {
    private GameController controller;
    private GameView gameView;

    public volatile boolean shouldStop = false;
    public volatile boolean isPaused = false;

    public double gameTime;
    public double lastTime;
    public double deltaTime;

    public GameThread(GameController controller, GameView gameView) {
        this.controller = controller;
        this.gameView = gameView;
    }


    public void run() {
        SurfaceHolder sh = gameView.getHolder();

        gameTime = 0;
        lastTime = System.currentTimeMillis()/1000;

        while (!shouldStop) {
            deltaTime = System.currentTimeMillis()/1000 - lastTime;
            lastTime = System.currentTimeMillis()/1000;
            gameTime += deltaTime;

            Canvas canvas = sh.lockCanvas();
            if (canvas != null) {
                controller.update();
                gameView.draw(canvas);
                sh.unlockCanvasAndPost(canvas);
            }

            try {
                Thread.sleep(10);
                while(isPaused){
                    Thread.sleep(10);
                }
            }catch(InterruptedException e){
                System.out.println(e);
            }
        }
    }
}
