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

    public GameThread(GameController controller, GameView gameView) {
        this.controller = controller;
        this.gameView = gameView;
    }

    public void run() {
        SurfaceHolder sh = gameView.getHolder();
        System.out.println("START THREAD");

        while (true) {

            System.out.println("GAME THREAD");
            Canvas canvas = sh.lockCanvas();
            if (canvas != null) {
                controller.update();
                gameView.draw(canvas);
                sh.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void start(){

    }
}
