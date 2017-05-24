package teambebop.teambebop_assignment3.Controller;

/**
 * Created by Byron on 5/17/2017.
 */

import android.content.Context;

import java.util.Random;

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

    public double score;
    public int lives;


    public GameController(GameView newView) {

        gameView = newView;

        initializeController();
        newGame();

        setGameView(newView);
    }

    public void processInput(int touchX, int touchY, int inputMode) {
        /*
         Inputmode refers to if the touch input is a move or a attack
         */

        if(inputMode == 0){
            //movement
        }else if(inputMode == 1){
            //attack
        }
    }

    public void update() { // update anything that's moving
        //monsters update

        //rocks update

        //digdugupdate
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

        generateLevel(4, 4, 4);
    }

    public void generateLevel(int numTunnels, int numMonsters, int numRocks){

        map.newQuadTree();

        int minMonstersPerTunnel = numMonsters/numTunnels;
        int maxMonstersPerTunnel = (numMonsters+1)/numTunnels;

        int minTunnelLength = 48 * 3;
        int maxTunnelLength = 48 * 6;
        int tunnelWidth = 48;

        int monsterCount = 0;
        monsters = new Monster[maxMonstersPerTunnel * numTunnels];
        rocks = new Rock[numRocks];

        /*
         first generate the tunnels
         */

        //start by digging the main tunnel where digdug resides.
        int startTunnelLength = tunnelWidth * 8;
        int cx = (map.x2-map.x1)/2;

        map.digTunnelRect(cx - tunnelWidth/2, 0, cx + tunnelWidth/2, startTunnelLength);
        map.digTunnelCircle(cx, startTunnelLength, tunnelWidth/2);

        //then place dig dug
        digDug = new DigDug(cx, startTunnelLength, gameView.getContext());

        //now dig the random tunnels that enemies will inhabit
        Random rand = new Random(System.currentTimeMillis());

        for(int i = 0; i < numTunnels; i++){
            int monstersInThisTunnel = minMonstersPerTunnel;
            if(maxMonstersPerTunnel > minMonstersPerTunnel)
                monstersInThisTunnel += rand.nextInt(maxMonstersPerTunnel - minMonstersPerTunnel);

            int tunnelLength = rand.nextInt(maxTunnelLength - minTunnelLength) + minTunnelLength;

            //first place the tunnel
            int w, h;
            if(rand.nextBoolean()){
                //vertical
                w = tunnelWidth;
                h = tunnelLength;
            }else{
                //horizontal
                w = tunnelLength;
                h = tunnelWidth;
            }

            int x, y;

            x = rand.nextInt((int)(map.getWidth() - w)) + w/2;
            y = rand.nextInt((int)(map.getHeight() - h)) + h/2;


            if(map.collideTunnelRect(x-w/2, y-h/2, x+w/2, y+h/2)){
                i--;
                continue;
            }

            //dig the tunnel
            map.digTunnelRect(x - w/2, y - h/2, x + w/2, y + h/2);

            //now dig the rounded ends of each tunnel
            if(h>w){
                //vertical
                map.digTunnelCircle(x, y - h/2, tunnelWidth/2);
                map.digTunnelCircle(x, y + h/2, tunnelWidth/2);
            }else{
                //horizontal
                map.digTunnelCircle(x - w/2, y, tunnelWidth/2);
                map.digTunnelCircle(x + w/2, y, tunnelWidth/2);
            }

            for(int j = 0; j < monstersInThisTunnel; j++){
                int mx = 0, my = 0;
                if(h > w){
                    //vertical tunnel
                    my = rand.nextInt(tunnelLength) - tunnelLength/2;
                }else{
                    //horizontal tunnel
                    mx = rand.nextInt(tunnelLength) - tunnelLength/2;
                }

                //place monster somewhere random along length of the tunnel

                int monsterType = rand.nextInt(3);
                if(monsterType == 2){
                    Monster newMonster = new FireMonster(x + mx, y + my, gameView.getContext());
                    monsters[monsterCount++] = newMonster;
                }else{
                    Monster newMonster = new Monster(x + mx, y + my, gameView.getContext());
                    monsters[monsterCount++] = newMonster;
                }

            }
        }

        for(int i = 0; i < numRocks; i++){
            int rockWidth = 48;
            int x = rand.nextInt((int)(map.getWidth() - rockWidth*2)) + rockWidth;
            int y = rand.nextInt((int)(map.getHeight() - rockWidth * 2)) + rockWidth;

            if(map.collideTunnelRect(x-rockWidth/2, y-rockWidth/2, x+rockWidth/2, y+rockWidth/2)){
                i--;
                continue;
            }
            map.digTunnelRect(x-rockWidth/2, y-rockWidth/2, x+rockWidth/2, y+rockWidth/2);

            Rock newRock = new Rock(x, y, gameView.getContext());
            rocks[i] = newRock;

        }

        setGameViewObjects(gameView);
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

    public void setGameViewObjects(GameView view){
        //this function sends the movableobjects to the gameview.
        if(view == null) return;

        view.setObjects(digDug, monsters, rocks);
    }

}
