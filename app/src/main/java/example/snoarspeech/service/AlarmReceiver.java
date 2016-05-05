package example.snoarspeech.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import example.snoarspeech.activity.AlarmActivity;


/**
 * Created by owen_ on 2016-04-22.
 */
public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AlarmActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle getBundle = intent.getExtras();
        Bundle sendBundle = new Bundle();
        System.out.println(getBundle.get("memorandum"));
        sendBundle.putString("memorandum",getBundle.getString("memorandum"));
        i.putExtras(sendBundle);
        context.startActivity(i);
    }
}

