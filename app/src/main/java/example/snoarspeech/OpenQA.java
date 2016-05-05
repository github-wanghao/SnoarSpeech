package example.snoarspeech;

import android.content.Context;

import example.snoarspeech.service.OnlineSpeechAction;

public class OpenQA {

    private String mText;
    OnlineSpeechAction onlineSpeechAction;
    Context ctx;
    public OpenQA(String text,Context context,OnlineSpeechAction activity){
        this.ctx = context;
        mText=text;
        onlineSpeechAction=activity;
    }

    public void start(){
        onlineSpeechAction.speak(mText, false,ctx);
    }

}
