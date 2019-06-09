package com.hblackcat.wifiusers.RecyclerView;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hblackcat.wifiusers.R;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView ips,macs,vendor;
    RelativeLayout settings_box;

    ViewHolder(View itemView)
    {
        super(itemView);
        ips = itemView.findViewById(R.id.ips);
        macs = itemView.findViewById(R.id.macs);
        vendor = itemView.findViewById(R.id.vendor);
        settings_box = itemView.findViewById(R.id.settings_box);
    }
}