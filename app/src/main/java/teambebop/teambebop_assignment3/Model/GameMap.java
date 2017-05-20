package teambebop.teambebop_assignment3.Model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

/**
 * Created by Miguel SuVasquez on 5/17/2017.
 */

/*
The GameMap class, in order to achieve a high granularity when digging tunnels,
implements a quadtree to keep track of the dirt and the tunnels.
The GameMap only keeps track of dirt, and has nothing to do with the characters that might inhabit the space.
The GameMap class also contains functions for collision checking shapes with solid dirt.
    This is needed for obstruction of monster movement, and digging.
*/


public class GameMap {

    QuadTreeNode quadTreeRoot;

    //the map has limited coordinates!
    int x1, x2, y1, y2;

    public boolean collideDirtRect(int ax1, int ay1, int ax2, int ay2){
        //This function will return true if the given rectangle collides with ANY dirt.
        return quadTreeRoot.collidesDirtRect(ax1, ay1, ax2, ay2);
    }

    public boolean collideDirtCircle(int ax1, int ay1, int r){
        //This function will return true if the given circle collides with ANY dirt.
        return quadTreeRoot.collidesDirtCircle(ax1, ay1, r);
    }

    public void drawToCanvas(Canvas canvas){
        quadTreeRoot.drawToCanvas(canvas);
    }
}


class QuadTreeNode{
    public ArrayList<QuadTreeNode> children = new ArrayList<QuadTreeNode>();
    public QuadTreeNode parent;

    //the following four coordinates refer to top left, bottom right.
    public int x1, y1, x2, y2;

    public int isFilledIn = 1;

    //granularity is a constant variable that determines the minimum size of a leaf node.
    static int granularity = 1;

    /*
    isFilledIn is a state variable for this quadtree node.
     -1 indicates a "mixed" value; this node is non-uniform
     0  indicates empty; a tunnel encompasses this whole node
     1  indicates filled; this whole node is made up of solid dirt
     */

    public static int drawTunnelAlpha = 224;
    //the alpha of a drawn tunnel pixel is almost fully opaque.

    public QuadTreeNode(int nx1, int nx2, int ny1, int ny2, QuadTreeNode nParent){
        x1 = nx1;
        y1 = ny1;
        x2 = nx2;
        y2 = ny2;

        if(nParent != null){
            parent = nParent;
            parent.children.add(this);

            isFilledIn = parent.isFilledIn;
        }
    }

    public void subdivide(){
        //this function subdivides this node into four children
        if(!canSubdivide()) return;

        int w = x2-x1;
        int h = y2-y1;

        //top left
        new QuadTreeNode(x1, y1, x1+w/2, y1+h/2, this);
        //top right
        new QuadTreeNode(x1 + w/2 + 1, y1, x1+w, y1+h/2, this);
        //bottom left
        new QuadTreeNode(x1, y1+h/2 + 1, x1+w/2, y1+h, this);
        //bottom right
        new QuadTreeNode(x1 + w/2 + 1, y1 + h/2 + 1, x1+w, y1+h, this);
    }

    public boolean canSubdivide(){
        if(Math.abs(x2-x1) <= granularity || Math.abs(y2-y1) <= granularity) return false;

        //only leaf nodes are allowed to divide in quadtrees.
        if(children.size() > 0) return false; //if there are already children, this node can't divide.
        return true;
    }

    public void mergeChildren(){
        //this function checks to see if all children are uniform;
        //if they are, the children are removed and "merged" into this object
        //Essentially, it merges leaf nodes into their parent branch node.

        //this is not a branch. return.
        if(children.size() == 0) return;

        //IF the children are branches, then pass the merge call to them recursively.
        for(QuadTreeNode child: children){
            //if the child also has children, pass the merge call to them
            if(child.children.size() > 0){
                child.mergeChildren();
            }
        }

        //at this point, this is a branch and the children are leafs.
        int filledValue = -2;
        boolean isUniform = true;

        //Check to see if all children leaf nodes have the same value.
        for(QuadTreeNode child: children){

            if(filledValue == -2){
                filledValue = child.isFilledIn;
            }else if(child.isFilledIn != filledValue){
                isUniform = false;
                break;
            }
        }

        //if all the leaves are uniform, then remove the leaves and change this value.
        if(isUniform && filledValue >= 0){
            //oh shit merge that fucking shit.
            //remove the children.
            children.clear();
            isFilledIn = filledValue;
        }
    }

    public int collidesRect(int ax1, int ay1, int ax2, int ay2){
        //This function checks if this collides with the given rect.
        //It returns the following:
        //0 - no collision
        //1 - intersects but neither contains the other
        //2 - this cell contains the entirety of the given rect
        //3 - the given rect contains the entirety of this cell

        Rect A = new Rect(x1, y1, x2, y2);
        Rect B = new Rect(ax1, ay1, ax2, ay2);

        if(!A.intersect(B)) return 0; //the two rectangles don't touch

        //does B contain this?
        if(B.contains(A)) return 3;

        //does this contain B?
        if(A.contains(B)) return 2;

        return 1;

    }

    public int collidesCircle(int ax1, int ay1, int r){
        //This function checks if this collides with the given circle
        //It returns the following:
        //0 - no collision
        //1 - intersects but neither contains the other
        //2 - this cell contains the entirety of the given circle
        //3 - the given circle contains the entirety of this cell

        Rect A = new Rect(x1, y1, x2, y2);
        boolean contains = A.contains(ax1, ay1);

        boolean intersect1 = circleIntersectsLineSegment(ax1, ay1, r,   x1, y1, x2, y1);
        boolean intersect2 = circleIntersectsLineSegment(ax1, ay1, r,   x2, y1, x2, y2);
        boolean intersect3 = circleIntersectsLineSegment(ax1, ay1, r,   x2, y2, x1, y2);
        boolean intersect4 = circleIntersectsLineSegment(ax1, ay1, r,   x1, y2, x1, y1);

        //boolean for if the boundaries of the circle and rect touch.
        boolean touches = intersect1 || intersect2 || intersect3 || intersect4;

        //the circle does not touch this rect.
        if(!(contains || touches))
            return 0;

        //this rect contains the circle.
        if(contains && !(touches))
            return 2;

        //SO MANY SQRT CALLS :(
        double dist1 = distBetweenPoints(ax1, ay1, x1, y1);
        double dist2 = distBetweenPoints(ax1, ay1, x2, y1);
        double dist3 = distBetweenPoints(ax1, ay1, x1, y2);
        double dist4 = distBetweenPoints(ax1, ay1, x2, y2);
        //the circle contains this rect
        if(dist1 <= r && dist2 <= r && dist3 <= r && dist4 <= r)
            return 3;

        //the two are merely intersecting
        return 1;
    }

    public void digTunnelRect(int ax1, int ay1, int ax2, int ay2){
        //This function will dig a tunnel in the shape of the given rectangle

        //if I have children, just recursively function call on the children and then return
        if(children.size() > 0){
            for(QuadTreeNode child:children){
                digTunnelRect(ax1, ay1, ax2, ay2);
            }
            return;
        }

        //if I don't have children, run the collision check
        int collision = collidesRect(ax1, ay1, ax2, ay2);
        if(collision == 0) return; //the shape does not have anything to do with this node.

        //if the shape contains all of me or I cannot subdivide, then set my value to "empty"
        if(collision == 3 || !canSubdivide()){
            isFilledIn = 0;
        }else{ //I can subdivide and the rect does not contain this
            //if intersects OR I contain the entirety of the given shape, then subdivide and recurse.
            subdivide();
            isFilledIn = -1;

            for(QuadTreeNode child:children){
                digTunnelRect(ax1, ay1, ax2, ay2);
            }
        }

    }

    public void digTunnelCircle(int ax1, int ay1, int r) {
        //This function will dig a tunnel in the shape of the given rectangle

        //if I have children, just recursively function call on the children and then return
        if (children.size() > 0) {
            isFilledIn = -1;

            for (QuadTreeNode child : children) {
                digTunnelCircle(ax1, ay1, r);
            }
            return;
        }

        //if I don't have children, run the collision check
        int collision = collidesCircle(ax1, ay1, r);
        if (collision == 0) return; //the shape does not have anything to do with this node.

        //if the shape contains all of me or I cannot subdivide, then set my value to "empty"
        if (collision == 3 || !canSubdivide()) {
            isFilledIn = 0;
        } else { //I can subdivide AND circle does not contain this.
            //if intersects OR I contain the entirety of the given shape, then subdivide and recurse.
            isFilledIn = -1;
            subdivide();

            for (QuadTreeNode child : children) {
                digTunnelCircle(ax1, ay1, r);
            }
        }
    }

    public boolean collidesDirtRect(int ax1, int ay1, int ax2, int ay2){
        if(collidesRect(ax1, ay1, ax2, ay2) == 0){
            return false;
        }

        if(children.size() > 0){
            boolean returnValue = false;
            for(QuadTreeNode child:children){
                returnValue |= child.collidesDirtRect(ax1, ay1, ax2, ay2);
            }

            return returnValue;
        }else{
            if(isFilledIn == 1 && collidesRect(ax1, ay1, ax2, ay2) != 0){
                return true;
            }else{
                return false;
            }
        }
    }

    public boolean collidesDirtCircle(int ax1, int ay1, int r){
        if(collidesCircle(ax1, ay1, r) == 0){
            return false;
        }

        if(children.size() > 0){
            boolean returnValue = false;
            for(QuadTreeNode child:children){
                returnValue |= child.collidesDirtCircle(ax1, ay1, r);
            }

            return returnValue;
        }else{
            if(isFilledIn == 1 && collidesCircle(ax1, ay1, r) != 0){
                return true;
            }else{
                return false;
            }
        }
    }

    public void drawToCanvas(Canvas canvas){
        if(children.size() > 0){
            //pass the draw call to the children
            for(QuadTreeNode child:children){
                child.drawToCanvas(canvas);
            }
        }else if(isFilledIn == 0){
            //draw a rect for this node if it does not have dirt.
            Rect rect = new Rect(x1, y1, x2, y2);
            Paint rectPaint = new Paint();
            rectPaint.setARGB(drawTunnelAlpha, 0,0,0);
            canvas.drawRect(rect, rectPaint);
        }
    }


    /*
     * Below are some generic and useful geometry functions
     */
    public static double distBetweenPoints(int ax1, int ay1, int ax2, int ay2){
        //Pythagorean distance between two points.
        //qq uses sqrt. :(

        return Math.sqrt((ax2-ax1) * (ax2-ax1) + (ay2-ay1) * (ay2-ay1));
    }

    public static boolean circleIntersectsLineSegment(int ax1, int ay1, int ar,
                                                      int bx1, int by1, int bx2, int by2){
        //This function returns true if the given circle and line segment intersect.
        //Using this image as reference... https://i.stack.imgur.com/P556i.png

        //the vector representing the line segment
        Vector2D AB = new Vector2D(bx2 - bx1, by2 - by1);

        //vector from one end of line segment to the circle
        Vector2D AC = new Vector2D(ax1 - bx1, ay1 - by1);

        //projecting AC onto AB gives us a point on the line
        Vector2D AD = AC.projectUnto(AB); //this uses an inverse sqrt... :(

        //distance vector from the point on line to the circle's centroid.
        Vector2D DC = new Vector2D(AC);
        DC.subtract(AD);

        if(DC.lengthSquared() > ar * ar) return false; //circle does not intersect line segment.

        double dot = AB.dot(AD);
        if(dot < 0 || dot > AB.lengthSquared()) return false; //projection AD does not exist within line segment

        return true;
    }
}

class Vector2D{
    /*
     When programming 2d games, a vector2d class is incredibly useful to have.
     Also, this was needed for my algorithm of checking line-circle intersection
     */

    public double x, y;

    public Vector2D(double nx, double ny){
        x = nx;
        y = ny;
    }

    public Vector2D(Vector2D B){ //copy constructor
        x = B.x;
        y = B.y;
    }

    public void add(Vector2D B){
        x += B.x; y+=B.y;
    }
    public void subtract(Vector2D B){
        x -= B.x; y -= B.y;
    }
    public void multiply(double scalar){
        x *= scalar; y *= scalar;
    }
    public double dot(Vector2D B){
        return x*B.x + y*B.y;
    }
    public double length(){
        //sqrt :(((
        return Math.sqrt(lengthSquared());
    }
    public double lengthSquared(){
        return x*x+y*y;
    }
    public void normalize(){
        //inverse sqrt :((((((
        double length = length();
        x /= length;
        y /= length;
    }

    public double componentOf(Vector2D B){
        //This function returns the length of this vector's component, parallel to b
        B = new Vector2D(B); //copying B so I don't affect the original object
        B.normalize();
        return this.dot(B);
    }

    public Vector2D projectUnto(Vector2D B){
        //This function returns a new vector of this projected unto B.
        double bLength = B.length();
        B = new Vector2D(B);
        B.multiply(this.dot(B)/ bLength / bLength);
        return B;
    }
}