package example.snoarspeech.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


import example.snoarspeech.R;
import example.snoarspeech.database.DBManager;
import example.snoarspeech.model.AlarmClock;
import example.snoarspeech.service.AlarmReceiver;
import example.snoarspeech.utils.AlarmUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by owen_ on 2016-04-22.
 */
public class AlarmClockSettingActivity extends Activity{

    EditText alarmName,memorandum;

    private Button btn = null,comfirmButton = null,deleteButton = null;
    private EditText alarmTime = null;
    private AlarmManager alarmManager=null;
    Calendar cal= Calendar.getInstance();
    Calendar hasSet = Calendar.getInstance();
    final int DIALOG_TIME = 0;    //璁剧疆瀵硅瘽妗唅d
    int position;

    Bundle bundle;
    DBManager dbManager;

    AlarmUtils alarmUtils = new AlarmUtils();


    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alarmclock_setting);

        dbManager = new DBManager(this);

        bundle = this.getIntent().getExtras();

        position = bundle.getInt("position");

        alarmName = (EditText)findViewById(R.id.alarmClockNameEditText);
        memorandum = (EditText)findViewById(R.id.alarmClockMemorandumEditText);

        alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmTime = (EditText)findViewById(R.id.alarmClockTEditText);
        alarmTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDialog(DIALOG_TIME);
            }
        });

        Button returnButton = (Button)findViewById(R.id.alarmClockSettingReturnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent().setClass(AlarmClockSettingActivity.this, AlarmClockMainActivity.class));
                AlarmClockSettingActivity.this.finish();
            }
        });

        comfirmButton = (Button)findViewById(R.id.alarmClockSettingComfirmButton);
        comfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(position == -1)
                {
                    int id = dbManager.queryAlarm().size() + 1;

                    Intent intent = new Intent(AlarmClockSettingActivity.this, AlarmReceiver.class);
                    PendingIntent pi = PendingIntent.getBroadcast(AlarmClockSettingActivity.this, id, intent, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, hasSet.getTimeInMillis(), pi);


                    Bundle memoBundle = new Bundle();
                    memoBundle.putString("memorandum",memorandum.getText().toString());
                    intent.putExtras(memoBundle);

                    dataInsert(id, alarmName.getText().toString(), hasSet, memorandum.getText().toString());
                    Toast.makeText(AlarmClockSettingActivity.this, "闹钟设置成功", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(AlarmClockSettingActivity.this, AlarmReceiver.class);
                    PendingIntent pi = PendingIntent.getBroadcast(AlarmClockSettingActivity.this, position + 1, intent, 0);
                    alarmManager.cancel(pi);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, hasSet.getTimeInMillis(), pi);

                    Bundle memoBundle = new Bundle();
                    memoBundle.putString("memorandum",memorandum.getText().toString());
                    intent.putExtras(memoBundle);

                    dataUpdate(position,alarmName.getText().toString(), hasSet, memorandum.getText().toString());
                    Toast.makeText(AlarmClockSettingActivity.this, "闹钟修改成功", Toast.LENGTH_LONG).show();
                }
                startActivity(new Intent().setClass(AlarmClockSettingActivity.this, AlarmClockMainActivity.class));
                AlarmClockSettingActivity.this.finish();
            }
        });

        deleteButton = (Button)findViewById(R.id.alarmClockDeleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(position == -1)
                {
                    Toast.makeText(AlarmClockSettingActivity.this, "闹钟还没创建，不能删除", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(AlarmClockSettingActivity.this, AlarmReceiver.class);
                    PendingIntent pi = PendingIntent.getBroadcast(AlarmClockSettingActivity.this, position + 1, intent, 0);
                    alarmManager.cancel(pi);

                    dbManager.deleteOneAlarm(position);
                    Toast.makeText(AlarmClockSettingActivity.this, "闹钟删除成功", Toast.LENGTH_LONG).show();
                    startActivity(new Intent().setClass(AlarmClockSettingActivity.this, AlarmClockMainActivity.class));
                    AlarmClockSettingActivity.this.finish();
                }

            }
        });

//        editText.setText(String.valueOf(bundle.getInt("position")));

        loadPage(position);









    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog=null;

        switch (id) {
            case DIALOG_TIME:
                dialog = new TimePickerDialog(
                        this,
                        new TimePickerDialog.OnTimeSetListener(){
                            public void onTimeSet(TimePicker timePicker, int hourOfDay,int minute) {
                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                c.set(Calendar.SECOND, 0);
                                c.set(Calendar.MILLISECOND, 0);
                                hasSet = c;

//                                alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
//                                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
//                                Toast.makeText(AlarmClockSettingActivity.this, "闂归挓璁剧疆鎴愬姛", Toast.LENGTH_LONG).show();
                                alarmTime.setText(alarmUtils.hasZero(c.get(Calendar.HOUR_OF_DAY))+":"+alarmUtils.hasZero(c.get(Calendar.MINUTE)));
                                System.out.println("设置的时间为" + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
                            }
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true);


                break;
        }
        return dialog;
    }

    public void dataInsert(int id,String alarmName, Calendar cal, String memorandum)
    {
        ArrayList<AlarmClock> alarmClockData = new ArrayList<>();
        AlarmClock alarmClock = new AlarmClock(id,alarmName + id,cal,memorandum);

        alarmClockData.add(alarmClock);
        dbManager.addAlarm(alarmClockData);
    }

    public void dataUpdate(int position,String alarmName, Calendar cal, String memorandum)
    {
        ArrayList<AlarmClock> alarmClockData = new ArrayList<>();

        AlarmClock alarmClock = new AlarmClock(position + 1,alarmName + (position + 1),cal,memorandum);
        alarmClockData.add(alarmClock);
        dbManager.updateAlarm(alarmClockData);
    }

    public void loadPage(int position)
    {
        if(position == -1)
        {
            alarmName.setText("闹钟" + (dbManager.queryAlarm().size() + 1));
            alarmTime.setText(alarmUtils.hasZero(hasSet.get(Calendar.HOUR_OF_DAY)) + ":" + alarmUtils.hasZero(hasSet.get(Calendar.MINUTE)));
        }
        else {
            Map<String,String> alarmClockData = new HashMap();
            alarmClockData = dbManager.queryOneAlarm(position + 1);
            alarmName.setText(alarmClockData.get("alarmName"));
            alarmTime.setText(alarmClockData.get("hour") + ":" + alarmClockData.get("minute"));
            hasSet.set(Calendar.HOUR_OF_DAY, Integer.valueOf(alarmClockData.get("hour")).intValue());
            hasSet.set(Calendar.MINUTE, Integer.valueOf(alarmClockData.get("minute")).intValue());
            memorandum.setText(alarmClockData.get("memorandum"));
        }
    }
}

