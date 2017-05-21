package teambebop.teambebop_assignment3.Controller;

/**
 * Created by Byron on 5/17/2017.
 */

import teambebop.teambebop_assignment3.*;
import teambebop.teambebop_assignment3.Model.*;
import teambebop.teambebop_assignment3.View.*;


public class GameController {
    private GameView gameView;
    private DigDug digDug;
    private Monster[] monsters;
    private Rock[] rocks;
    private GameMap map;

    private GameThread gameThread;

    public GameController(GameView newView) {
        if(newView != null) gameView = newView;

        gameThread = new GameThread(this, gameView);
        gameThread.start();
    }

    public void processInput(/** parameter */) {
        // if (moveRight) {
        //    digDug.moveRight();
        // }
        // if (attack) {
        //    digDug.attack();
        // }
    }

    public void update() { // update anything that's moving
        for (int i = 0; i < monsters.length; i++)
            monsters[i].attack();
        for (int i = 0; i < rocks.length; i++) {
            if (rocks[i].shouldFall())
                rocks[i].fall();
        }
    }

}
