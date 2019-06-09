package com.hblackcat.wifiusers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        try
        {
            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                public void run() {
                    try {
                        Intent i = new Intent("com.hblackcat.wifiusers.MainActivity");
                        startActivity(i);
                        finish();
                        //animation ..
                        try{
                            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        }catch (Exception e){}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 500);
        }catch (Exception e){
            Intent i=new Intent("com.hblackcat.wifiusers.MainActivity");
            startActivity(i);
            finish();
        }
    }
}