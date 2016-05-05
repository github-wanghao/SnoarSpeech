package example.snoarspeech.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by owen_ on 2016-04-27.
 */
public class DatabaseHelper extends SQLiteOpenHelper{


    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "TestDB.db";


    public static final String ALARM_TABLE_NAME = "AlarmTable";
    public static final String CALL_RECORDS_TABLE_NAME = "CallRecordsTable";
    public static final String MASSAGE_RECORDS_TABLE_NAME = "MassageRecordsTable";



    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version, DatabaseErrorHandler errorHandler)
    {
        super(context, name, factory, version, errorHandler);

    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version)
    {
        super(context, name, factory, version);

    }

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);


    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {


//        Log.d(AppConstants.LOG_TAG, "DatabaseHelper onCreate");


        StringBuffer alarmSB = new StringBuffer();
        StringBuffer callRecordSB = new StringBuffer();
        StringBuffer massageRecordSB = new StringBuffer();


        alarmSB.append("CREATE TABLE [" + ALARM_TABLE_NAME + "] (");
        alarmSB.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        alarmSB.append("[name] TEXT,");
        alarmSB.append("[hour] INTEGER,");
        alarmSB.append("[minute] INTEGER,");
        alarmSB.append("[memorandum] TEXT)");

        callRecordSB.append("CREATE TABLE [" + CALL_RECORDS_TABLE_NAME + "] (");
        callRecordSB.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        callRecordSB.append("[name] TEXT,");
        callRecordSB.append("[phonenumber] INTEGER,");
        callRecordSB.append("[time] TEXT)");


        massageRecordSB.append("CREATE TABLE [" + MASSAGE_RECORDS_TABLE_NAME + "] (");
        massageRecordSB.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        massageRecordSB.append("[name] TEXT,");
        massageRecordSB.append("[phonenumber] INTEGER,");
        massageRecordSB.append("[content] TEXT,");
        massageRecordSB.append("[time] TEXT)");



        db.execSQL(alarmSB.toString());
        db.execSQL(callRecordSB.toString());
        db.execSQL(massageRecordSB.toString());



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {


//        Log.d(AppConstants.LOG_TAG, "DatabaseHelper onUpgrade");

        db.execSQL("DROP TABLE IF EXISTS " + ALARM_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CALL_RECORDS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MASSAGE_RECORDS_TABLE_NAME);
        onCreate(db);


    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);


//        Log.d(AppConstants.LOG_TAG, "DatabaseHelper onOpen");
    }


}

