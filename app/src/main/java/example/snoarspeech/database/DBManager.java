package example.snoarspeech.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import example.snoarspeech.model.AlarmClock;
import example.snoarspeech.model.CallRecord;
import example.snoarspeech.model.MassageRecord;
import example.snoarspeech.utils.AlarmUtils;

/**
 * Created by owen_ on 2016-04-27.
 */
public class DBManager {

    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context)
    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> Constructor");
        helper = new DatabaseHelper(context);

        db = helper.getWritableDatabase();
    }

    //************************************** Alarm Part *****************************************************

    public void addAlarm(List<AlarmClock> alarm)
    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> add");

        db.beginTransaction();
        try
        {
            for (AlarmClock alarmClock : alarm)
            {
                db.execSQL("INSERT INTO " + DatabaseHelper.ALARM_TABLE_NAME
                        + " VALUES(?, ?, ?, ?, ?)", new Object[] { alarmClock.id,
                        alarmClock.alarmName, alarmClock.hour ,alarmClock.minute, alarmClock.memorandum});

            }
            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }

    public void updateAlarm(List<AlarmClock> alarm)
    {
        for (AlarmClock alarmClock : alarm)
        {
            ContentValues cv = new ContentValues();
            cv.put("name",alarmClock.alarmName);
            cv.put("hour",alarmClock.hour);
            cv.put("minute",alarmClock.minute);
            cv.put("memorandum",alarmClock.memorandum);
            db.update(DatabaseHelper.ALARM_TABLE_NAME, cv, "_id = ?",
                    new String[]{String.valueOf(alarmClock.id)});

        }
    }
    //
    public List<AlarmClock> queryAlarm()
    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> query");

        ArrayList<AlarmClock> alarmClocks = new ArrayList<AlarmClock>();
        Cursor c = queryTheCursor();
//        System.out.println(c.moveToNext());

        while (c.moveToNext())
        {
            AlarmClock alarmClock = new AlarmClock();
            alarmClock.id = c.getInt(c.getColumnIndex("_id"));
            alarmClock.alarmName = c.getString(c.getColumnIndex("name"));
            alarmClock.hour = c.getInt(c.getColumnIndex("hour"));
            alarmClock.minute = c.getInt(c.getColumnIndex("minute"));
            alarmClock.memorandum = c.getString(c.getColumnIndex("memorandum"));
            alarmClocks.add(alarmClock);
        }
        c.close();
        return alarmClocks;
    }

    public Cursor queryTheCursor()
    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> queryTheCursor");
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.ALARM_TABLE_NAME,
                null);
        return c;
    }

    public Map<String,String> queryOneAlarm(int id)
    {
        Map<String,String> alarmClockData = new HashMap<>();
        AlarmUtils alarmUtils = new AlarmUtils();
        AlarmClock alarmClock = new AlarmClock();
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.ALARM_TABLE_NAME + " WHERE _id = ?",
                new String[]{String.valueOf(id)});

        c.moveToNext();
        alarmClockData.put("id", String.valueOf(id));
        alarmClockData.put("alarmName",c.getString(c.getColumnIndex("name")));
        alarmClockData.put("hour",alarmUtils.hasZero(c.getInt(c.getColumnIndex("hour"))));
        alarmClockData.put("minute",alarmUtils.hasZero(c.getInt(c.getColumnIndex("minute"))));
        alarmClockData.put("memorandum", c.getString(c.getColumnIndex("memorandum")));

        return alarmClockData;
    }

    public void deleteAllData()
    {
        db.execSQL("DELETE FROM " + DatabaseHelper.ALARM_TABLE_NAME);
    }

    public void deleteOneAlarm(int position)
    {
        db.delete(DatabaseHelper.ALARM_TABLE_NAME, "_id = ?", new String[]{String.valueOf(position + 1)});

        for(int i = position + 1; i <= queryAlarm().size() ; i++)
        {
            ContentValues cv = new ContentValues();
            cv.put("_id",i);
            db.update(DatabaseHelper.ALARM_TABLE_NAME, cv, "_id = ?",new String[]{String.valueOf(i + 1)});
        }

    }



    public void addAPhoneRecord(List<CallRecord> callRecords)
    {
        //        Log.d(AppConstants.LOG_TAG, "DBManager --> add");

        db.beginTransaction();
        try
        {
            for (CallRecord callRecord : callRecords)
            {
                db.execSQL("INSERT INTO " + DatabaseHelper.CALL_RECORDS_TABLE_NAME
                        + " VALUES(?, ?, ?, ?)", new Object[] { callRecord.id,
                        callRecord.contactName, callRecord.phoneNumber ,callRecord.callTime});

            }
            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }

    public List<CallRecord> queryPhoneRecords()
    {
        ArrayList<CallRecord> callRecords = new ArrayList<>();
        Cursor c =queryPhoneRecordsCursor();


        while (c.moveToNext())
        {
            CallRecord callRecord = new CallRecord();
            callRecord.id = c.getInt(c.getColumnIndex("_id"));
            callRecord.contactName = c.getString(c.getColumnIndex("name"));
            callRecord.phoneNumber = c.getString(c.getColumnIndex("phonenumber"));
            callRecord.callTime = c.getString(c.getColumnIndex("time"));
            callRecords.add(callRecord);
        }
        c.close();
        return callRecords;
    }

    public Cursor queryPhoneRecordsCursor()
    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> queryTheCursor");
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.CALL_RECORDS_TABLE_NAME,
                null);
        return c;
    }

    public void updateCallRecordID(int id)
    {
        ContentValues cv = new ContentValues();
        cv.put("_id",id + 1);
        db.update(DatabaseHelper.CALL_RECORDS_TABLE_NAME, cv, "_id = ?",
                new String[]{String.valueOf(id)});
    }

    public void deleteAllCallRecords()
    {
        db.execSQL("DELETE FROM " + DatabaseHelper.CALL_RECORDS_TABLE_NAME);
    }



    //******************************************** Massage Part **************************************************

    public void addAMassageRecord(List<MassageRecord> massageRecords)
    {
        //        Log.d(AppConstants.LOG_TAG, "DBManager --> add");

        db.beginTransaction();
        try
        {
            for (MassageRecord massageRecord : massageRecords)
            {
                db.execSQL("INSERT INTO " + DatabaseHelper.MASSAGE_RECORDS_TABLE_NAME
                        + " VALUES(?, ?, ?, ?, ?)", new Object[] { massageRecord.id,
                        massageRecord.contactName, massageRecord.phoneNumber, massageRecord.massageContent ,massageRecord.sendTime});

            }
            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }

    public List<MassageRecord> queryMassageRecords()
    {
        ArrayList<MassageRecord> massageRecords = new ArrayList<>();
        Cursor c =queryMassageRecordsCursor();


        while (c.moveToNext())
        {
            MassageRecord massageRecord = new MassageRecord();
            massageRecord.id = c.getInt(c.getColumnIndex("_id"));
            massageRecord.contactName = c.getString(c.getColumnIndex("name"));
            massageRecord.phoneNumber = c.getString(c.getColumnIndex("phonenumber"));
            massageRecord.massageContent = c.getString(c.getColumnIndex("content"));
            massageRecord.sendTime = c.getString(c.getColumnIndex("time"));

            massageRecords.add(massageRecord);
        }
        c.close();
        return massageRecords;
    }

    public Cursor queryMassageRecordsCursor()
    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> queryTheCursor");
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.MASSAGE_RECORDS_TABLE_NAME,
                null);
        return c;
    }

    public Map<String,String> queryOneMassageRecord(int id)
    {
        Map<String,String> massageRecordData = new HashMap<>();

        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.MASSAGE_RECORDS_TABLE_NAME + " WHERE _id = ?",
                new String[]{String.valueOf(id)});

        c.moveToNext();
        massageRecordData.put("id", String.valueOf(id));
        massageRecordData.put("contactName",c.getString(c.getColumnIndex("name")));
        massageRecordData.put("phonenumber",c.getString(c.getColumnIndex("phonenumber")));
        massageRecordData.put("content",c.getString(c.getColumnIndex("content")));
        massageRecordData.put("time", c.getString(c.getColumnIndex("time")));

        return massageRecordData;
    }

    public void updateSendRecordID(int id)
    {
        ContentValues cv = new ContentValues();
        cv.put("_id",id + 1);
        db.update(DatabaseHelper.MASSAGE_RECORDS_TABLE_NAME, cv, "_id = ?",
                new String[]{String.valueOf(id)});
    }

    public void deleteAllMassageRecords()
    {
        db.execSQL("DELETE FROM " + DatabaseHelper.MASSAGE_RECORDS_TABLE_NAME);
    }


    public void closeDB()
    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> closeDB");

        db.close();
    }
}
