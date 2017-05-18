package teambebop.teambebop_assignment3.Model;

import java.util.ArrayList;

/**
 * Created by Byron on 5/17/2017.
 */

/*
The GameMap class, in order to achieve a high granularity when digging tunnels, implements a quadtree
to keep track of the dirt.
The GameMap only keeps track of dirt, and has nothing to do with the characters that might inhabit the space.
*/


public class GameMap {

    QuadTreeNode quadTreeRoot;

}


class QuadTreeNode{
    public ArrayList<QuadTreeNode> children = new ArrayList<QuadTreeNode>();
    public QuadTreeNode parent;

    //the following four coordinates refer to top left, bottom right.
    public int x1, y1, x2, y2;

    public int isFilledIn = 1;
    /*
    isFilledIn is a state variable for this quadtree node.
     -1 indicates a "mixed" value; this node is non-uniform
     0  indicates empty; a tunnel encompasses this whole node
     1  indicates filled; this whole node is made up of solid dirt
     */

    public void subdivide(){
        //this function subdivides this node into four children
    }

    public void mergeChildren(){
        //this function checks to see if all children are uniform;
        //if they are, the children are removed and "merged" into this object
    }

    public int collidesRect(int ax1, int ay1, int ax2, int ay2){
        //This function checks if this collides with the given rect.
        //It returns the following:
        //0 - no collision
        //1 - intersects but neither contains the other
        //2 - this cell contains the entirety of the given rect
        //3 - the given rect contains the entirety of this cell

    }

    public int collidesCircle(int ax1, int ay1, int r){
        //This function checks if this collides with the given circle
        //It returns the following:
        //0 - no collision
        //1 - intersects but neither contains the other
        //2 - this cell contains the entirety of the given circle
        //3 - the given circle contains the entirety of this cell
    }

    public void digTunnelRect(int ax1, int ay1, int ax2, int ay2){
        //This function will dig a tunnel in the shape of the given rectangle

        //if I have children, just recursively function call on the children

        //if I don't have children, run the collision check
        //if the shape contains all of me or I cannot subdivide, then set my value to "empty"
        //if intersects OR I contain the entirety of the given shape, then subdivide and recurse.

    }

    public void digTunnelCircle(int ax1, int ay1, int r){
        //This function will dig a tunnel in the shape of the given circle

        //if I have children, just recursively function call on the children

        //if I don't have children, run the collision check
        //if the shape contains all of me or I cannot subdivide, then set my value to "empty"
        //if intersects OR I contain the entirety of the given shape, then subdivide and recurse.
    }
}

class collisions{
    /*
     This class simply holds static collision checking functions.
     All rectangles here are AABBs (axis-aligned bounding boxes)
     It will have:
     - Point in Rect
     - Rect in Rect
     - Rect intersects Rect
     - Line intersects Circle
     - Point in Circle
     - Rect in Circle
     - Circle in Rect
     - Rect intersects Circle
     */
}