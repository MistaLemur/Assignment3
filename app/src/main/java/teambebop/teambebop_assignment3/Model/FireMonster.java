package teambebop.teambebop_assignment3.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;

import teambebop.teambebop_assignment3.R;

/**
 * Created by Byron on 5/17/2017.
 */

/*
Firemonster.
It does what monster does
1: it moves in real time. Thread **********
2: breathes fire
 */

public class FireMonster extends Monster {

    int type = 2;

    public FireMonster(int newX, int newY, Context _context){
        xPos = newX;
        yPos = newY;

        super.loadMonsterSprites(_context);
        icon = super.monsterSprites[this.type];
    }

    public void attack() {

    }
}
