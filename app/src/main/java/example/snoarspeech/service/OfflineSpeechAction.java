package example.snoarspeech.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.sunflower.FlowerCollector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import example.snoarspeech.JsonParser;
import example.snoarspeech.activity.MainActivity;
import example.snoarspeech.R;
import example.snoarspeech.activity.MassageEditActivity;
import example.snoarspeech.activity.OfflineMassageEditActivity;
import example.snoarspeech.adapter.OffLineChatMsgViewAdapt;
import example.snoarspeech.utils.ApkInstaller;
import example.snoarspeech.utils.FucUtil;

/**
 * Created by ASUS on 2016/5/6.
 */
public class OfflineSpeechAction implements AdapterView.OnItemClickListener, View.OnClickListener {
    private Context ctx;
    public OfflineSpeechAction(Context context){
        ctx = context;
        mToast= Toast.makeText(ctx, "", Toast.LENGTH_SHORT);
    }
    private MediaPlayer player;
    private static String TAG = OfflineSpeechAction.class.getSimpleName();
    private SpeechRecognizer mIat;
    private RecognizerDialog mIatDialog;
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    private String JsonResult;
    private ArrayList<SiriListItem> list;
    OffLineChatMsgViewAdapt mAdapter;
    private ListView mListView;

    private Toast mToast;

    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    ApkInstaller mInstaller;
    GetNameAction getNameAction = new GetNameAction();
    BrightnessAction brightnessAction = new BrightnessAction(ctx);
    OpenAppAction openAppAction = new OpenAppAction(ctx);




    public void initIflytek(){
        Activity activity = (Activity)ctx;

        activity.findViewById(R.id.voice_input).setOnClickListener(this);
        SpeechUtility.createUtility(ctx, SpeechConstant.APPID + "=564f2dfe");

        mEngineType = SpeechConstant.TYPE_LOCAL;
        if (!SpeechUtility.getUtility().checkServiceInstalled()) {
            mInstaller.install();
        } else {
            String result = FucUtil.checkLocalResource();
            if (!TextUtils.isEmpty(result)) {
                showTip(result);
            }
        }
    }


    int ret = 0;


    @Override
    public void onClick(View view){

        startSpeenchRecognition();
    }

    public void startSpeenchRecognition() {
        player = MediaPlayer.create(ctx, R.raw.begin);
        player.start();
        FlowerCollector.onEvent(ctx, "iat_recognize");

        setParam();
        ret = mIat.startListening(mRecognizerListener);
        Log.v("????123123", String.valueOf(ret));
        if (ret != ErrorCode.SUCCESS) {
            showTip("听写失败,错误码：" + ret);
        } else {
            showTip("成功");
        }

    }

    public void speechRecognition(){

        mIat= SpeechRecognizer.createRecognizer(ctx, mInitListener);
        Activity activity = (Activity)ctx;
        mInstaller = new ApkInstaller(activity);
    }

    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
            }
        }
    };




    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            printResult(results);

            if (isLast) {
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小为" + volume);
            Log.d(TAG, "音量" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            Log.v(">>>>>",resultJson.toString());
//            JsonResult
            String ws = resultJson.getString("ws");
            JSONArray list = new JSONArray(ws);
            JSONObject JsonWs = new JSONObject(list.getString(0));
            String cw = JsonWs.getString("cw");
            JSONArray ArrayCw = new JSONArray(cw);
            JSONObject JsonCw = new JSONObject(ArrayCw.getString(0));
            String w = JsonCw.getString("w");
            JsonResult = w;
            SonarReaction(JsonResult);
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

    }
    public void SonarReaction(String text) {
        String Function = "";
        String AppName = "";
        Activity activityContext = (Activity) ctx;
        String[] inputString = getNameAction.getParagraph(text);
        for (int i = 0; i < inputString.length; i++) {
            if (inputString[i] != null && inputString[i] != "") {
                Function = inputString[i];
                Log.v("1", String.valueOf(i));
            }
            switch (Function) {
                case "打":
                    String Name = null;
                    String paragraph = null;
                    if(inputString.length >= (i + 3) ){
                        int  NameLength = inputString.length - i - 1;
                        for(int m = 0; m < NameLength;m++ ){
                            if (NameLength >= (i + 2 + m)) {
                                Name =  inputString[i + 1 + m] + inputString[i + 2 + m];
                                if (inputString[i + 1].equals("开")) {
                                    final WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
                                    int InputLength = inputString.length - (i + 2);
                                    for (int o = 0; o < InputLength; o++) {
                                        if(inputString.length >= (i + 3 + o)){
                                            //   if(inputString[i + 2 + o] != null && inputString[i + 2 + o] != ""){
                                            AppName = AppName + inputString[i + 2 + o];
                                            Boolean BoolApp = openAppAction.OpenApplication(AppName);
                                            if (BoolApp == false && AppName.trim().equals("wifi")) {
                                                wifiManager.setWifiEnabled(true);
                                                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                                ctx.startActivity(intent);

                                                break;
                                            }
                                        }

                                    }

                                } else if (Name.equals("电话")) {
                                    for (int n = i + 3; n < inputString.length; n++) {
                                        if (paragraph == null) {
                                            paragraph = inputString[n];
                                        }else if(inputString[n].equals("。")){
                                            continue;
                                        } else {
                                            paragraph = paragraph + inputString[n];
                                        }
                                    }
                                    if(paragraph != null) {
                                        String PhoneName = getNameAction.GetChineseName(paragraph.trim());
                                        Log.v("PhoneName",PhoneName);
                                        CallAction callAction = new CallAction(PhoneName,"",ctx);
                                        callAction.start();
                                    }
                                }

                            }
                        }
                    }
                    break;
                case "关":

                    if((inputString.length - i ) > 0 ){
                        if(inputString[i + 1].equals("闭")){
                            final WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
                            int InputLength = inputString.length - (i + 2);
                            for (int o = 0; o < InputLength; o++) {
                                AppName = AppName + inputString[i + 2 + o];
                                Log.v("inputString", inputString[o]);
                                Log.v("AppName", AppName);
                                if(AppName.equals("wifi")){
                                    wifiManager.setWifiEnabled(false);
                                    break;
                                }

                            }
                        }
                    }
                    break;
                case "发":
                    if((inputString.length - i ) > 0 ){
                        String MassageName = null;
                        int InputLength = inputString.length - (i + 1);
                        Log.v("2", "in");
                        for (int o = 0; o < InputLength; o++) {
                            if(InputLength >= (i + 2 + o) ){
                                String Massage = inputString[i + 1 + o]
                                        + inputString[i + 2 + o];
                                Log.v("3", Massage);
                                Log.v("3", "in");
                                if(Massage.equals("短信")){
                                    Log.v("duanxin", "in");
                                    for (int n = i + 3; n < inputString.length; n++) {
                                        if (MassageName == null) {
                                            MassageName = inputString[n];
                                        }else if(inputString[n].equals("。")){
                                            continue;
                                        }
                                        else {
                                            MassageName = MassageName + inputString[n];
                                        }
                                    }

                                    if(MassageName != null){
                                        String PhoneName = getNameAction.GetChineseName(MassageName.trim());
                                        Bundle bundle=new Bundle();
                                        bundle.putSerializable("Name", PhoneName);
//                                        Log.v("Name", PhoneName);
                                        Intent intent = new Intent(ctx,OfflineMassageEditActivity.class);
                                        intent.putExtras(bundle);
                                        ctx.startActivity(intent);
                                    }else{
                                        Log.v("no", "no");
                                        Intent intent = new Intent(ctx, MassageEditActivity.class);
                                        ctx.startActivity(intent);
                                    }

                                    break;
                                }
                            }
                        }
                    }
                    break;
                case "增":
                    if (inputString[i + 1] != null && inputString[i + 1] != "") {
                        if (inputString[i + 1].equals("大")
                                || inputString[i + 1].equals("加")) {

                            int InputLength = inputString.length - i;
                            if (InputLength >= 2) {
                                AudioManager mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
                                for (int o = 0; o < InputLength; o++) {
                                    if (InputLength > (i + 3 + o)) {
                                        String CommandName = inputString[i + 2 + o]
                                                + inputString[i + 3 + o];
                                        switch (CommandName) {
                                            case "亮度":
                                                int BrightnessNow = brightnessAction.screenBrightness_check();
                                                int Brightness = BrightnessNow + 52;
                                                Log.v("Brightness",String.valueOf(Brightness));
                                                brightnessAction.setScreenBritness(Brightness);
                                                break;
                                            case "媒体":
                                                if (InputLength > (i + 5 + o)) {
                                                    String SongName = inputString[i
                                                            + 4 + o]
                                                            + inputString[i + 5 + o];
                                                    if (SongName.equals("音量")) {

                                                        // 音乐回放即媒体音量
                                                        mAudioManager
                                                                .adjustStreamVolume(
                                                                        AudioManager.STREAM_MUSIC,
                                                                        AudioManager.ADJUST_RAISE,
                                                                        AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                    }
                                                }
                                                break;
                                            case "提示":
                                                if (InputLength > (i + 5 + o)) {
                                                    String SongName = inputString[i
                                                            + 4 + o]
                                                            + inputString[i + 5 + o];
                                                    if (SongName.equals("音量")) {
                                                        // 提示
                                                        mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                    }
                                                }
                                                break;
                                            case "铃声":
                                                if (InputLength > (i + 5 + o)) {
                                                    String SongName = inputString[i
                                                            + 4 + o]
                                                            + inputString[i + 5 + o];
                                                    if (SongName.equals("音量")) {
                                                        // 铃声
                                                        mAudioManager
                                                                .adjustStreamVolume(
                                                                        AudioManager.STREAM_RING,
                                                                        AudioManager.ADJUST_RAISE,
                                                                        AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                    }
                                                }
                                                break;
                                            case "通话":
                                                if (InputLength > (i + 5 + o)) {
                                                    String SongName = inputString[i
                                                            + 4 + o]
                                                            + inputString[i + 5 + o];
                                                    if (SongName.equals("音量")) {
                                                        // 通话
                                                        mAudioManager
                                                                .adjustStreamVolume(
                                                                        AudioManager.STREAM_VOICE_CALL,
                                                                        AudioManager.ADJUST_RAISE,
                                                                        AudioManager.FX_FOCUS_NAVIGATION_UP);
                                                    }
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case "降":
                    if (inputString[i + 1] != null && inputString[i + 1] != "") {
                        if (inputString[i + 1].equals("低")) {
                            int InputLength = inputString.length - i;
                            if (InputLength >= 2) {
                                for (int o = 0; o < InputLength; o++) {
                                    if (InputLength > (i + 3 + o)) {
                                        String CommandName = inputString[i + 2 + o]
                                                + inputString[i + 3 + o];
                                        AudioManager mAudioManager = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
                                        switch (CommandName) {
                                            case "亮度":
                                                int BrightnessNow = brightnessAction.screenBrightness_check();
                                                int Brightness = BrightnessNow - 52;
                                                brightnessAction.setScreenBritness(Brightness);
                                                break;
                                            case "媒体":
                                                if (InputLength > (i + 5 + o)) {
                                                    String SongName = inputString[i
                                                            + 4 + o]
                                                            + inputString[i + 5 + o];
                                                    if (SongName.equals("音量")) {

                                                        // 音乐回放即媒体音量
                                                        mAudioManager
                                                                .adjustStreamVolume(
                                                                        AudioManager.STREAM_MUSIC,
                                                                        AudioManager.ADJUST_RAISE,
                                                                        AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                                                    }
                                                }
                                                break;
                                            case "提示":
                                                if (InputLength > (i + 5 + o)) {
                                                    String SongName = inputString[i
                                                            + 4 + o]
                                                            + inputString[i + 5 + o];
                                                    if (SongName.equals("音量")) {
                                                        // 提示
                                                        mAudioManager
                                                                .adjustStreamVolume(
                                                                        AudioManager.STREAM_ALARM,
                                                                        AudioManager.ADJUST_RAISE,
                                                                        AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                                                    }
                                                }
                                                break;
                                            case "铃声":
                                                if (InputLength > (i + 5 + o)) {
                                                    String SongName = inputString[i
                                                            + 4 + o]
                                                            + inputString[i + 5 + o];
                                                    if (SongName.equals("音量")) {
                                                        // 铃声
                                                        mAudioManager
                                                                .adjustStreamVolume(
                                                                        AudioManager.STREAM_RING,
                                                                        AudioManager.ADJUST_RAISE,
                                                                        AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                                                    }
                                                }
                                                break;
                                            case "通话":
                                                if (InputLength > (i + 5 + o)) {
                                                    String SongName = inputString[i
                                                            + 4 + o]
                                                            + inputString[i + 5 + o];
                                                    if (SongName.equals("音量")) {
                                                        // 通话
                                                        mAudioManager
                                                                .adjustStreamVolume(
                                                                        AudioManager.STREAM_VOICE_CALL,
                                                                        AudioManager.ADJUST_RAISE,
                                                                        AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                                                    }
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }



    public void initUI(){

        list = new ArrayList<SiriListItem>();
        if(ctx instanceof MainActivity) {
            list.add(new SiriListItem("text", true));
        }
        mAdapter = new OffLineChatMsgViewAdapt(ctx, list);
        Activity activity = (Activity)ctx;
        mListView = (ListView) activity.findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setFastScrollEnabled(true);
        activity.registerForContextMenu(mListView);
    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

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
        mToast.setText(str);
        mToast.show();
    }


    public void setParam() {
        mIat.setParameter(SpeechConstant.PARAMS, null);


        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);

        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");


        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");



        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");


        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");


        mIat.setParameter(SpeechConstant.ASR_PTT,"1");


        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

}

