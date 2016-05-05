package example.snoarspeech.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by owen_ on 2016-04-22.
 */
public class AlarmActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getIntent().getExtras();


        new AlertDialog.Builder(AlarmActivity.this).
                setTitle("闹钟").
                setMessage(bundle.getString("memorandum")).
                setPositiveButton("知道了", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        AlarmActivity.this.finish();//鍏抽棴Activity
                    }
                }).create().show();


    }
}

