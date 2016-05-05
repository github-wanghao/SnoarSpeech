package example.snoarspeech.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import example.snoarspeech.R;
import example.snoarspeech.database.DBManager;
import example.snoarspeech.model.MassageRecord;
import example.snoarspeech.service.OnlineSpeechAction;


import java.util.ArrayList;
import java.util.List;
import com.andexert.expandablelayout.library.ExpandableLayout;

/**
 * Created by owen_ on 2016-03-22.
 */
public class MassegeActivity extends Activity{

    int j ,i;

    private DBManager dbManager;
    private List<MassageRecord> massageRecords = new ArrayList<>();
    Bundle bundle;

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);;
        setContentView(R.layout.massege_activity);
        final OnlineSpeechAction vbutton = new OnlineSpeechAction(MassegeActivity.this);
        vbutton.initIflytek();
        vbutton.initUI();
        vbutton.speechRecognition();

//        RelativeLayout RL = (RelativeLayout)findViewById(R.id.massegeContentRelativeLayout);
        TextView textView = new TextView(this);
        textView.setText("你好");



        Button returnButton = (Button)findViewById(R.id.massegeReturnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent().setClass(MassegeActivity.this, MainActivity.class));
                MassegeActivity.this.finish();
            }
        });

        Button addButton = (Button)findViewById(R.id.MassageAddButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MassegeActivity.this,MassageEditActivity.class);

                bundle = new Bundle();
                bundle.putInt("tag", -1);
                intent.putExtras(bundle);
                startActivity(intent);
                MassegeActivity.this.finish();
            }
        });

        dbManager = new DBManager(this);

        massageRecords = dbManager.queryMassageRecords();


        ExpandableLayout EL = (ExpandableLayout)findViewById(R.id.second1);

        if(massageRecords.isEmpty())
        {
            EL.setVisibility(View.GONE);
        }
        else
        {
            i = 1;
            j = 0;
            LinearLayout callRecLinearLayout = (LinearLayout)findViewById(R.id.massageRecLayout);
            RelativeLayout[] callRecRelativeLayout = new RelativeLayout[50];
            final TextView[] nameTextView = new TextView[50];
            final TextView[] phoneTextView = new TextView[50];
            final TextView[] timeTextView = new TextView[50];

            RelativeLayout.LayoutParams[] rules = new RelativeLayout.LayoutParams[200];

//            for (int x = callRecords.size() - 1; x >= 0; x --)
            for (int x = 0; x < massageRecords.size(); x++)
            {

                callRecRelativeLayout[j] = new RelativeLayout(this);
                rules[i] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                callRecRelativeLayout[j].setId(i);
                callRecRelativeLayout[j].setTag(i);
                if (j%2==0)
                {
                    callRecRelativeLayout[j].setBackgroundColor(Color.parseColor("#01579B"));
                }
                else
                {
                    callRecRelativeLayout[j].setBackgroundColor(Color.parseColor("#CCCCFF"));
                }
                i++;

                nameTextView[j] = new TextView(this);
                rules[i] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
//            rules[i].addRule(RelativeLayout.ALIGN_PARENT_TOP);
                nameTextView[j].setText(massageRecords.get(x).contactName + massageRecords.get(x).id);
//            nameTextView[j].setTextSize(25.0f);
                nameTextView[j].setId(i);
                nameTextView[j].setTag(x + 1);
                i++;

                phoneTextView[j] = new TextView(this);
                rules[i] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                rules[i] .addRule(RelativeLayout.BELOW, i - 1);
                phoneTextView[j].setText(massageRecords.get(x).phoneNumber);
//            nameTextView[j].setTextSize(20.0f);
                phoneTextView[j].setId(i);
                i++;

                timeTextView[j] = new TextView(this);
                rules[i] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                rules[i] .addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                rules[i].addRule(RelativeLayout.CENTER_VERTICAL);
                timeTextView[j].setText(massageRecords.get(x).sendTime);
//            nameTextView[j].setTextSize(20.0f);
                timeTextView[j].setId(i);
                i++;

                callRecRelativeLayout[j].addView(nameTextView[j],rules[i-3]);
                callRecRelativeLayout[j].addView(phoneTextView[j],rules[i-2]);
                callRecRelativeLayout[j].addView(timeTextView[j],rules[i-1]);
                callRecLinearLayout.addView(callRecRelativeLayout[j], rules[i - 4]);

                callRecRelativeLayout[j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int tag = (Integer)v.getTag();

//                        TextView chooseText = (TextView)findViewById(R.id.chooseTextMa);
//                        TextView tv1 = (TextView)findViewById(tag+1);
//                        TextView tv2 = (TextView)findViewById(tag+2);
//                        TextView tv3 = (TextView)findViewById(tag+3);
//                        chooseText.setText(tv1.getText()+"@"+tv2.getText()+"@"+ tv3.getText()+"@"+ tv1.getTag());
//
//                        Intent intent = new Intent(MassegeActivity.this,MassageEditActivity.class);
//
//                        bundle = new Bundle();
//                        bundle.putInt("tag", (Integer) tv1.getTag());
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                        MassegeActivity.this.finish();

                    }
                });

                j++;
            }
        }

    }
}

