package com.hblackcat.wifiusers.DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/com.hblackcat.wifiusers/databases/"; // path of db file ..
    private static final int DATABASE_VERSION = 1; // Database Version ..
    private static final String DATABASE_NAME = "oui.db"; // Database Name ..
    private Context myContext;
    private SQLiteDatabase myDataBase;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    //implementation method ..
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    //implementation method ..
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //Creates a empty database on the system and rewrites it with your own database.
    public void createDataBase(){

        //check database exist ..
        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (Exception e) {}
        }
    }

    // Check if the database already exist ..@return true if it exists, false if it doesn't
    private boolean checkDataBase(){
        File dbFile = myContext.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() {
        try {
            //Open your local db as the input stream
            InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

            // Path to the just created empty db
            String outFileName = DB_PATH + DATABASE_NAME;

            //Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }catch (Exception e){}
    }

    //open database ..
    public void openDataBase(){
        //Open the database
        String myPath = DB_PATH + DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    //Search in DataBase ..
    public String getCursor(String mac) {
        try
        {
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder ();

            //table name ..
            queryBuilder.setTables("oui_table");

            //String  asColumnsToReturn[] = new String [] {"field1", "field2"}; columns name ..

            // Search in column 1 where mac like ..
            String selectQuery = " select * from oui_table where field1 like  '%" + mac +"%'";

            // Initialize Cursor ..
            Cursor mCursor = myDataBase.rawQuery(selectQuery, null);

            //Search in all table start from the top of column ..
            if(mCursor.moveToFirst())
            {
                do{
                    return mCursor.getString(mCursor.getColumnIndex("field2"));
                }while (mCursor.moveToNext());
            }
        }catch (Exception e){}
        return "Vendor not found !";
    }

    //Drop database if exist .. in case i update the oui file every month ..
    public void dropDataBase()
    {
        //check database exist ..
        boolean dbExist = checkDataBase();

        if(dbExist) {
            //database already exist .. delete it
            myContext.deleteDatabase(DATABASE_NAME);
        }
    }

    //close database ..
    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();
        super.close();

    }

}