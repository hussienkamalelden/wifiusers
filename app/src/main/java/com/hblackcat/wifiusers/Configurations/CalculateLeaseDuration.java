package com.hblackcat.wifiusers.Configurations;

import java.util.concurrent.TimeUnit;

public class CalculateLeaseDuration {

    //calculate lease duration from seconds to days,hours,minutes ..
    public String calcLeaseDuration(long seconds)
    {
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
        return day +" day, "+hours+" hours";
    }
}
