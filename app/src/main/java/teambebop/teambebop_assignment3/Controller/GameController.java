package teambebop.teambebop_assignment3.Controller;

/**
 * Created by Byron on 5/17/2017.
 */

import android.content.Context;

import java.util.ArrayList;
import java.util.Random;

import teambebop.teambebop_assignment3.*;
import teambebop.teambebop_assignment3.Model.*;
import teambebop.teambebop_assignment3.View.*;


public class GameController {
    public GameView gameView;
    public DigDug digDug;
    public ArrayList<Monster> monsters;
    public ArrayList<Rock> rocks;
    public ArrayList<Thundershock> thunderShocks = new ArrayList<Thundershock>();
    public ArrayList<Fireball> fireballs = new ArrayList<Fireball>();
    private GameMap map;

    public GameThread gameThread;

    public double score;
    public int lives;

    private int inputX, inputY;
    private int inputMode = 0;
    private boolean hasInput;

    public static Random RNG;

    private int tunnels = 4, enemies = 4, rockNum = 4;

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

        if(inputMode == -1){
            hasInput = false;
            return;
        }

        inputX = touchX;
        inputY = touchY;

        hasInput = true;
        this.inputMode = inputMode;
    }

    public void update() { // update anything that's moving
        //monsters update

        //rocks update

        //digdugupdate
        if(hasInput){
            digDug.update(inputX, inputY, inputMode, hasInput, this, map);
        }

        if(monsters.size() == 0){
            tunnels ++;
            enemies ++;
            generateLevel(tunnels, enemies, rockNum);
            return;
        }

        for (int i = 0; i < monsters.size(); i++) {
            Monster monster = monsters.get(i);
            if(monster == null) continue;
            if(monster.deathCounter > 30){
                monsters.remove(monster);
                i--;
                continue;
            }

            if(monster.collidesOtherObject(digDug)){
                digDug.death();
            }
            monster.update(digDug.xPos, digDug.yPos, map, this);
        }

        for (int i = 0; i < rocks.size(); i++) {
            Rock rock = rocks.get(i);
            if(rock == null) continue;
            if(rock.crumbleCounter > 30){
                rocks.remove(rock);
                i--;
                continue;
            }

            rock.update(map, this);
        }


        for (int i = 0; i < thunderShocks.size(); i++) {
            Thundershock shock = thunderShocks.get(i);
            if(shock == null) continue;
            if(!shock.alive){
                thunderShocks.remove(shock);
                i--;
                continue;
            }

            shock.update(this, map);
        }

        for (int i = 0; i < fireballs.size(); i++) {
            Fireball fire = fireballs.get(i);
            if(fire == null) continue;
            if(!fire.alive){
                fireballs.remove(fire);
                i--;
                continue;
            }

            fire.update(this, map);
        }


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
        monsters = new ArrayList<Monster>();
        rocks = new ArrayList<Rock>();

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
        RNG = new Random(System.currentTimeMillis());

        for(int i = 0; i < numTunnels; i++){
            int monstersInThisTunnel = minMonstersPerTunnel;
            if(maxMonstersPerTunnel > minMonstersPerTunnel)
                monstersInThisTunnel += RNG.nextInt(maxMonstersPerTunnel - minMonstersPerTunnel);

            int tunnelLength = RNG.nextInt(maxTunnelLength - minTunnelLength) + minTunnelLength;

            //first place the tunnel
            int w, h;
            if(RNG.nextBoolean()){
                //vertical
                w = tunnelWidth;
                h = tunnelLength;
            }else{
                //horizontal
                w = tunnelLength;
                h = tunnelWidth;
            }

            int x, y;

            x = RNG.nextInt((int)(map.getWidth() - w) - tunnelWidth) + w/2 + tunnelWidth/2;
            y = RNG.nextInt((int)(map.getHeight() - h) - tunnelWidth) + h/2 + tunnelWidth/2;


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
                    my = RNG.nextInt(tunnelLength) - tunnelLength/2;
                }else{
                    //horizontal tunnel
                    mx = RNG.nextInt(tunnelLength) - tunnelLength/2;
                }

                //place monster somewhere random along length of the tunnel

                int monsterType = RNG.nextInt(3);
                if(monsters.size() == 0) monsterType = 2;

                if(monsterType == 2){
                    Monster newMonster = new FireMonster(x + mx, y + my, gameView.getContext());
                    monsters.add(newMonster);
                }else{
                    Monster newMonster = new Monster(x + mx, y + my, gameView.getContext());
                    monsters.add(newMonster);
                }


            }
        }

        for(int i = 0; i < numRocks; i++){
            int rockWidth = 48;
            int x = RNG.nextInt((int)(map.getWidth() - rockWidth*2)) + rockWidth;
            int y = RNG.nextInt((int)(map.getHeight() - rockWidth * 2)) + rockWidth;

            if(map.collideTunnelRect(x-rockWidth/2, y-rockWidth/2, x+rockWidth/2, y+rockWidth/2)){
                i--;
                continue;
            }
            //map.digTunnelRect(x-rockWidth/2, y-rockWidth/2, x+rockWidth/2, y+rockWidth/2);
            map.digTunnelCircle(x, y, rockWidth/2 + 4);

            Rock newRock = new Rock(x, y, gameView.getContext());
            rocks.add(newRock);

        }
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
