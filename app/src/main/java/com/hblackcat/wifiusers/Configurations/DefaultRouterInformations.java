package com.hblackcat.wifiusers.Configurations;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

public class DefaultRouterInformations {

    private WifiInfo wifiInfo;
    private Context context;
    private WifiManager wifi;
    private DhcpInfo dhcp;
    private String my_device_Name,my_device_Man,my_device_ip,my_device_mac,router_dns1,router_dns2,router_gateway,
            router_leaseDuration,router_netmask,router_serverAddress,router_ssid,router_mac,signal_strength;

    public DefaultRouterInformations(Context contextX)
    {
        context =contextX;
        //Initialize wifi manager and wifi info..
        wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifi.getConnectionInfo();

        //Initialize DHCP ..
        dhcp=wifi.getDhcpInfo();
    }

    //get my phone and router information ..
    public String getDefaultInformations()
    {
        try{
            //My device info ..
            my_device_ip = "IP Address: " + String.valueOf(new Formatter().intToIp(dhcp.ipAddress));
            my_device_mac = "MAC Address: " + checkMyDeviceMacAddr();
            my_device_Name = "Model: " + android.os.Build.MODEL;
            my_device_Man = "Manufacturer: " + android.os.Build.MANUFACTURER;

            //router info ..
            router_ssid = "SSID: " + wifiInfo.getSSID();
            router_gateway = "Default Gateway: " + String.valueOf(new Formatter().intToIp(dhcp.gateway));
            router_mac = "MAC Address: " + (wifiInfo.getBSSID().toUpperCase());
            router_dns1 = "DNS 1: " + String.valueOf(new Formatter().intToIp(dhcp.dns1));
            router_dns2 = "DNS 2: " + String.valueOf(new Formatter().intToIp(dhcp.dns2));
            router_leaseDuration = "Lease Time: " + new CalculateLeaseDuration().calcLeaseDuration(dhcp.leaseDuration);
            router_netmask = "Subnet Mask: " + String.valueOf(new Formatter().intToIp(dhcp.netmask));
            router_serverAddress = "Server IP: " + String.valueOf(new Formatter().intToIp(dhcp.serverAddress));
            signal_strength = "Signal Strength: (" +  wifiInfo.getRssi() +" dBm)";
        }catch(Exception e){e.printStackTrace();}

        return "My Device Info :\n" + my_device_ip + "\n" + my_device_mac + "\n" + my_device_Name + "\n" + my_device_Man +
                "\n" + "\n ---------------------------" + "\nNetwork Info :\n" + router_ssid + "\n" + router_gateway +
                "\n" + router_mac + "\n" + router_dns1 + "\n" + router_dns2 + "\n" +
                router_leaseDuration + "\n" + router_netmask + "\n" + router_serverAddress+ "\n" + signal_strength;
    }

    //check OS if it over Marshmellow or not ..
    public String checkMyDeviceMacAddr()
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return new MyDeviceMacAddress().getMyMacAddr();
        else
            return wifiInfo.getMacAddress();
    }
}
