package com.hblackcat.wifiusers.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.hblackcat.wifiusers.R;

import java.util.List;
import java.util.Random;

public class Adapter extends RecyclerView.Adapter<ViewHolder> {

    private int counter = 0;
    private int lastPosition= -1;
    private Context context;
    private List<DataCatcher> catcher_arr;
    private InterstitialAd interstitialAd;
    private boolean settings_clicked = false;

    //get Context and array of data ..
    public Adapter(Context context,List<DataCatcher> catcher){
        this.context = context;
        this.catcher_arr = catcher;
    }

    // create holder ..
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        ViewHolder holder = new ViewHolder(row);
        return holder;
    }

    // counter of items ...
    @Override
    public int getItemCount() {
        //check if there data in arraylist ..
        if(catcher_arr != null)
        {
            if(settings_clicked == true)
            {
                settings_clicked = false;
                //load ad..
                try {
                    //here put Your Ad:
                    interstitialAd = new InterstitialAd(context);
                    interstitialAd.setAdUnitId(context.getString(R.string.admob_full));
                    //Load Ad ..
                    AdRequest adRequest = new AdRequest.Builder().build();
                    interstitialAd.loadAd(adRequest);
                } catch (Exception e) {
                }
            }
            counter = catcher_arr .size(); // set array size in counter ..
            return catcher_arr.size(); // get array size ..
        }else {
            return counter;
        }
    }

    // this method called every item added or viewed on screen ..
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        // create new object to receive data from list of objects *** ATTENTION: don't worry onBind will not call untill add item ***
        DataCatcher obj = catcher_arr.get(position);
        holder.ips.setText(obj.ip); // Get IP from arraylist ..
        holder.macs.setText(obj.mac); // Get Mac from arraylist ..
        holder.vendor.setText(obj.vendor); // Get vendor from arraylist ..

        //change text color of my device ..
        if(obj.vendor.contains("MY DEVICE"))
        {
            holder.vendor.setTextColor(Color.parseColor("#00FF00"));
            holder.vendor.setText("MY DEVICE");
        }else
            holder.vendor.setTextColor(Color.parseColor("#ffffff"));

        //Start animation ..
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            holder.itemView.startAnimation(anim);
            lastPosition = position;
        }

        //Click to settings of Item ..
        holder.settings_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                settings_clicked = true;

                //settings of item ..
                try {
                    //open dialog ..
                    DataCatcher objec = catcher_arr.get(position);
                    Intent i = new Intent("com.hblackcat.wifiusers.RecyclerView.DialogInfo");
                    i.putExtra("ip",""+objec.ip);
                    i.putExtra("mac",""+objec.mac);
                    i.putExtra("vendor",""+objec.vendor);
                    context.startActivity(i);

                    // start full screen ad ..
                    try{
                        if (interstitialAd.isLoaded())
                            interstitialAd.show();
                    }catch(Exception e){e.printStackTrace();}

                }catch (Exception e){e.printStackTrace();}
            }
        });
    }

    //created to clear animation ..
    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }
}