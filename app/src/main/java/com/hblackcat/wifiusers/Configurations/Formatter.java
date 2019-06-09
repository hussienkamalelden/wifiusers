package com.hblackcat.wifiusers.Configurations;

import java.util.ArrayList;
import java.util.List;

public class Formatter {

    //Formatter ..
    public String intToIp(int addr) {
        return  ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
    }

    //mac get first 6 digits only ..
    public String getFirstSix(String full_mac) {
        try
        {
            List<String> strings = new ArrayList<String>();
            int index = 0;
            while (index < full_mac.length()) {
                strings.add(full_mac.substring(index, Math.min(index + 6,full_mac.length())));
                index += 6;
            }
            return strings.get(0);
        }catch (Exception e){}

        return "";
    }
}
