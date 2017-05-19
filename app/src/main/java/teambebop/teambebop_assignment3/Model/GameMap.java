package teambebop.teambebop_assignment3.Model;

import java.util.ArrayList;

/**
 * Created by Byron on 5/17/2017.
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


    public boolean collideDirtRect(int ax1, int ay1, int ax2, int ay2){
        //This function will return true if the given rectangle collides with ANY dirt.

    }

    public boolean collideDirtCircle(int ax1, int ay1, int r){
        //This function will return true if the given circle collides with ANY dirt.

    }
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

        QuadTreeNode child;
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
        if(Math.abs(x2-x1) <= 1 || Math.abs(y2-y1) <= 1) return false;
        return true;
    }

    public void mergeChildren(){
        //this function checks to see if all children are uniform;
        //if they are, the children are removed and "merged" into this object

        if(children.size() == 0) return; //there are no children to merge...

        //first check if the children have children. If so, then pass the function call to the children
        //BEFORE merging this.
        for(QuadTreeNode child: children){
            //if the child also has children, pass the merge call to them
            if(child.children.size() > 0){
                child.mergeChildren();
            }
        }

        int filledValue = -2;
        boolean isUniform = true;
        for(QuadTreeNode child: children){

            if(filledValue == -2){
                filledValue = child.isFilledIn;
            }else if(child.isFilledIn != filledValue){
                isUniform = false;
                break;
            }
        }

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
        }else{
            //if intersects OR I contain the entirety of the given shape, then subdivide and recurse.
            subdivide();
            isFilledIn = -1;

            for(QuadTreeNode child:children){
                digTunnelRect(ax1, ay1, ax2, ay2);
            }
        }

    }

    public void digTunnelCircle(int ax1, int ay1, int r){
        //This function will dig a tunnel in the shape of the given rectangle

        //if I have children, just recursively function call on the children and then return
        if(children.size() > 0){
            for(QuadTreeNode child:children){
                digTunnelCircle(ax1, ay1, r);
            }

            return;
        }

        //if I don't have children, run the collision check
        int collision = collidesCircle(ax1, ay1, r);
        if(collision == 0) return; //the shape does not have anything to do with this node.

        //if the shape contains all of me or I cannot subdivide, then set my value to "empty"
        if(collision == 3 || !canSubdivide()){
            isFilledIn = 0;
        }else{
            //if intersects OR I contain the entirety of the given shape, then subdivide and recurse.
            isFilledIn = -1;
            subdivide();

            for(QuadTreeNode child:children){
                digTunnelCircle(ax1, ay1, r);
            }
        }

    }
}

class collisions{
    /*
     This class simply holds static collision checking functions.
     All rectangles here are AABBs (axis-aligned bounding boxes)
     It will have:
     - Point in Rect
     - Line intersects Line
     - Rect in Rect
     - Rect intersects Rect
     - Line intersects Circle
     - Point in Circle
     - Rect in Circle
     - Circle in Rect
     - Rect intersects Circle
     */
}