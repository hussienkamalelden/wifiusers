package com.hblackcat.wifiusers.RecyclerView;

public class DataCatcher {

    public String ip,mac,vendor;

    public DataCatcher(String device_ip,String device_mac,String device_vendor)
    {
        this.ip = device_ip;
        this.mac = device_mac;
        this.vendor = device_vendor;
    }
}