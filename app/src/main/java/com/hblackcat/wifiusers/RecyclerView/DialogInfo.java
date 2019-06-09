package com.hblackcat.wifiusers.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.hblackcat.wifiusers.R;

public class DialogInfo extends Activity {

    private Intent intent;
    private TextView ip_number,mac_number,vendor_name;
    private ImageView ip_copy,mac_copy,vendor_copy;
    private Animation zoom_in_and_out_fast;
    private String vendor = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_info);

        //Initialize ..
        adView();
        zoom_in_and_out_fast = AnimationUtils.loadAnimation(this, R.anim.zoom_in_and_out_fast);
        ip_number = findViewById(R.id.ip_number);
        mac_number = findViewById(R.id.mac_number);
        vendor_name = findViewById(R.id.vendor_name);
        ip_copy = findViewById(R.id.ip_copy);
        mac_copy = findViewById(R.id.mac_copy);
        vendor_copy = findViewById(R.id.vendor_copy);
        intent = getIntent();

        //get data from Recyclerview Adapter ..
        try {
            ip_number.setText("" + getIntent().getStringExtra("ip"));
            mac_number.setText("" + getIntent().getStringExtra("mac"));
            vendor = getIntent().getStringExtra("vendor");
            if(vendor.contains("MY DEVICE"))
                vendor_name.setText(vendor.replace("MY DEVICE",""));
            else
                vendor_name.setText(vendor);
        }catch (Exception e){}

        //ip_copy ..
        ip_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Animation ..
                try
                {
                    ip_copy.startAnimation(zoom_in_and_out_fast);
                }catch(Exception e){}

                try{
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("simple texts",ip_number.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(DialogInfo.this, "Copied", Toast.LENGTH_SHORT).show();
                }catch(Exception e){e.printStackTrace();}
            }
        });

        //mac_copy ..
        mac_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Animation ..
                try
                {
                    mac_copy.startAnimation(zoom_in_and_out_fast);
                }catch(Exception e){}

                try{
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("simple texts",mac_number.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(DialogInfo.this, "Copied", Toast.LENGTH_SHORT).show();
                }catch(Exception e){e.printStackTrace();}
            }
        });

        //vendor_copy ..
        vendor_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Animation ..
                try
                {
                    vendor_copy.startAnimation(zoom_in_and_out_fast);
                }catch(Exception e){}

                try{
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("simple texts",vendor_name.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(DialogInfo.this, "Copied", Toast.LENGTH_SHORT).show();
                }catch(Exception e){e.printStackTrace();}
            }
        });
    }

    //show_banner_Ad ..
    public void adView()
    {
        try{
            AdView adView = this.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }catch(Exception e){e.printStackTrace();}
    }
}