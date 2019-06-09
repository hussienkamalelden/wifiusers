package com.hblackcat.wifiusers.Configurations;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class SignalStrength {

    private WifiInfo wifiInfo;
    private WifiManager wifi;
    private Context context;
    private int dBm;
    private String dBm_value;

    public SignalStrength(Context contextX)
    {
        context =contextX;
        //Initialize wifi manager and wifi info..
        wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifi.getConnectionInfo();

    }

    //check dBm  ..
    public String checkDBM()
    {
        try
        {
            dBm = wifiInfo.getRssi();
            if(dBm>=-60){
                //Excellent
                return "excellent";
            }else if(dBm>=-70){
                //Good
                return "good";
            }else if(dBm>=-80){
                //Fair
                return "fair";
            }else if(dBm>=-90) {
                //Weak
                return "weak";
            }else
                return "nothing";
        }catch (Exception e){}

        return "nothing";
    }
}
