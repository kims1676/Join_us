package kr.co.company.join_us;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by tofot_000 on 2016-11-07.
 */

public class board extends AppCompatActivity {



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);

    }


    public void onBackPressed() {
        Intent bord = new Intent(board.this, menu.class);
        startActivity(bord);
        finish();
    }
}
