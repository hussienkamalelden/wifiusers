package com.hblackcat.wifiusers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.hblackcat.wifiusers.Configurations.DefaultRouterInformations;
import com.hblackcat.wifiusers.Configurations.Formatter;
import com.hblackcat.wifiusers.Configurations.SignalStrength;
import com.hblackcat.wifiusers.DataBase.DataBaseConnector;
import com.hblackcat.wifiusers.Devices.DevicesMacAddresses;
import com.hblackcat.wifiusers.RecyclerView.Adapter;
import com.hblackcat.wifiusers.RecyclerView.DataCatcher;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {

    public  List<DataCatcher> catcher_arr;
    DataCatcher catcher_obj;
    private WifiInfo wifiInfo;
    private RelativeLayout router_info,router_info_bg,devices_num_box;
    private InetAddress host;
    private DhcpInfo dhcp;
    private TextView users_numb,disconnect,wifi_name,ip_number,mac_number,signal_number,scanning;
    private ImageView start,signal_strength,start_small,more_info;
    private int reactX_counter = 0;
    private int method_start = 0;
    private Subscription subscription;
    private Button enable_wifi;
    private WifiManager wifi;
    private Context context =this;
    private RecyclerView recycler_view;
    private Adapter custom_adapter;
    private DataBaseConnector connector;
    private String mac_first_6_digits,signal,full_mac;
    private ConnectivityManager connManager;
    private NetworkInfo mWifi;
    private Handler handler;
    private int time = 5000; //milliseconds
    private boolean handler_run =false;
    private boolean start_button_clicked =false;
    private boolean main_start =false;
    private Animation zoom_in_and_out_fast,start_btn_animation;
    private boolean repeat_method =false;
    private InterstitialAd interstitialAd;
    private CountDownTimer CountDownTimer;
    private boolean one_click_only =false;
    private VideoView videoHolder;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // keep screen on ..
        try{
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (Exception e) {e.printStackTrace();}

        //My code ..
        adView();
        zoom_in_and_out_fast = AnimationUtils.loadAnimation(this, R.anim.zoom_in_and_out_fast);
        start_btn_animation = AnimationUtils.loadAnimation(this, R.anim.start_btn_animation);
        wifi_name = findViewById(R.id.wifi_name);
        ip_number = findViewById(R.id.ip_number);
        mac_number = findViewById(R.id.mac_number);
        signal_number = findViewById(R.id.signal_number);
        router_info = findViewById(R.id.router_info);
        router_info_bg = findViewById(R.id.router_info_bg);
        start_small = findViewById(R.id.start_small);
        devices_num_box = findViewById(R.id.devices_num_box);
        recycler_view = findViewById(R.id.recycler_view);
        scanning = findViewById(R.id.scanning);
        start = findViewById(R.id.start);
        enable_wifi = findViewById(R.id.enable_wifi);
        users_numb = findViewById(R.id.users_numb);
        disconnect = findViewById(R.id.disconnect);
        signal_strength = findViewById(R.id.signal_strength);
        videoHolder=findViewById(R.id.videoHolder);
        more_info = findViewById(R.id.more_info);
        handler = new Handler();
        catcher_arr = new ArrayList<>(); // array of objects to received data ..
        connector = new DataBaseConnector(this);  // initialize object from connector ..

        //Drop database if exist .. in case i update the oui file every month ..
        //put here to create and open copy of database oncreate activity before start searching ..
        try{
            connector.DropDataBase(); //drop database IF NOT EXIST ..
            connector.CreateDataBase(); //create database IF NOT EXIST ..
            connector.OpenDataBase(); //open database ..
        }catch (Exception e){e.printStackTrace();}

        //set up Adapter with recyclerView ..
        custom_adapter =new Adapter(context,catcher_arr); // send context in parameter ..
        recycler_view.setLayoutManager(new LinearLayoutManager(context));
        recycler_view.setAdapter(custom_adapter);

        //show and hide buttons ..
        scanning.setVisibility(View.GONE);
        videoHolder.setVisibility(View.GONE); // hide gif ..
        enable_wifi.setVisibility(View.INVISIBLE); // hide Button Enable WIFI ..
        disconnect.setVisibility(View.GONE); // hide disconnect ..
        recycler_view.setVisibility(View.INVISIBLE); // hide recycler_view ..
        devices_num_box.setVisibility(View.GONE); // hide number of users ..
        router_info.setVisibility(View.GONE); // hide router_info ..
        router_info_bg.setVisibility(View.GONE); // hide router_info_bg ..
        start_small.setVisibility(View.GONE); // hide start_small ..

        //Start Button ..
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(one_click_only == false) {
                    one_click_only = true;

                    //Animation ..
                    try {
                        start.startAnimation(start_btn_animation);
                    } catch (Exception e) {
                    }

                    //counter to clear animation and hide imageview ..
                    try {
                        CountDownTimer = new CountDownTimer(1100, 1100) {

                            @Override
                            public void onFinish() {

                                start.clearAnimation();
                                start.setVisibility(View.GONE);
                            }

                            @Override
                            public void onTick(long arg0) {
                            }
                        }.start();
                    } catch (Exception e) {
                    }

                    main_start = true;

                    //videoHolder start ..
                    try{
                        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"+ R.raw.splash);
                        videoHolder.setVideoURI(video);
                        videoHolder.setZOrderOnTop(true);
                        videoHolder.start();
                    } catch(Exception ex) {}

                    //Start function ..
                    doStart();
                }
            }
        });

        //Start small Button ..
        start_small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                start_small.setVisibility(View.GONE);
                router_info.setVisibility(View.GONE); // hide router_info ..
                router_info_bg.setVisibility(View.GONE); // hide router_info_bg ..


                //videoHolder start ..
                try{
                    Uri video = Uri.parse("android.resource://" + getPackageName() + "/"+ R.raw.splash);
                    videoHolder.setVideoURI(video);
                    videoHolder.setZOrderOnTop(true);
                    videoHolder.start();
                } catch(Exception ex) {}

                //Start function ..
                doStart();
            }
        });

        //more_info Button ..
        more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Animation ..
                try
                {
                    more_info.startAnimation(zoom_in_and_out_fast);
                }catch(Exception e){}

                //open dialog ..
                try {
                    Intent i = new Intent("com.hblackcat.wifiusers.Configurations.MoreInfoDialog");
                    startActivity(i);
                }catch (Exception e){e.printStackTrace();}

                // start full screen ad ..
                try{
                    if (interstitialAd.isLoaded())
                        interstitialAd.show();
                }catch(Exception e){e.printStackTrace();}
            }
        });

        // Button Enable wifi ..
        enable_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); // open wifi settings ..
                }catch (Exception e){e.printStackTrace();}
            }
        });
    }

    // my background ReactX to get all ips connect to my network (used instead of Asynctask) ..
    public void ReactX (final int start_num)
    {
        try {
            Observable<String> observableObj = Observable.fromCallable(new Callable<String>() {
                @Override
                public String call() {

                    start_button_clicked =true; //use it in runnable ..

                    try {
                        host = InetAddress.getByName(new Formatter().intToIp(dhcp.ipAddress));
                        byte[] ip = host.getAddress();

                        ip[3] = (byte) start_num;
                        InetAddress address = InetAddress.getByAddress(ip);

                        //pure ip without '/' ..
                        String pureAddress_1 = address.toString().replace("/","");
                        /* if my ip address .. put here in case can't Reach (the device already connected so must be Reach)..
                            but there some cases like use vpn
                            - get full mac and the first 6 digit pure of mac only to search in db about vendor ..
                        */
                        if(pureAddress_1.equals(String.valueOf(new Formatter().intToIp(dhcp.ipAddress)))) {
                            full_mac = new DefaultRouterInformations(context).checkMyDeviceMacAddr();
                            mac_first_6_digits = full_mac.toString().replace(":", "").toUpperCase(); // filter mac without ":" and to uppercase ..
                            mac_first_6_digits = new Formatter().getFirstSix(mac_first_6_digits); // get first six digits ..
                            //create object from DataCatcher and send new ip & mac & vendor then add this object to DataCatcher arraylist ..
                            catcher_obj = new DataCatcher(pureAddress_1, full_mac, "MY DEVICE"+connector.SearchInDataBase(mac_first_6_digits));
                            catcher_arr.add(catcher_obj);
                        }

                        /* ***ATTENTION not the right solution just (hack) may contain some issues in devices not clear yet***
                            - we used address.isReachable(5000) to search in every ip but this function have some problem
                            with antivirus and some routers and iphones .. but the active ips saved in arp table
                            - so we search using isReachable then serach in arp table to get all active ips ..
                            -check arp will be in (else) in case isReachable can't catch the ip so check arp table
                            - note .. we must use isReachable to refresh arp table
                         */
                        if (address.isReachable(5000)) // 5 sec for best scan ..
                        {
                            //System.out.println(address + " machine is turned on and can be pinged");
                            //pure ip without '/' ..
                            String pureAddress_2 = address.toString().replace("/","");
                            //check if ip equal my device ip get my mac address in another way because it will return zeros..
                            if(!pureAddress_2.equals(String.valueOf(new Formatter().intToIp(dhcp.ipAddress)))) {
                                // if not my ip address ..
                                //get full mac and the first 6 digit pure of mac only to search in db about vendor ..
                                full_mac = new DevicesMacAddresses().getHardwareAddress(pureAddress_2);
                                mac_first_6_digits = full_mac.toString().replace(":","").toUpperCase(); // filter mac without ":" and to uppercase ..
                                mac_first_6_digits = new Formatter().getFirstSix(mac_first_6_digits); // get first six digits ..
                                //create object from DataCatcher and send new ip & mac & vendor then add this object to DataCatcher arraylist ..
                                catcher_obj =new DataCatcher(pureAddress_2,full_mac, connector.SearchInDataBase(mac_first_6_digits) );
                                catcher_arr.add(catcher_obj);
                            }
                        } else {
                            //System.out.println(address + " not pinged");
                            //pure ip without '/' ..
                            String pureAddress_3 = address.toString().replace("/","");
                            //check arp table by search by mac address .. here in case isReachable can't catch the ip .. it will be in arp table..
                            if(!new DevicesMacAddresses().getHardwareAddress(pureAddress_3).equals("00:00:00:00:00:00"))
                            {
                                // if not my ip address ..
                                //get full mac and the first 6 digit pure of mac only to search in db about vendor ..
                                full_mac = new DevicesMacAddresses().getHardwareAddress(pureAddress_3);
                                mac_first_6_digits = full_mac.toString().replace(":","").toUpperCase(); // filter mac without ":" and to uppercase ..
                                mac_first_6_digits = new Formatter().getFirstSix(mac_first_6_digits); // get first six digits ..
                                //create object from DataCatcher and send new ip & mac & vendor then add this object to DataCatcher arraylist ..
                                catcher_obj =new DataCatcher(pureAddress_3,full_mac, connector.SearchInDataBase(mac_first_6_digits) );
                                catcher_arr.add(catcher_obj);
                            }
                        }
                    } catch (Exception e) {e.printStackTrace();}
                    return null;
                }
            });

            Observer<String> observerObj = new Observer<String>() {
                @Override
                public void onCompleted() {
                    try {
                        reactX_counter++;
                        if (reactX_counter == 255) {
                            reactX_counter = 0; // make counter 0
                            if(repeat_method == true) {
                                //start handler to check wifi info every 5 seconds ..
                                try {
                                    //check handler run before or not ..
                                    if (handler_run == false) {
                                        handler_run = true;
                                        handler.post(runnableForEver);
                                    }
                                } catch (Exception e) {
                                }

                                start_button_clicked = false; //use it in runnable..
                                scanning.setVisibility(View.GONE);
                                try{videoHolder.stopPlayback();}catch (Exception d){}
                                videoHolder.setVisibility(View.GONE); // hide gif ..
                                // start full screen admob ..
                                try {
                                    if (interstitialAd.isLoaded())
                                        interstitialAd.show();
                                } catch (Exception e) {e.printStackTrace();}
                                adView.setVisibility(View.VISIBLE); // show admob banner ..
                                start_small.setVisibility(View.VISIBLE); // show start button ..
                                router_info.setVisibility(View.VISIBLE); // show router_info ..
                                router_info_bg.setVisibility(View.VISIBLE); // show router_info_bg ..
                                recycler_view.setVisibility(View.VISIBLE); // show recycler_view ..
                                devices_num_box.setVisibility(View.VISIBLE); // show number of users ..
                                users_numb.setText(catcher_arr.size() + ""); //get number of connected users ..
                                //list sort Ascending by ip ..
                                try {
                                    Collections.sort(catcher_arr, new Comparator<DataCatcher>() {
                                        @Override
                                        public int compare(DataCatcher lhs, DataCatcher rhs) {
                                            return Integer.parseInt(lhs.ip.replace(".", "")) - Integer.parseInt(rhs.ip.replace(".", ""));
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                custom_adapter.notifyDataSetChanged(); //Refresh recyclerView to get all new info from arraylist (DataCatcher) ..
                                subscription.unsubscribe(); // stop subscription to avoid memory leak ..
                                repeat_method = false;
                            }else {
                                repeat_method = true;
                                doStart();
                            }
                        }
                    } catch (Exception e) {e.printStackTrace();}
                }

                @Override
                public void onError(Throwable e) {}

                @Override
                public void onNext(String s) {}
            };

            subscription = observableObj
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observerObj);
        }catch (Exception e){
            //in case error this things important to run ..
            start_button_clicked =false; //use it in runnable
            scanning.setVisibility(View.GONE);
            try{videoHolder.stopPlayback();}catch (Exception d){}
            videoHolder.setVisibility(View.GONE); // hide gif ..
            start_small.setVisibility(View.VISIBLE); // show start button ..
        }
    }

    //check defaultInformation every 5 seconds ..
    private Runnable runnableForEver = new Runnable() {
        @Override
        public void run() {
            try {
                //check and reFresh wifi connection ..
                refreshWiFiConnection();
            }catch (Exception e){}
            handler.postDelayed(runnableForEver, time);
        }
    };

    /*Checking Wifi and check connection ..
      - Initialize ConnectivityManager & NetworkInfo ..
      - Initialize wifi and dhcp ..
      -check all this every 5 second in case onpause or no connection or disable wifi ..
    */
    public void refreshWiFiConnection()
    {
        connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifi.getConnectionInfo();
        dhcp=wifi.getDhcpInfo();
        //Checking Wifi enable or disable ..
        if (wifi.isWifiEnabled() == true) {
            //check wifi connection ..
            if(mWifi.isConnected() == true) {
                //check if we clicked start_small button and ReactX running don't show start_small button ..
                if (start_button_clicked==false) {
                    if(main_start == true) {
                        //get default info ..
                        router_info.setVisibility(View.VISIBLE);
                        router_info_bg.setVisibility(View.VISIBLE);
                        wifi_name.setText(wifiInfo.getSSID());
                        ip_number.setText(String.valueOf(new Formatter().intToIp(dhcp.gateway)));
                        mac_number.setText(new DevicesMacAddresses().getHardwareAddress(String.valueOf(new Formatter().intToIp(dhcp.gateway))));
                        signal_number.setText("(" + wifiInfo.getRssi() + " dBm) ");
                        disconnect.setVisibility(View.GONE);
                        checkSignal(); //check Signal Strength ..
                        if (enable_wifi.getVisibility() == View.VISIBLE) // if enable_wifi was VISIBLE ..
                            enable_wifi.setVisibility(View.INVISIBLE); // hide Button Enable WIFI ..
                        start_small.setVisibility(View.VISIBLE); // show Button start_small ..
                    }else {
                        start.setVisibility(View.VISIBLE);
                        disconnect.setVisibility(View.INVISIBLE);
                        enable_wifi.setVisibility(View.INVISIBLE); // hide Button Enable WIFI ..
                    }
                }
            }else {
                router_info.setVisibility(View.GONE);
                router_info_bg.setVisibility(View.GONE);
                disconnect.setVisibility(View.VISIBLE);
                disconnect.setText("No Connection !");
                start.setVisibility(View.GONE); // hide Button start ..
                start_small.setVisibility(View.INVISIBLE); // hide Button start_small ..
                enable_wifi.setVisibility(View.INVISIBLE); // hide Button Enable WIFI ..
                recycler_view.setVisibility(View.GONE); // hide recycler_view ..
                devices_num_box.setVisibility(View.GONE); // hide number of users ..
                try{videoHolder.stopPlayback();}catch (Exception d){}
                scanning.setVisibility(View.GONE);
                videoHolder.setVisibility(View.GONE);
            }
        }else {
            router_info.setVisibility(View.GONE);
            router_info_bg.setVisibility(View.GONE);
            disconnect.setVisibility(View.VISIBLE);
            disconnect.setText("Wifi Is Disabled !");
            start.setVisibility(View.GONE); // hide Button start ..
            start_small.setVisibility(View.INVISIBLE); // hide Button start_small ..
            enable_wifi.setVisibility(View.VISIBLE); // show Button Enable WIFI ..
            recycler_view.setVisibility(View.GONE); // hide recycler_view ..
            devices_num_box.setVisibility(View.GONE); // hide number of users ..
            try{videoHolder.stopPlayback();}catch (Exception d){}
            scanning.setVisibility(View.GONE);
            videoHolder.setVisibility(View.GONE);
        }
    }

    //check Signal Strength ..
    public void checkSignal()
    {
        try {
            signal = new SignalStrength(this).checkDBM();
            if(signal.equals("excellent")) {
                signal_strength.setImageDrawable(getResources().getDrawable(R.drawable.excellent));
            }else if (signal.equals("good")) {
                signal_strength.setImageDrawable(getResources().getDrawable(R.drawable.good));
            }else if (signal.equals("fair")) {
                signal_strength.setImageDrawable(getResources().getDrawable(R.drawable.fair));
            }else if (signal.equals("weak")) {
                signal_strength.setImageDrawable(getResources().getDrawable(R.drawable.weak));
            }else {
                signal_strength.setImageDrawable(getResources().getDrawable(R.drawable.weak));
            }
        }catch (Exception e){}
    }

    //Start buttons functions
    public void doStart()
    {
        // clear ArrayList in case it run before .. clear better and faster than removeAll() method ..
        try{
            catcher_arr.clear();
            //put notifyDataSetChanged() here in case the user scroll down the recyclerview don't start from the same place and start from the top ..
            custom_adapter.notifyDataSetChanged(); //Refresh recyclerView to get all new info from arraylist (DataCatcher) ..
        }catch (Exception e){e.printStackTrace();}

        try{
            //Initialize wifi and dhcp here again because in net cut situation we need to re-initialize ..
            wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            dhcp=wifi.getDhcpInfo();

            //show and hide buttons ..
            adView.setVisibility(View.INVISIBLE); // hide admob banner ..
            videoHolder.setVisibility(View.VISIBLE); // Show gif ..
            scanning.setVisibility(View.VISIBLE);
            start_small.setVisibility(View.INVISIBLE); // hide start button ..
            recycler_view.setVisibility(View.INVISIBLE); // hide recycler_view ..
            devices_num_box.setVisibility(View.INVISIBLE); // hide number of users ..

            //connected devices ..
            // check 254 ip in 255 ReactX start with 0 ..
            ReactX(method_start); // start with 0 ..
            for(int i =1;i<=254;i++)
            {
                ReactX(i);
            }
        }catch (Exception e){e.printStackTrace();}
    }

    //onResume ..
    @Override
    protected void onResume() {
        super.onResume();
        try{
            //here one time in case runnable not work or crashed or any problem ..
            // *** BUT WE DON'T NEED TO CALL IT HERE ***
            refreshWiFiConnection();

            //load ad..
            try {
                //here put Your Ad:
                interstitialAd = new InterstitialAd(context);
                interstitialAd.setAdUnitId(context.getString(R.string.admob_full));
                //Load Ad ..
                AdRequest adRequest = new AdRequest.Builder().build();
                interstitialAd.loadAd(adRequest);
            } catch (Exception e) {}
        }catch (Exception e){e.printStackTrace();}
    }

    //show_banner_Ad ..
    public void adView()
    {
        try{
            adView = this.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }catch(Exception e){e.printStackTrace();}
    }

    //onDestroy ..
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            connector.CloseDataBase(); //close database ..
            subscription.unsubscribe(); // stop subscription to avoid memory leak ..
        }catch (Exception e){System.out.println("eee"+e.getMessage());}
        //we divide method here between to catchers to avoid crash happening by unsubscribe() in case we didn't call ReactX method ..
        try{
            catcher_arr.clear(); // clear ArrayList in case it run before .. clear better and faster than removeAll() method ..
            handler.removeCallbacks(runnableForEver); // finish handler ..
        }catch (Exception e){System.out.println("eee"+e.getMessage());}
    }
}
