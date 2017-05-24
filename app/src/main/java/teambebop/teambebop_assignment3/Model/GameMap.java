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
While this can technically keep track of dirt and tunnels, per pixel... setting it to a very high granularity
actually causes the game to slow down significantly because of the number of nodes that much be checked for the most basic functions.
The GameMap only keeps track of dirt, and has nothing to do with the characters that might inhabit the space.
The GameMap class also contains functions for collision checking shapes with solid dirt.
    This is needed for obstruction of monster movement, and digging.
*/


public class GameMap {

    QuadTreeNode quadTreeRoot;

    //the map has limited coordinates!

    public int x1, x2, y1, y2;

    public GameMap(int nx1, int ny1, int nx2, int ny2){
        x1 = nx1;
        x2 = nx2;
        y1 = ny1;
        y2 = ny2;

        quadTreeRoot = new QuadTreeNode(x1, y1, x2, y2, null);
    }

    public void newQuadTree(){
        if(quadTreeRoot != null){
            quadTreeRoot.flush();
        }
        quadTreeRoot = new QuadTreeNode(x1, y1, x2, y2, null);
    }

    public boolean collideDirtRect(int ax1, int ay1, int ax2, int ay2){
        //This function will return true if the given rectangle collides with ANY dirt.
        return quadTreeRoot.collidesDirtRect(ax1, ay1, ax2, ay2);
    }

    public boolean collideDirtCircle(int ax1, int ay1, int r){
        //This function will return true if the given circle collides with ANY dirt.

        return quadTreeRoot.collidesDirtCircle(ax1, ay1, r);
    }

    public boolean collideTunnelRect(int ax1, int ay1, int ax2, int ay2){
        return quadTreeRoot.collideTunnelRect(ax1, ay1, ax2, ay2);
    }

    public void drawToCanvas(Canvas canvas, int offx, int offy){
        quadTreeRoot.drawToCanvas(canvas, offx, offy);
    }

    public void digTunnelCircle(int ax1, int ay1, int r){
        if(quadTreeRoot != null){
            //there's a strange problem with circle-rect intersection,
            //and I can't seem to debug it in time to be able to work on everything else in this
            //So therefore, the workaround is to just dig a rect first, then put the circle over it.
            int r2 = (int)(r * Math.sqrt(2)/2);
            quadTreeRoot.digTunnelRect(ax1 - r2, ay1 - r2, ax1 + r2, ay1 + r2);

            quadTreeRoot.digTunnelCircle(ax1, ay1, r);

            quadTreeRoot.mergeChildrenRect(ax1-r, ay1-r, ax1+r, ay1+r);
        }
    }

    public void digTunnelRect(int ax1, int ay1, int ax2, int ay2){
        if(quadTreeRoot != null){
            quadTreeRoot.digTunnelRect(ax1, ay1, ax2, ay2);
            //quadTreeRoot.mergeChildren();
            quadTreeRoot.mergeChildrenRect(ax1, ay1, ax2, ay2);
        }
    }

    public double getWidth(){
        return x2-x1;
    }
    public double getHeight(){
        return y2-y1;
    }

    public void rescale(double scalar){
        x1 = (int) (x1 * scalar);
        x2 = (int) (x2 * scalar);
        y1 = (int) (y1 * scalar);
        y2 = (int) (y2 * scalar);

        quadTreeRoot.rescale(scalar);
    }
}


class QuadTreeNode{
    public ArrayList<QuadTreeNode> children = new ArrayList<QuadTreeNode>();
    public QuadTreeNode parent;

    //the following four coordinates refer to top left, bottom right.
    public int x1, y1, x2, y2;

    public int isFilledIn = 1;

    //granularity is a constant variable that determines the minimum size of a leaf node.
    static int granularity = 4;

    /*
    isFilledIn is a state variable for this quadtree node.
     -1 indicates a "mixed" value; this node is non-uniform
     0  indicates empty; a tunnel encompasses this whole node
     1  indicates filled; this whole node is made up of solid dirt
     */

    public int depth = 0;

    public static int drawTunnelAlpha = 224;
    //the alpha of a drawn tunnel pixel is almost fully opaque.

    public QuadTreeNode(int nx1, int ny1, int nx2, int ny2, QuadTreeNode nParent){
        x1 = nx1;
        y1 = ny1;
        x2 = nx2;
        y2 = ny2;

        if(nParent != null){
            parent = nParent;
            parent.children.add(this);

            depth = parent.depth + 1;

            isFilledIn = parent.isFilledIn;
        }
    }

    public void flush(){
        //This function deletes this node and all of its children
        if (children.size() > 0){
            for(QuadTreeNode child:children){
                child.flush();
            }
        }

        parent = null;
        children.clear();
        children = null;
    }

    public void subdivide(){
        //this function subdivides this node into four children
        if(!canSubdivide()) return;

        //System.out.println("SUBDIVIDING " + depth);

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
        //only leaf nodes are allowed to divide in quadtrees.
        if(children.size() > 0) return false; //if there are already children, this node can't divide.
        if(Math.abs(x2-x1) <= granularity || Math.abs(y2-y1) <= granularity) return false;

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

    public void mergeChildrenRect(int ax1, int ay1, int ax2, int ay2){
        //This is just like mergeChildren, but it limits the merge checking to the given rect.

        //this function checks to see if all children are uniform;
        //if they are, the children are removed and "merged" into this object
        //Essentially, it merges leaf nodes into their parent branch node.

        //this is not a branch. return.
        if(children.size() == 0) return;
        int collision = collidesRect(ax1, ay1, ax2, ay2);
        if(collision == 0) return;

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

        //does B contain this?
        if(B.contains(A)) return 3;

        //does this contain B?
        if(A.contains(B)) return 2;

        if(!A.intersect(B)) return 0; //the two rectangles don't touch

        return 1;

    }

    public int collidesCircle(int ax1, int ay1, int r){
        //This function checks if this collides with the given circle
        //It returns the following:
        //0 - no collision
        //1 - intersects but neither contains the other
        //2 - this cell contains the entirety of the given circle
        //3 - the given circle contains the entirety of this cell

        //first do a rough check. The rectangle that circumscribes the circle is a good shape to use for rough checks
        //In our case, if neither the node's rectangle or the circle's rectangle are intersecting, then
        //the circle is not intersecting the node either.
        Rect A = new Rect(x1, y1, x2, y2);
        Rect B = new Rect(ax1-r, ay1-r, ax1+r, ay1+r);

        if(!A.intersect(B) && !A.contains(B) && !B.contains(A)) return 0; //the two rectangles don't touch

        //If the circle contains all four vertices of the node, then it contains the rect.
        //This is a simple pythagorean distance check from the circle to each vertex.
        if((x1-ax1)*(x1-ax1) + (y1-ay1)*(y1-ay1) <= r*r &&
                (x2-ax1)*(x2-ax1) + (y1-ay1)*(y1-ay1) <= r*r &&
                (x1-ax1)*(x1-ax1) + (y2-ay1)*(y2-ay1) <= r*r &&
                (x2-ax1)*(x2-ax1) + (y2-ay1)*(y2-ay1) <= r*r)
        {
            return 3;
        }

        boolean contains = A.contains(ax1, ay1);

        boolean intersect1 = circleIntersectsLineSegment(ax1, ay1, r,   x1, y1, x2, y1);
        boolean intersect2 = circleIntersectsLineSegment(ax1, ay1, r,   x2, y1, x2, y2);
        boolean intersect3 = circleIntersectsLineSegment(ax1, ay1, r,   x2, y2, x1, y2);
        boolean intersect4 = circleIntersectsLineSegment(ax1, ay1, r,   x1, y2, x1, y1);

        //boolean for if the boundaries of the circle and rect touch.
        boolean touches = intersect1 || intersect2 || intersect3 || intersect4;

        //the circle does not touch this rect.
        if(!contains && !touches)
            return 0;

        //if(contains && !touches)
        //     return 2;


        //the two are merely intersecting
        return 1;
    }

    public void digTunnelRect(int ax1, int ay1, int ax2, int ay2){
        //This function will dig a tunnel in the shape of the given rectangle
        if(isFilledIn == 0) return;

        int collision = collidesRect(ax1, ay1, ax2, ay2);
        if(collision == 0) return; //the shape does not have anything to do with this node.

        //if I have children, just recursively function call on the children and then return
        if(children.size() > 0){
            for(QuadTreeNode child:children){
                child.digTunnelRect(ax1, ay1, ax2, ay2);
            }
            return;
        }

        //if I don't have children, check collision type...

        //if the shape contains all of me or I cannot subdivide, then set my value to "empty"
        if(collision == 3 || !canSubdivide()){
            isFilledIn = 0;
        }else{ //I can subdivide and the rect does not contain this
            //if intersects OR I contain the entirety of the given shape, then subdivide and recurse.
            subdivide();
            isFilledIn = -1;

            for(QuadTreeNode child:children){
                child.digTunnelRect(ax1, ay1, ax2, ay2);
            }
        }

    }

    public void digTunnelCircle(int ax1, int ay1, int r) {
        //This function will dig a tunnel in the shape of the given rectangle
        if(isFilledIn == 0) return;

        //run collision check
        int collision = collidesCircle(ax1, ay1, r);
        if (collision == 0) return; //the shape does not have anything to do with this node.

        //if I have children, just recursively function call on the children and then return
        if (children.size() > 0) {
            isFilledIn = -1;

            for (QuadTreeNode child : children) {
                child.digTunnelCircle(ax1, ay1, r);
            }
            return;
        }
        //if I don't have children, check collision type

        //if the shape contains all of me or I cannot subdivide, then set my value to "empty"
        if (collision == 3 || !canSubdivide()) {
            isFilledIn = 0;
        } else { //I can subdivide AND circle does not contain this.
            //if intersects OR I contain the entirety of the given shape, then subdivide and recurse.
            subdivide();
            isFilledIn = -1;

            for (QuadTreeNode child : children) {
                child.digTunnelCircle(ax1, ay1, r);
            }
        }
    }

    public boolean collidesDirtRect(int ax1, int ay1, int ax2, int ay2){
        if(isFilledIn == 0) return false;
        if(collidesRect(ax1, ay1, ax2, ay2) == 0){
            return false;
        }

        if(children.size() > 0){
            boolean returnValue = false;
            for(QuadTreeNode child:children){
                returnValue |= child.collidesDirtRect(ax1, ay1, ax2, ay2);
            }
            return returnValue;

        }else if(isFilledIn == 1){
            return true;
        }
        return false;
    }

    public boolean collidesDirtCircle(int ax1, int ay1, int r){
        if(isFilledIn == 0) return false;
        if(collidesCircle(ax1, ay1, r) == 0){
            return false;
        }

        if(children.size() > 0){
            boolean returnValue = false;
            for(QuadTreeNode child:children){
                returnValue |= child.collidesDirtCircle(ax1, ay1, r);
            }
            return returnValue;

        }else if(isFilledIn == 1){
            return true;
        }
        return false;
    }

    public boolean collideTunnelRect(int ax1, int ay1, int ax2, int ay2){
        if(isFilledIn == 1) return false;
        if(collidesRect(ax1, ay1, ax2, ay2) == 0){
            return false;
        }

        if(children.size() > 0){
            boolean returnValue = false;
            for(QuadTreeNode child:children){
                returnValue |= child.collideTunnelRect(ax1, ay1, ax2, ay2);
            }
            return returnValue;

        }else if(isFilledIn == 0){
            return true;
        }
        return false;
    }

    public void drawToCanvas(Canvas canvas, int offx, int offy){
        //this will draw the quad tree to a canvas.
        //It draws tunnels as dark transparent pixels.

        //ONLY THE RECTS OF LEAF NODES ARE DRAWN


        if(children.size() > 0){
            //pass the draw call to the children
            //System.out.println("DRAW CHILD: " + depth);

            for(QuadTreeNode child:children){
                child.drawToCanvas(canvas, offx, offy);
            }
        }else {


            /*
            //debug draw here...
            Paint outlinePaint = new Paint();
            outlinePaint.setARGB(255, 255,255,255);
            float[] vertices = {x1 + offx, y1 + offy, x2 + offx, y1 + offy,
                                x2 + offx, y1 + offy, x2 + offx, y2 + offy,
                                x2 + offx, y2 + offy, x1 + offx, y2 + offy,
                                x1 + offx, y2 + offy, x1 + offx, y1 + offy};
            canvas.drawLines(vertices, outlinePaint);
            */

            if(isFilledIn == 0){
                //draw a rect for this node if it does not have dirt.
                Rect rect = new Rect(x1 + offx - 1, y1 + offy - 1, x2 + offx + 1, y2 + offy + 1);
                Paint rectPaint = new Paint();
                rectPaint.setARGB(drawTunnelAlpha, 0,0,0);
                canvas.drawRect(rect, rectPaint);
            }

        }
    }

    public void rescale(double scalar){
        x1 = (int) (scalar * x1);
        x2 = (int) (scalar * x2);
        y1 = (int) (scalar * y1);
        y2 = (int) (scalar * y2);

        for(QuadTreeNode child:children){
            child.rescale(scalar);
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

        Vector2D _D = new Vector2D(bx2 - bx1, by2 - by1); //vector of the line segment, from one point to the other
        Vector2D _F = new Vector2D(ax1 - bx1, ay1 - by1); //vector from start of the line segment to the circle

        double a = _D.dot(_D);
        double b = _F.dot(_D);
        double c = _F.dot(_F) - ar * ar;

        double discriminant = b*b - 4 * a * c;
        if(discriminant < 0) return false; //no intersection

        discriminant = Math.sqrt(discriminant);
        double t0 = (-b + discriminant) / (2 * a);
        double t1 = (-b - discriminant) / (2 * a);

        if(t0 >= 0 && t0 <= 1){
            return true;
        }else if(t1 >= 0 && t1 <= 1){
            return true;
        }

        return true;
    }
}

class Vector2D{
    /*
     When programming 2d games, a vector2d class is incredibly useful to have.
     Also, this was needed for my algorithm of checking line-circle intersection
     */

    public double x=0, y=0;

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

    public String toString(){
        return "< " + x + ", " + y + " >";
    }
}