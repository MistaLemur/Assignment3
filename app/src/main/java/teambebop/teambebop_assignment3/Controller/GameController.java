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

    public GameThread gameThread;

    public GameController(GameView newView) {

        gameView = newView;

        initializeController();
        newGame();

        setGameView(newView);
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
        /*
        for (int i = 0; i < monsters.length; i++) {
            //monsters[i].attack();
        }
        for (int i = 0; i < rocks.length; i++) {
            if (rocks[i].shouldFall())
                rocks[i].fall();
        }
        */
    }

    public void initializeController(){
        int width = gameView.getHeight();
        int height = gameView.getWidth();
        int mapWidth = Math.min(width, height);

        System.out.println("NEW MAP: " + mapWidth);
        map = new GameMap(0, 0, mapWidth, mapWidth);

    }

    public void newGame(){

    }

    public void generateLevel(int numTunnels, int numMonsters, int numRocks){

    }

    public void setGameView(GameView view){
        if(gameThread != null){
            gameThread.shouldStop = true;
        }

        gameView = view;
        gameView.setGameMap(map);

        gameThread = new GameThread(this, gameView);
        gameThread.start();

    }

}
