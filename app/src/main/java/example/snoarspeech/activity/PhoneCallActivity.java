package example.snoarspeech.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import example.snoarspeech.R;
import example.snoarspeech.service.CallAction;
import example.snoarspeech.service.OnlineSpeechAction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by owen_ on 2016-04-29.
 */
public class PhoneCallActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.phone_call_activity);

        final OnlineSpeechAction vbutton = new OnlineSpeechAction(PhoneCallActivity.this);
        vbutton.initIflytek();
        vbutton.speechRecognition();
        final EditText editText = (EditText)findViewById(R.id.callphoneEditText);
        Button btnCall = (Button)findViewById(R.id.callPhoneButton);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Phone = editText.getText().toString();
                Pattern Num = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
                Matcher m = Num.matcher(Phone);
                if(m.matches() ){
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Phone));
                    startActivity(intent);
                }else{

                    CallAction callAction = new CallAction(Phone, null, PhoneCallActivity.this, vbutton);
                    callAction.start();
                }
            }
        });

        Button btnReturn = (Button)findViewById(R.id.callPhoneReturnButton);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent().setClass(PhoneCallActivity.this, PhoneActivity.class));
                PhoneCallActivity.this.finish();
            }
        });
    }
}

