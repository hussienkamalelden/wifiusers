package com.hblackcat.wifiusers.DataBase;

import android.content.Context;

public class DataBaseConnector {

    private MySQLiteHelper myDbHelper; // object from database ..
    Context context;

    public DataBaseConnector(Context contexts)
    {
        this.context = contexts;
        myDbHelper = new MySQLiteHelper(context); // initialize object from database ..
    }

    //create Database ..
    public void CreateDataBase()
    {
        try {
            myDbHelper.createDataBase();
        } catch (Exception e) {}
    }

    //open Database ..
    public void OpenDataBase()
    {
        try {
            myDbHelper.openDataBase();
        } catch (Exception e) {}
    }

    //Search in Database about vendor by first 6 digits in mac address in uppercase like (D4823E) ..
    public String SearchInDataBase(String mac)
    {
        try {
            return myDbHelper.getCursor(mac);
        } catch (Exception e) {}
        return "Vendor not found !";
    }

    //Drop Database ..
    public void DropDataBase()
    {
        try {
            myDbHelper.dropDataBase();
        } catch (Exception e) {}
    }

    //close Database ..
    public void CloseDataBase()
    {
        try {
            myDbHelper.close();
        } catch (Exception e) {}
    }
}
