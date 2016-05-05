package example.snoarspeech.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import example.snoarspeech.JsonParser;
import example.snoarspeech.activity.MainActivity;
import example.snoarspeech.activity.MassageEditActivity;
import example.snoarspeech.OpenQA;
import example.snoarspeech.activity.NewsActivity;
import example.snoarspeech.activity.PhoneCallActivity;
import example.snoarspeech.activity.WeatherActivity;
import example.snoarspeech.adapter.ChatMsgViewAdapter;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import example.snoarspeech.R;

public class OnlineSpeechAction implements AdapterView.OnItemClickListener,View.OnClickListener {
    private Context ctx;
    public OnlineSpeechAction(Context context){
        ctx = context;
        info= Toast.makeText(ctx, "", Toast.LENGTH_SHORT);
        if(setTopBox == null){
            setTopBox = new SetTopBox();
            setTopBox.openSocket();
        }
    }
    private SpeechSynthesizer mTts;
    private String voicer="xiaoyan";

    private LocationService locationService;
    private static SetTopBox setTopBox = null;
    public static boolean serviceFlag=false;
    public static JSONObject semantic = null,slots =null,answer=null,datetime=null,location=null,data=null;public static String operation = null,service=null;
    public static JSONArray result=null;
    public static String receiver=null, name = null,price=null,code=null,song = null,keywords=null,content=null,
            url=null,text=null,time=null,date=null,city=null,sourceName=null,target=null,source=null;
    public static String[] weatherDate=null,weather=null,tempRange=null,airQuality=null,wind=null,humidity=null,windLevel=null;

    private TextUnderstander mTextUnderstander;
    private ListView mListView;
    private ArrayList<SiriListItem> list;
    ChatMsgViewAdapter mAdapter;
    private MediaPlayer player;
    public static  String SaveResult="";
    public static  String SRResult="";
    private static String SAResult="";
    private static String TAG = MainActivity.class.getSimpleName();

    private Toast info;

    private TextView textView;

    private SpeechRecognizer mIat;

    private RecognizerDialog mIatDialog;

    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private String mEngineTypeTTS = SpeechConstant.TYPE_CLOUD;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences mSharedPreferencesTTS;
    MainActivity mActivity = new MainActivity();
    GetNameAction getNameAction = new GetNameAction();


    private RecognizerListener recognizerListener = new RecognizerListener() {
        public void onBeginOfSpeech() {

        }
        public void onError(SpeechError error) {
            speak("没有听到您说话", false,ctx);
            showTip(error.getPlainDescription(true));
        }
        public void onEndOfSpeech() {
            showTip("结束说话");

        }
        public void onResult(RecognizerResult results, boolean isLast) {

            printResult(results,isLast);
            if (isLast) {

            }
        }
        public void onVolumeChanged(int volume,byte[] data) {
            showTip("请对着麦克风讲话，音量为："+volume);

        }
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };

    private InitListener mInitListener = new InitListener() {


        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码"+code);
            }
        }
    };


    private InitListener textUnderstanderListener = new InitListener() {
        public void onInit(int code) {
            Log.d(TAG, "textUnderstanderListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {

                Log.d("dd","初始化失败,错误码："+code);
            }
        }
    };

    public void getJsonData() {

        try {
            JSONObject SAResultJson = new JSONObject(SAResult);

            operation=SAResultJson.optString("operation");
            service=SAResultJson.optString("service");
            semantic=SAResultJson.optJSONObject("semantic");
            answer=SAResultJson.optJSONObject("answer");
            data=SAResultJson.optJSONObject("data");

            if(data==null){
            }else result=data.optJSONArray("result");

            if(result==null){
            }else{

                airQuality=new String[10];
                weatherDate=new String[10];
                wind=new String[10];
                humidity=new String[10];
                windLevel=new String[10];
                weather=new String[10];
                tempRange=new String[10];
                for(int i=1;i<7;i++){
                    airQuality[i-1]=result.getJSONObject(i).optString("airQuality");
                    weatherDate[i-1]=result.getJSONObject(i).optString("date");
                    wind[i-1]=result.getJSONObject(i).optString("wind");
                    humidity[i-1]=result.getJSONObject(i).optString("humidity");
                    windLevel[i-1]=result.getJSONObject(i).optString("windLevel");
                    weather[i-1]=result.getJSONObject(i).optString("weather");
                    tempRange[i-1]=result.getJSONObject(i).optString("tempRange");
                    sourceName=result.getJSONObject(i).optString("sourceName");
                }

            }

            if(answer==null){
            }else text=answer.optString("text");

            if(semantic==null){
            }else slots=semantic.optJSONObject("slots");

            if(slots==null){
            }else{
                receiver=slots.optString("receiver");
                location=slots.optJSONObject("location");
                name = slots.optString("name");
                price= slots.optString("price");
                code = slots.optString("code");
                song = slots.optString("song");
                keywords=slots.optString("keywords");
                content=slots.optString("content");
                url=slots.optString("url");
                target=slots.optString("target");
                source=slots.optString("source");
            }

            if(location==null){
            }else{
                city=location.optString("city");
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            speak("解析json数据有问题",false,ctx);
            e.printStackTrace();
        }
        SonarReaction();
    }

    public void SonarReaction(){
        SRResult=null;
        SAResult=null;
        //speak("service:"+service+" operation:"+operation,false);
        //speak("serviceFlag",serviceFlag);
        if(serviceFlag==false) {

            switch (service) {


                case "telephone": {

                    switch (operation) {

                        case "CALL": {
                            CallAction callAction = new CallAction(name, code, ctx, this);
                            callAction.start();
                            name = null;
                        }

                        case "VIEW": {

                            break;
                        }

                        default:
                            break;

                    }

                    break;
                }
                case "tvControl":{
                    switch(operation){
                        case "PAUSE":{
                            byte keycode = 127;
                            setTopBox.onKeyDown(keycode);
                            break;
                        }
                        case "PLAY":{
                            byte keycode = 126;
                            setTopBox.onKeyDown(keycode);
                            break;
                        }
                        default:break;
                    }
                }

                case "message": {

                    switch (operation) {

                        case "SEND": {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Name",name);
                            Intent intent = new Intent(ctx,MassageEditActivity.class);
                            intent.putExtras(bundle);
                            ctx.startActivity(intent);
                            name = null;
                            break;
                        }

                        case "VIEW": {


                            break;
                        }


                        case "SENDCONTACTS": {

                            break;
                        }
                        default:
                            break;
                    }

                    break;
                }

                case "app": {

                    switch (operation) {

                        case "LAUNCH": {

                            break;
                        }

                        case "QUERY": {

                            break;
                        }

                        default:
                            break;

                    }
                    break;
                }

                case "website": {

                    switch (operation) {

                        case "OPEN": {


                            break;
                        }

                        default:
                            break;
                    }

                    break;
                }

                case "websearch": {

                    switch (operation) {

                        case "QUERY": {


                            break;
                        }

                        default:
                            break;

                    }


                    break;
                }

                case "faq": {

                    switch (operation) {

                        case "ANSWER": {


                            break;
                        }

                        default:
                            break;
                    }

                    break;
                }

                case "chat": {


                     switch (operation) {

                        case "ANSWER": {
                            Log.v("wifi", SaveResult);
                            String[] Function = getNameAction.getParagraph(SaveResult);
                            WifiManager wifiManager = (WifiManager) ctx
                                    .getSystemService(Context.WIFI_SERVICE);
                            for (int i = 0; i < Function.length; i++) {
                                if ((i + 1) < Function.length) {
                                    String GetNews = Function[i] + Function[i + 1];
                                    switch (GetNews) {
                                        case "打开":
                                            for (int n = 0; n < Function.length; n++) {
                                                if ((n + 3) < Function.length) {
                                                    String keyword = Function[n] + Function[n + 1] + Function[n + 2] + Function[n + 3];
                                                    if (keyword.equals("wifi")) {
                                                        Log.v("wifiTest", "wifiIn");
                                                        wifiManager.setWifiEnabled(true);
                                                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                                        ctx.startActivity(intent);
                                                    }
                                                }
                                            }
                                            break;
                                        case "关闭":
                                            for (int n = 0; n < Function.length; n++) {
                                                if ((n + 3) < Function.length) {
                                                    String keyword = Function[n] + Function[n + 1] + Function[n + 2] + Function[n + 3];
                                                    if (keyword.equals("wifi")) {
                                                        Log.v("keyword", "in");
                                                        wifiManager.setWifiEnabled(false);
                                                    }
                                                }
                                            }
                                            break;

                                    }
                                }
                            }

                            break;
                        }

                        default:
                            break;
                    }

                }

                case "openQA": {

                    switch (operation) {

                        case "ANSWER": {

                            OpenQA openQA = new OpenQA(text,ctx,this);
                            openQA.start();

                            break;
                        }

                        default:
                            break;
                    }

                    break;
                }

                case "baike": {

                    switch (operation) {

                        case "ANSWER": {


                            break;
                        }

                        default:
                            break;
                    }

                    break;
                }

                case "schedule": {

    /*                switch (operation) {

                        case "CREATE": {

                            Intent intent = new Intent(ctx,AlarmClockSettingActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("position", -1);

                            intent.putExtras(bundle);
                            ctx.startActivity(intent);
                            break;
                        }

                        case "VIEW": {

                            Intent intent = new Intent(ctx,AlarmClockMainActivity.class);
                            ctx.startActivity(intent);
                            break;
                        }


                        default:
                            break;
                    }*/

                    break;
                }

                case "weather": {

                  switch (operation) {

                        case "QUERY": {
                            Intent intent = new Intent(ctx,WeatherActivity.class);
                            ctx.startActivity(intent);
                            locationService.start();
                            locationService.stop();


                            break;
                        }

                        default:
                            break;

                    }

                    break;
                }

                case "translation": {

                    switch (operation) {

                        case "TRANSLATION": {


                            break;
                        }

                        default:
                            break;

                    }

                    break;
                }

                default: {
                    String VoiceTag = "null";
                    String paragraph = null;
                    try {
                        JSONObject JSONNews = new JSONObject(SaveResult);
                        String FunctionText = JSONNews.getString("text");
                        String[] Function = getNameAction.getParagraph(FunctionText);
                        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
                        Activity activityContext = (Activity) ctx;
                        for (int i = 0; i < Function.length; i++) {
                            Log.v("i", String.valueOf(i));
                            if ((i + 1) < Function.length) {
                                String GetNews = Function[i] + Function[i + 1];
                                switch (GetNews) {
                                    case "新闻":
                                        for (int n = 0; n < Function.length; n++) {
                                            if ((n + 1) < Function.length) {
                                                String keyword = Function[n] + Function[n + 1];
                                                VoiceTag = "N";
                                                switch (keyword) {
                                                    case "社会":
                                                        VoiceTag = "N1";
                                                        Jump("http://apis.baidu.com/txapi/social/social");
                                                        break;
                                                    case "国际":
                                                        VoiceTag = "N2";
                                                        Jump("http://apis.baidu.com/txapi/world/world");
                                                        break;
                                                    case "体育":
                                                        VoiceTag = "N3";
                                                        Jump("http://apis.baidu.com/txapi/tiyu/tiyu");
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        }
                                        break;

                                    case "亮度":
                                        for (int n = 0; n < Function.length; n++) {
                                            if ((n + 1) < Function.length) {
                                                BrightnessAction brightnessAction = new BrightnessAction(ctx);
                                                String keyword = Function[n] + Function[n + 1];
                                                int BrightnessNow = brightnessAction.screenBrightness_check();
                                                switch (keyword) {
                                                    case "增大":
                                                        VoiceTag = "B1";
                                                        int increase = BrightnessNow + 52;
                                                        brightnessAction.setScreenBritness(increase);
                                                        break;
                                                    case "降低":
                                                        VoiceTag = "B2";
                                                        int lower = BrightnessNow - 52;
                                                        brightnessAction.setScreenBritness(lower);
                                                        break;
                                                }
                                            }
                                        }
                                        break;

                                    case "音量":
                                        for (int n = 0; n < Function.length; n++) {
                                            if ((n + 1) < Function.length) {
                                                String keyword = Function[n] + Function[n + 1];
                                                switch (keyword) {
                                                    case "媒体":
                                                        for (int m = 0; m < Function.length; m++) {

                                                            if ((m + 1) < Function.length) {
                                                                String keywordType = Function[m] + Function[m + 1];
                                                                if (keywordType.equals("增大")) {
                                                                    VoiceTag = "S1";
                                                                    ChangeVolume(
                                                                            AudioManager.STREAM_MUSIC,
                                                                            AudioManager.ADJUST_RAISE,
                                                                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                                } else if (keywordType.equals("降低")) {
                                                                    VoiceTag = "S2";
                                                                    ChangeVolume(
                                                                            AudioManager.STREAM_MUSIC,
                                                                            AudioManager.ADJUST_LOWER,
                                                                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case "提示":
                                                        for (int m = 0; m < Function.length; m++) {
                                                            if ((m + 1) < Function.length) {
                                                                String keywordType = Function[m] + Function[m + 1];
                                                                if (keywordType.equals("增大")) {
                                                                    VoiceTag = "S3";
                                                                    ChangeVolume(
                                                                            AudioManager.STREAM_ALARM,
                                                                            AudioManager.ADJUST_RAISE,
                                                                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                                } else if (keywordType.equals("降低")) {
                                                                    VoiceTag = "S4";
                                                                    ChangeVolume(
                                                                            AudioManager.STREAM_ALARM,
                                                                            AudioManager.ADJUST_LOWER,
                                                                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                                }
                                                            }
                                                        }

                                                        break;
                                                    case "铃声":
                                                        for (int m = 0; m < Function.length; m++) {
                                                            if ((m + 1) < Function.length) {
                                                                String keywordType = Function[m] + Function[m + 1];
                                                                if (keywordType.equals("增大")) {
                                                                    VoiceTag = "S5";
                                                                    ChangeVolume(
                                                                            AudioManager.STREAM_RING,
                                                                            AudioManager.ADJUST_RAISE,
                                                                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                                } else if (keywordType.equals("降低")) {
                                                                    VoiceTag = "S6";
                                                                    ChangeVolume(
                                                                            AudioManager.STREAM_RING,
                                                                            AudioManager.ADJUST_LOWER,
                                                                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case "通话":
                                                        for (int m = 0; m < Function.length; m++) {
                                                            if ((m + 1) < Function.length) {
                                                                String keywordType = Function[m] + Function[m + 1];
                                                                if (keywordType.equals("增大")) {
                                                                    VoiceTag = "S7";
                                                                    ChangeVolume(
                                                                            AudioManager.STREAM_VOICE_CALL,
                                                                            AudioManager.ADJUST_RAISE,
                                                                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                                } else if (keywordType.equals("降低")) {
                                                                    VoiceTag = "S8";
                                                                    ChangeVolume(
                                                                            AudioManager.STREAM_VOICE_CALL,
                                                                            AudioManager.ADJUST_LOWER,
                                                                            AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                                }
                                                            }
                                                        }
                                                        break;
                                                }
                                            }
                                        }
                                        break;
                                    case "发给":
                                        if (activityContext instanceof MassageEditActivity) {
                                            VoiceTag = "M1";

                                            for (int n = i + 2; n < Function.length; n++) {
                                                if (paragraph == null) {
                                                    paragraph = Function[n];
                                                }else if(Function[n].equals("。")){
                                                    continue;
                                                }
                                                else {
                                                    paragraph = paragraph + Function[n];
                                                }
                                                Log.v("name",paragraph);
                                                Log.v("n",String.valueOf(n));
                                            }
                                            if(paragraph != null) {
                                                String MassageName = getNameAction.GetChineseName(paragraph.trim());
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("Name",MassageName);
                                                Intent intent = new Intent(ctx,MassageEditActivity.class);
                                                intent.putExtras(bundle);
                                                ctx.startActivity(intent);
                                                ((Activity) ctx).finish();

                                            }
                                        }
                                        break;
                                    case "内容":
                                        if (activityContext instanceof MassageEditActivity) {
                                            VoiceTag = "M2";

                                            for (int n = i + 3; n < Function.length; n++) {
                                                if (paragraph == null) {
                                                    paragraph = Function[n];
                                                } else {
                                                    paragraph = paragraph + Function[n];
                                                }
                                            }
                                            if(paragraph != null) {
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("Content",paragraph);
                                                Intent intent = new Intent(ctx,MassageEditActivity.class);
                                                intent.putExtras(bundle);
                                                ctx.startActivity(intent);
                                                ((Activity) ctx).finish();

                                            }

                                        }
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                Log.v("ininini", VoiceTag);
                                Log.v("i + 1", String.valueOf(i + 1));
                                Log.v("Function.length", String.valueOf(Function.length));
                                if (VoiceTag.equals("null") && (i + 1) == Function.length) {
                                    Log.v("ininini", "in");
                                    speak("不知道您要干嘛，不过我想过一段时间我就会懂了。", false, ctx);
                                    VoiceTag = "";

                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }







//        super.onStop();
//    }
//
//    @Override
//    protected void onStart() {
//        // TODO Auto-generated method stub
//        super.onStart();
//        // -----------location config ------------
//        locationService = ((MyApplication)getApplication()).locationService;

//     *
//     */
//    public BDLocationListener mListener = new BDLocationListener() {
//
//        String locationStr;
//
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//            // TODO Auto-generated method stub
//            WeatherActivity.nowLocation = location.getCity().toString();
//        }
//    };





    public void ChangeVolume(int streamType,int direction,int flages){
        AudioManager mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.adjustStreamVolume(streamType, direction, flages);
    }



    public  void Jump(String Url){
        Bundle bundle = new Bundle();
        bundle.putSerializable("Url",Url);
        Intent intent = new Intent(ctx ,NewsActivity.class);
        intent.putExtras(bundle);
        ctx.startActivity(intent);
    }



    public void initIflytek(){
        Activity activity = (Activity)ctx;



        activity.findViewById(R.id.voice_input).setOnClickListener(this);
        SpeechUtility.createUtility(ctx, SpeechConstant.APPID + "=564f2dfe" +
                ".");

    }

    public void initUI(){
        SRResult="";
        list = new ArrayList<SiriListItem>();
        if(ctx instanceof MainActivity) {
            list.add(new SiriListItem("text", true));
        }
        mAdapter = new ChatMsgViewAdapter(ctx, list);
        Activity activity = (Activity)ctx;
        mListView = (ListView) activity.findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setFastScrollEnabled(true);
        activity.registerForContextMenu(mListView);
    }

    public void speechRecognition(){
        mIat= SpeechRecognizer.createRecognizer(ctx, mInitListener);

        mIatDialog = new RecognizerDialog(ctx, mInitListener);

        mTextUnderstander = TextUnderstander.createTextUnderstander(ctx, textUnderstanderListener);

        mTts = SpeechSynthesizer.createSynthesizer(ctx, mTtsInitListener);
    }

    public void startSpeenchRecognition(){
        player = MediaPlayer.create(ctx, R.raw.begin);
        player.start();

        mIatDialog.setListener(recognizerDialogListener);

        ret = mIat.startListening(recognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            Log.d(TAG, "" + ret);
            showTip("听写失败，错误码：" + ret);
        }

    }

    private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results,isLast);
        }


        public void onError(SpeechError error) {
            speak(error.getPlainDescription(true),true,ctx);
            info.makeText(ctx.getApplicationContext(), "error.getPlainDescription(true)", 1000).show();
        }

    };



    private void startAnalysis(){

        mTextUnderstander.setParameter(SpeechConstant.DOMAIN,  "iat");
        Log.v("qwer","12122121");
        if(mTextUnderstander.isUnderstanding()){
            mTextUnderstander.cancel();
            Log.d("dd","取消");
        }else {
            ret = mTextUnderstander.understandText(SRResult, textListener);

            Log.v("12345",String.valueOf(ret));
            if(ret != 0)
            {
                Log.d("dd","语义理解失败,错误码:"+ ret);
            }
        }
    }

    private TextUnderstanderListener textListener = new TextUnderstanderListener() {

        public void onResult(final UnderstanderResult result) {
            Activity activity = (Activity)ctx;
            activity.runOnUiThread(new Runnable() {

                public void run() {
                    if (null != result) {
                        String text = result.getResultString();
                        SAResult = text;
                        SaveResult = SAResult;
                        Log.d("dd", "SAResult:" + SAResult);

                        if (TextUtils.isEmpty(text)) {
                        }

                        getJsonData();
                    }
                }



					/*private void dialogueManagement(int mainServiceID,int branchServiceID) {//?????????
						// TODO Auto-generated method stub
						if(mainServiceID==1){
							if(branchServiceID==1){//???????锟??????????????锟????,????????小???????????????????????巍???尾?????
								//???????????????????????

							}
							if(branchServiceID==2){//????????锟??????

							}

						}
						if(mainServiceID==2){//??????????????????????锟????????????

						}
					}*/
            });
        }

        public void onError(SpeechError error) {
            Log.d("dd","onError Code??"	+ error.getErrorCode());
        }
    };


    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 0){
            if(resultCode == 0){
                SAResult = data.getStringExtra("SRResult");
            }
        }
    }



    private void printResult(RecognizerResult results,boolean isLast) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            Log.d("dd","json:"+results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        SRResult=resultBuffer.toString();
        Activity activityContext = (Activity)ctx;
        if(isLast==true){
            speak(SRResult, true,ctx);

            startAnalysis();
        }
    }

    int ret = 0;

    @SuppressWarnings("static-access")
    @Override
    public void onClick(View view) {


        startSpeenchRecognition();
    }

    public void setParam(){
        mIat.setParameter(SpeechConstant.PARAMS, null);

        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);

        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {

            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {

            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()
                + "/iflytek/wavaudio.pcm");

        mIat.setParameter(SpeechConstant.ASR_DWA, mSharedPreferences.getString("iat_dwa_preference", "0"));
    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

    }



    private InitListener mTtsInitListener = new InitListener() {

        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码："+code);
            } else {

            }
        }
    };


    private void setParamTTS(){

        mTts.setParameter(SpeechConstant.PARAMS, null);

        if(mEngineTypeTTS.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);

            mTts.setParameter(SpeechConstant.VOICE_NAME,voicer);

        }else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
        }
    }


    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        public void onSpeakBegin() {
        }


        public void onSpeakPaused() {
            showTip("暂停播放");
        }


        public void onSpeakResumed() {
            showTip("继续播放");
        }


        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {

        }


        public void onSpeakProgress(int percent, int beginPos, int endPos) {

        }


        public void onCompleted(SpeechError error) {
            if (error == null) {
            } else if (error != null) {
                showTip(error.getPlainDescription(true));
            }
        }


        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };

    private void textToSpeach(String text){

        setParamTTS();
        int code = mTts.startSpeaking(text, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            showTip("语音合成失败，错误码："+code);
        }
    }


    public void speak(String msg, boolean isSiri,Context context) {
        Activity activityContext = (Activity)context;
       if(!(activityContext instanceof PhoneCallActivity) && !(activityContext instanceof MassageEditActivity)) {
           Log.v("INININININI", "AIAIAIA");
           addToList(msg, isSiri);
       }

        if(isSiri==false){
            textToSpeach(msg);
        }
    }

    public void beginSpeak(String msg, boolean isSiri){
        Log.v("123457","asadfsfs");
        if(isSiri==false){
            textToSpeach(msg);
        }
    }

    private void addToList(String msg, boolean isSiri) {

        Log.v("msg12345678",String.valueOf(isSiri));
        list.add(new SiriListItem(msg, isSiri));
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(list.size() - 1);
    }

    public class SiriListItem {
       public String message;
       public boolean isSiri;

        public SiriListItem(String msg, boolean siri) {
            message = msg;
            isSiri = siri;
        }
    }

    private void showTip(final String str) {
        info.setText(str);
        info.show();
    }



}
