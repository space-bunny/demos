package com.fancypixel.spacebunny.vdevicedemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
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

        findViewById(R.id.action_more_sb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                builder.setToolbarColor(ContextCompat.getColor(Main.this, R.color.colorAccent));
                builder.setStartAnimations(Main.this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                builder.setExitAnimations(Main.this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                builder.setShowTitle(true);
                customTabsIntent.launchUrl(Main.this, Uri.parse("http://www.spacebunny.io/"));
            }
        });

        findViewById(R.id.action_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog dialog = new BottomSheetDialog(Main.this);
                View dialogView = View.inflate(Main.this, R.layout.bottom_dialog_menu, null);
                dialogView.findViewById(R.id.action_copy_web).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "http://www.demoapps.spacebunny.io/virtual_device/index.html");
                        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.action_copy)));
                    }
                });
                dialogView.findViewById(R.id.action_try).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=7872946254476055994")));
                    }
                });
                dialog.setContentView(dialogView);
                dialog.show();
            }
        });
    }

}
