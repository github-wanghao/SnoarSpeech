package example.snoarspeech.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import example.snoarspeech.R;
import example.snoarspeech.adapter.MyAdspter;
import example.snoarspeech.database.DBManager;
import example.snoarspeech.model.AlarmClock;
import example.snoarspeech.utils.AlarmUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by owen_ on 2016-04-21.
 */
public class AlarmClockMainActivity extends Activity {

    private DBManager dbManager;
    private ListView listView = null;
    private AlarmUtils alarmUtils = new AlarmUtils();
    private TextView totalTitle;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alarmclock_main);

        Button returnButton = (Button) findViewById(R.id.alarmClockMainReturnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent().setClass(AlarmClockMainActivity.this, MainActivity.class));
                AlarmClockMainActivity.this.finish();
            }
        });

        Button addButton = (Button) findViewById(R.id.alarmClockMainAddButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(AlarmClockMainActivity.this, AlarmClockSettingActivity.class);

                Bundle bundle = new Bundle();
                bundle.putInt("position", -1);

                intent.putExtras(bundle);

                startActivity(intent);
                AlarmClockMainActivity.this.finish();
            }
        });

        totalTitle = (TextView)findViewById(R.id.alarmTitle);
        totalTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.deleteAllData();
                Toast.makeText(AlarmClockMainActivity.this, "数据删除成功", Toast.LENGTH_LONG).show();//提示用户
            }
        });

        dbManager = new DBManager(this);
        listView = (ListView) findViewById(R.id.alarmClockList);
        List<Map<String, Object>> list = getData(dbManager.queryAlarm());
        listView.setAdapter(new MyAdspter(this, list));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                intent.setClass(AlarmClockMainActivity.this, AlarmClockSettingActivity.class);

                Bundle bundle = new Bundle();
                bundle.putInt("position", position);

                intent.putExtras(bundle);
                startActivity(intent);
                AlarmClockMainActivity.this.finish();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public List<Map<String, Object>> getData(List<AlarmClock> alarmClockData) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (AlarmClock alarmClock : alarmClockData)
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", R.drawable.alarm_clock);
            map.put("title", alarmClock.alarmName);
            map.put("text", alarmUtils.hasZero(alarmClock.hour) + ":" + alarmUtils.hasZero(alarmClock.minute));
            list.add(map);
        }

        return list;
    }


}


