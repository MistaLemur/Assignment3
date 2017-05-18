package teambebop.teambebop_assignment3.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

    public static Bitmap monster1[];
    monster1 = new Bitmap[2];
    monster1[0] = BitmapFactory.decodeResource(_context.getApplicationContext().getResources(), R.drawable.16686_Nurse_Joy);

    public void attack() {

    }
}
