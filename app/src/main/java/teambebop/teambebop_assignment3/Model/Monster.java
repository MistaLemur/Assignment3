/*
Author: Anthony SuVasquez
*/

package teambebop.teambebop_assignment3.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

import teambebop.teambebop_assignment3.Controller.GameController;
import teambebop.teambebop_assignment3.Controller.GameThread;
import teambebop.teambebop_assignment3.R;

import static teambebop.teambebop_assignment3.Controller.GameController.RNG;

public class Monster extends MovingGameObject {

    protected boolean alive = true;
    public static Bitmap monsterSprites[];
    int type = 0;
    public int deathCounter = 0;

    int chaseRadius = 48 * 10;
    int state = 0;
    //monsters have three states?
    //0: idle
    //1: chase
    //2: ghost

    //shock counter is for the damage that the player pikachu character does to the monsters.
    int shockCounter = 0;
    int maxShock = 4;
    double shockTimer = 0;
    double shockDelay = 2;

    int idleX = 0;
    int idleY = 0;
    double idleTimer = 0;
    double idleDelay = 2;
    double ghostTimer = 0;
    double ghostDelay = 20;
    double phaseTimer = 0;
    double phaseDelay = 2;

    double searchTimer = 0;
    double searchDelay = 1;
    int pathIndex = 0;
    ArrayList<node> path;

    public Monster(){
        if(monsterSprites != null){
            type = (int)(System.currentTimeMillis()%2);
            icon = monsterSprites[type];
        }
    }

    public Monster(int newX, int newY, Context _context){
        xPos = newX;
        yPos = newY;

        loadMonsterSprites(_context);

        type = (int)(System.currentTimeMillis()%2);
        icon = monsterSprites[type];
    }

    public static void loadMonsterSprites(Context _context) {
        if (monsterSprites == null) {
            monsterSprites = new Bitmap[4];
            monsterSprites[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.ditto);
            monsterSprites[1] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.celeb1i);
            // FIRE BREATHER
            monsterSprites[2] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.charizard1);
            // ghost
            monsterSprites[3] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.haunter1);
        }
    }

    public void update(int digDugX, int digDugY, GameMap map, GameController controller){
        if(!alive){
            death();
            deathCounter ++;
            return;
        }

        int dx = digDugX - xPos;
        int dy = digDugY - yPos;
        boolean inChaseRange = (dx*dx+dy*dy) < chaseRadius*chaseRadius;
        Rect rect = new Rect(xPos - collideSize/2, yPos - collideSize/2, xPos + collideSize/2, yPos + collideSize/2);

        if(ghostTimer == 0){
            ghostTimer = GameThread.gameTime + ghostDelay * 2 + GameController.RNG.nextDouble() * ghostDelay;
        }

        if(shockCounter > 0){
            if(GameThread.gameTime > shockTimer){
                unshock();
            }
            return;
        }

        switch(state){ //This is a state-machine
            case 1: //chase state
                /*
                 Pathfinding to the player character uses A*
                 */
                boolean shouldSearchNewPath = false;
                if (path != null && pathIndex < path.size()) {
                    //move along the path.
                    boolean success = false;

                    node next = path.get(pathIndex);
                    success = moveTowards(next.x, next.y, map, false);

                    if(!success){
                        shouldSearchNewPath = true;
                    }else{
                        if(next.reached(this)){ //if I reached the node, then move onto the next one
                            pathIndex++;
                        }
                    }

                } else if(!moveTowards(digDugX, digDugY, map, false)){
                    if(!moveTowards(digDugX, digDugY, map, true)){
                        shouldSearchNewPath = true;
                    }
                }
                if(shouldSearchNewPath && GameThread.gameTime > searchTimer){
                    pathIndex = 0;
                    searchTimer = GameThread.gameTime + searchDelay;
                    path = pathfind(digDugX, digDugY, map);
                }


                if(!inChaseRange){
                    state = 0;
                }

                if(GameThread.gameTime > ghostTimer){
                    state = 2;

                    if(path!= null){
                        path.clear();
                        path = null;
                    }

                    phaseTimer = GameThread.gameTime + phaseDelay;
                    setSolid(false);
                }

                break;

            case 2: //ghost state
                //In dig dug, monsters can enter a ghost state where they will phase through the dirt and chase the player.
                setSolid(false);
                System.out.println("GHOST STATE");
                if(!map.collideDirtRect(rect.left, rect.top, rect.right, rect.bottom) && GameThread.gameTime > phaseTimer){
                    System.out.println("TRANSITION OUT OF GHOST");
                    //transition out of this state
                    state = inChaseRange? 1 : 0;
                    setSolid(true);
                    ghostTimer = GameThread.gameTime + ghostDelay + GameController.RNG.nextDouble() * ghostDelay;
                }else{
                    System.out.println("GHOST CHASE");
                    moveTowards(digDugX, digDugY, map, false);
                }
                break;

            case 0: //idle state
                //The monster is just sitting around and waiting for the player in this state. 
                //Random movement here makes them slightly more interesting
                //pick a direction to move.

                if (path != null && pathIndex < path.size()) {
                    //move along the path.
                    boolean success = false;

                    node next = path.get(pathIndex);
                    success = moveTowards(next.x, next.y, map, false);

                    if(success) {
                        if (next.reached(this)) { //if I reached the node, then move onto the next one
                            pathIndex++;
                        }
                    }else{
                        path.clear();
                        path = null;
                        pathIndex = 0;
                    }
                }else if(GameThread.gameTime > idleTimer){
                    idleX = 0;
                    idleY = 0;
                    switch(GameController.RNG.nextInt(5)){
                        case 1:
                            idleX = (int) speed;
                            break;
                        case 2:
                            idleX = (int) -speed;
                            break;
                        case 3:
                            idleY = (int) speed;
                            break;
                        case 4:
                            idleY = (int) -speed;
                            break;
                    }
                    idleTimer = GameThread.gameTime + GameController.RNG.nextDouble() * idleDelay;
                }else if(idleX != 0 || idleY != 0){
                    if(!moveTowards(xPos+idleX, yPos+idleY, map, false)){
                        idleTimer = 0;
                        idleX = 0;
                        idleY = 0;
                    }
                }

                if(inChaseRange){
                    state = 1;
                }

                if(GameThread.gameTime > ghostTimer){
                    state = 2;

                    if(path!= null){
                        path.clear();
                        path = null;
                    }

                    phaseTimer = GameThread.gameTime + phaseDelay;
                    setSolid(false);
                }



                break;
        }
    }


    public void setSolid(boolean newSolid){
        //This function changes the isSolid boolean, which determines if the monster can phase through dirt or not.
        isSolid = newSolid;
        if(isSolid){
            icon = monsterSprites[type];
        }else{
            icon = monsterSprites[3];
        }
    }
    public void setType(int newType){
        type = newType;
        icon = monsterSprites[type];
    }

    public void shocked(){
        //This function is called to deal damage to the monster
        shockCounter ++;
        shockTimer = GameThread.gameTime + shockDelay;
        spriteWidth += 5;
        spriteHeight += 5;

        if(shockCounter >= maxShock){
            death();
        }
    }
    public void unshock(){
        //Damaged monsters heal over time. This function is called to heal damage.
        shockCounter --;
        shockTimer = GameThread.gameTime + shockDelay;
        spriteWidth -= 5;
        spriteHeight -= 5;
    }
    public void flatten(){
        //This function is called when a monster is crushed by a stone.
        spriteHeight = 20;

        death();
    }

    public void death(){
        alive = false;
    }

    //following digdug and touch digdug
    public void attack( int ax1, int ay1, int r) {
        //Since each monster has different attack behaviors, this is just an empty function meant to be overriden
    }


    public void drawToCanvas(int xOff, int yOff, Canvas canvas){
        super.drawToCanvas(xOff, yOff, canvas);

        //debug draw
        /*
        //This draws the A* path that the monster is taking.
        if(path != null && path.size() > 0){
            for(node n:path){
                n.drawToCanvas(xOff, yOff, canvas);
            }
        }
        */
    }

    //A* implementation. This uses rectangular nodes that are arbitrarily sized in order to find paths through the tunnel network.
    public ArrayList<node> pathfind(int destX, int destY , GameMap map){
        ArrayList<node> newPath  = new ArrayList<node>();
        ArrayList<node> reversePath = new ArrayList<node>();

        node origin = new node(xPos, yPos, map, null);
        node end = new node(destX, destY, map, null);
        ArrayList<node> closed = new ArrayList<node>();
        ArrayList<node> open = new ArrayList<node>();

        node current;
        open.add(origin);
        open.addAll(origin.getNeighbors());

        int maxIterations = 50; //this is just here to make sure that A* doesn't freeze the game
        int iterationCount = 0;

        current = open.get(0);
        while(open.size() > 0 && iterationCount++ < maxIterations){

            if(current.equalsTo(end)) break; //found the destination!

            closed.add(current);
            open.remove(current);

            ArrayList<node> neighbors = current.getNeighbors();

            //adding neighbors
            for(node a:neighbors){
                boolean newNeighbor = true;
                a.pathCost = current.pathCost + 1;
                a.heuristic = Math.abs(end.x - a.x) + Math.abs(end.y - a.y); //manhattan heuristic, aka taxicab distance
                a.totalCost = a.pathCost + a.heuristic;

                for(node b:closed){
                    if(a.equalsTo(b)){
                        newNeighbor = false;
                        break;
                    }
                }

                for(node b:open){
                    if(a.equalsTo(b)){
                        if(a.totalCost < b.totalCost){
                            b.totalCost = a.totalCost;
                            b.parent = a.parent;

                        }
                        newNeighbor = false;
                        break;
                    }
                }
                if(newNeighbor){
                    open.add(a);
                }
            }

            //selecting the next node to test.
            node newCurrent = null;
            int newCost = 999999999;
            for(node a:open){
                if(a.totalCost < newCost){
                    newCost = a.totalCost;
                    newCurrent = a;
                }
            }

            if(newCurrent == null){
                break;
            }else{
                current = newCurrent;
            }
        }

        while(current != null){
            reversePath.add(current);
            current = current.parent;
        }

        //now reverse the list.
        for(int i = reversePath.size()-1; i >= 0; i--){
            newPath.add(reversePath.get(i));
        }

        return newPath;
    }
}

class node{
    //This node is for A* pathfinding.
    
    int x;
    int y;
    int nodeWidth = 20;
    int collideWidth = 34;
    int pathCost = 0;
    int heuristic = 0;
    int totalCost = 0;
    node parent;

    GameMap map;

    public node(int newx, int newy, GameMap nMap, node nparent){
        map = nMap;
        x = newx;
        y = newy;
        parent = nparent;
    }

    public boolean reached(MovingGameObject other){
        Rect A = new Rect(x - nodeWidth/2, y - nodeWidth/2, x + nodeWidth/2, y + nodeWidth/2);
        Rect B = new Rect(other.xPos - other.collideSize/2, other.yPos - other.collideSize/2, other.xPos + other.collideSize/2, other.yPos + other.collideSize/2);

        //if(A.intersect(B) || A.contains(B) || B.contains(A)) return true;
        if(A.contains(other.xPos, other.yPos)) return true;
        return false;
    }

    public boolean equalsTo(node other){
        Rect A = new Rect(x - nodeWidth/2, y - nodeWidth/2, x + nodeWidth/2, y + nodeWidth/2);
        Rect B = new Rect(other.x - other.nodeWidth/2, other.y - other.nodeWidth/2, other.x + other.nodeWidth/2, other.y + other.nodeWidth/2);

        if(A.intersect(B) || A.contains(B) || B.contains(A)) return true;
        return false;
    }

    public ArrayList<node> getNeighbors(){
        ArrayList<node> neighbors = new ArrayList<node> ();

        if(!map.collideDirtRect(x-collideWidth/2 + nodeWidth, y-collideWidth/2, x+collideWidth/2 + nodeWidth, y+collideWidth/2)){
            neighbors.add(new node(x+nodeWidth, y, map, this));
        }

        if(!map.collideDirtRect(x-collideWidth/2 - nodeWidth, y-collideWidth/2, x+collideWidth/2 - nodeWidth, y+collideWidth/2)){
            neighbors.add(new node(x-nodeWidth, y, map, this));
        }

        if(!map.collideDirtRect(x-collideWidth/2, y-collideWidth/2 + nodeWidth, x+collideWidth/2, y+collideWidth/2 + nodeWidth)){
            neighbors.add(new node(x, y+nodeWidth, map, this));
        }

        if(!map.collideDirtRect(x-collideWidth/2, y-collideWidth/2 - nodeWidth, x+collideWidth/2, y+collideWidth/2 - nodeWidth)){
            neighbors.add(new node(x, y-nodeWidth, map, this));
        }

        return neighbors;
    }

    public void drawToCanvas(int xOff, int yOff, Canvas canvas){
        //This function is for debugging. Drawing node paths to the screen to troubleshoot the monsters' pathfinding.
        Rect A = new Rect(x - nodeWidth/2 + xOff, y - nodeWidth/2 + yOff, x + nodeWidth/2 + xOff, y + nodeWidth/2 + yOff);
        int x1 = A.left;
        int y1 = A.top;
        int x2 = A.right;
        int y2 = A.bottom;

        Paint outlinePaint = new Paint();
        outlinePaint.setARGB(255, 255,0,0);
        float[] vertices = {x1, y1, x2, y1,
                            x2, y1, x2, y2,
                            x2, y2, x1, y2,
                            x1, y2, x1, y1};
        canvas.drawLines(vertices, outlinePaint);
    }
}
