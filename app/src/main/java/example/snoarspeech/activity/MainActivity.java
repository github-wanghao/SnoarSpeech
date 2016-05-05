package example.snoarspeech.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import example.snoarspeech.IsNetWorkConnected;
import example.snoarspeech.R;
import example.snoarspeech.service.OfflineSpeechAction;
import example.snoarspeech.service.OnlineSpeechAction;


public class MainActivity extends Activity {

    private MediaPlayer player;

    private Toast info;
    private boolean isnet;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        info=Toast.makeText(this, "", Toast.LENGTH_SHORT);

        OnlineSpeechAction vbutton = new OnlineSpeechAction(this);
        OfflineSpeechAction voicebutton = new OfflineSpeechAction(this);
        showTip("开始语音");

        IsNetWorkConnected isNetWorkConnected = new IsNetWorkConnected();
        isnet = isNetWorkConnected.net(this);
        if(isnet == true){
        vbutton.initIflytek();
        vbutton.initUI();
        vbutton.speechRecognition();
        }else{
            Log.v("?????", "??????");
            voicebutton.initIflytek();
            voicebutton.initUI();
            voicebutton.speechRecognition();
        }
        showTip("初始化完成");
        player = MediaPlayer.create(MainActivity.this, R.raw.lock);
        player.start();


//            vbutton.beginSpeak("您好，我是Sonar，您的智能语音助手", false);

    }


    private void showTip(final String str) {
        info.setText(str);
        info.show();
    }


}

