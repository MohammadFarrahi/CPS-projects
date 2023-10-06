package ir.ac.ut.pong;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private PongGame pongGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pongGame = new PongGame(this);
        setContentView(pongGame);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pongGame.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pongGame.pause();
    }
}