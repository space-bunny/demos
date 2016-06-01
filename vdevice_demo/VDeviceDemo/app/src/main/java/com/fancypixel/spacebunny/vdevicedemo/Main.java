package com.fancypixel.spacebunny.vdevicedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import io.spacebunny.vdevicedemo.R;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText code_text = (EditText) findViewById(R.id.code_text);
        LinearLayout action_start = (LinearLayout) findViewById(R.id.action_start);

        action_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = code_text.getText().toString();
                if (code.length() == 5) {
                    Intent i = new Intent(Main.this, SendData.class);
                    i.putExtra(Constants.CODE_EXTRA_INTENT, code);
                    startActivity(i);
                } else {
                    Toast.makeText(Main.this, getString(R.string.code_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
