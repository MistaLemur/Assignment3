package teambebop.teambebop_assignment3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import teambebop.teambebop_assignment3.Controller.GameController;
import teambebop.teambebop_assignment3.View.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("MAIN");
        GameView gameView = new GameView(this);

        setContentView(gameView);
    }
}
