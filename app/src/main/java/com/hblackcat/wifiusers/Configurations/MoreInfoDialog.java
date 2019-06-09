package com.hblackcat.wifiusers.Configurations;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.hblackcat.wifiusers.Devices.DevicesMacAddresses;
import com.hblackcat.wifiusers.R;

public class MoreInfoDialog extends Activity {

    private WifiInfo wifiInfo;
    private WifiManager wifi;
    private DhcpInfo dhcp;
    private TextView ssid_name,ip_number,mac_number,dns_1_number,dns_2_number,lease_time_number,subnet_mask_number,
            server_ip_number,signal_strength_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.more_info_dialog);

        //Initialize ..
        adView();
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifi.getConnectionInfo();
        dhcp=wifi.getDhcpInfo();
        ssid_name = findViewById(R.id.ssid_name);
        ip_number = findViewById(R.id.ip_number);
        mac_number = findViewById(R.id.mac_number);
        dns_1_number = findViewById(R.id.dns_1_number);
        dns_2_number = findViewById(R.id.dns_2_number);
        lease_time_number = findViewById(R.id.lease_time_number);
        subnet_mask_number = findViewById(R.id.subnet_mask_number);
        server_ip_number = findViewById(R.id.server_ip_number);
        signal_strength_number = findViewById(R.id.signal_strength_number);
        ssid_name.setText(""+wifiInfo.getSSID());
        ip_number.setText(""+String.valueOf(new Formatter().intToIp(dhcp.gateway)));
        mac_number.setText(""+new DevicesMacAddresses().getHardwareAddress(String.valueOf(new Formatter().intToIp(dhcp.gateway))));
        dns_1_number.setText(""+String.valueOf(new Formatter().intToIp(dhcp.dns1)));
        dns_2_number.setText(""+String.valueOf(new Formatter().intToIp(dhcp.dns2)));
        lease_time_number.setText(""+new CalculateLeaseDuration().calcLeaseDuration(dhcp.leaseDuration));
        subnet_mask_number.setText(""+String.valueOf(new Formatter().intToIp(dhcp.netmask)));
        server_ip_number.setText(""+String.valueOf(new Formatter().intToIp(dhcp.serverAddress)));
        signal_strength_number.setText("("+  wifiInfo.getRssi() +" dBm)");
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