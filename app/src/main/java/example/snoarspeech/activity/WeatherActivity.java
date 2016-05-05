package example.snoarspeech.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

import example.snoarspeech.MyApplication;
import example.snoarspeech.R;
import example.snoarspeech.service.LocationService;
import example.snoarspeech.utils.WeatherUtils;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by owen_ on 2016-04-18.
 */
public class WeatherActivity extends Activity{

    Button weatherReturnButton,weatherUpdateButton,readButton,chooseButton;
    public TextView testTextView;
    private LocationService locationService;
    String weatherJsonStr;

    static String nowLocation = null,chosePlace = null;;
    static int locationServiceStatus = 0;

    Map<String,String> nowWeatherInfo = new HashMap<String,String>();
    Map<String,Map<String,String>> weatherBroadcastInfo = new HashMap<>();

    WeatherUtils weatherUtils;


    public TextView weatherStatus;
    public TextView temperature;
    public TextView date;
    public TextView PM25;
    public TextView location;
    public ImageView todayActWeatherIcon;

    public ImageView todayWeatherIcon;
    public TextView todayWeather;
    public TextView todayTemperature;

    public ImageView tomorrowWeatherIcon;
    public TextView tomorrowWeather;
    public TextView tomorrowTemperature;

    public ImageView dayAfterTomorrowWeatherIcon;
    public TextView dayAfterTomorrowWeather;
    public TextView dayAfterTomorrowTemperature;

    public TextView liftSuggestionStatus;
    public TextView liftSuggestionText;
    public TextView CarWashingSuggestionStatus;
    public TextView CarWashingSuggestionText;
    public TextView clothesSuggestionStatus;
    public TextView clothesSuggestionText;
    public TextView fluSuggestionStatus;
    public TextView fluSuggestionText;
    public TextView sportsSuggestionStatus;
    public TextView sportsSuggestionText;
    public TextView travSuggestionStatus;
    public TextView travSuggestionText;
    public TextView ultravioletRaysSuggestionStatus;
    public TextView ultravioletRaysSuggestionText;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_main);
        weatherUtils = new WeatherUtils();
        addListener();
//        apiTest(nowLocation);

        testTextView = (TextView)findViewById(R.id.testTextView2);



    }



    void addListener()
    {
        weatherReturnButton = (Button)findViewById(R.id.weatherReturnButton);
        weatherReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent().setClass(WeatherActivity.this, MainActivity.class));
                WeatherActivity.this.finish();
            }
        });

        weatherUpdateButton = (Button)findViewById(R.id.weatherUpdateButton);
        weatherUpdateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                updateWeather();
                System.out.println("weatherUpdateButton");
            }
        });

        readButton = (Button)findViewById(R.id.weatherReadButton);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testTextView.setText("readButton");
                weatherUtils.weatherBroadcast(weatherBroadcastInfo);
            }
        });

        chooseButton = (Button)findViewById(R.id.chooseCityButton);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WeatherActivity.this,WeatherChooseActivity.class));
                WeatherActivity.this.finish();
            }
        });
    }

    public void updateWeather()
    {
        if(chosePlace == null)
        {
            locationService.start();
            locationService.stop();
        }
        else {
            apiTest(weatherUtils.switchCity(chosePlace));
        }
    }


    public void apiTest(String city) {

        if(city != null)
        {
            Parameters para = new Parameters();

            para.put("city", city);
            ApiStoreSDK.execute("http://apis.baidu.com/heweather/weather/free",
                    ApiStoreSDK.GET,
                    para,
                    new ApiCallBack() {
                        @Override
                        public void onSuccess(int status, String responseString) {
                            Log.i("sdkdemo", "onSuccess");

                            if(weatherUtils.getNowWeatherInJson(responseString).isEmpty())
                                Toast.makeText(WeatherActivity.this, "无法查询天气", Toast.LENGTH_LONG).show();
                            else
                                weatherBroadcastInfo = setWeatherUI(weatherUtils.getNowWeatherInJson(responseString));
//                            System.out.println(responseString);
                        }

                        @Override
                        public void onComplete() {
                            Log.i("sdkdemo", "onComplete");
                        }

                        @Override
                        public void onError(int status, String responseString, Exception e) {
                            Log.i("sdkdemo", "onError, status: " + status);
                            Log.i("sdkdemo", "errMsg: " + (e == null ? "" : e.getMessage()));
                            weatherJsonStr = getStackTrace(e);
                        }
                    });
            nowLocation = null;
        }


    }

    String getStackTrace(Throwable e) {
        if (e == null) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        str.append(e.getMessage()).append("\n");
        for (int i = 0; i < e.getStackTrace().length; i++) {
            str.append(e.getStackTrace()[i]).append("\n");
        }
        return str.toString();
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener);
        locationService.stop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // -----------location config ------------
        locationService = ((MyApplication) getApplication()).locationService;

        locationService.registerListener(mListener);

        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }

    }



    public BDLocationListener mListener = new BDLocationListener() {

        String locationStr;

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub

            apiTest(weatherUtils.switchCity(location.getCity().toString()));


        }
    };

    public Map<String,Map<String,String>> setWeatherUI( Map<String,Map<String,String>> WeatherInfo) {



        Map<String,String> todayActualWeatherInfo = new HashMap<>();
        Map<String,String> todayForecastWeather = new HashMap<>();
        Map<String,String> tomorrowForecastWeather = new HashMap<>();
        Map<String,String> dayAfterTomorrowForecastWeather = new HashMap<>();
        Map<String,String> weatherSuggestion = new HashMap<>();
        Map<String,Map<String,String>> weatherBroadcast = new HashMap<>();

        todayActualWeatherInfo = WeatherInfo.get("todayActualWeatherInfo");
        todayForecastWeather = WeatherInfo.get("todayForecastWeather");
        tomorrowForecastWeather = WeatherInfo.get("tomorrowForecastWeather");
        dayAfterTomorrowForecastWeather = WeatherInfo.get("dayAfterTomorrowForecastWeather");
        weatherSuggestion = WeatherInfo.get("weatherSuggestion");

        weatherBroadcast.put("actualWeatherBroadcast",WeatherInfo.get("actualWeatherBroadcast"));
        weatherBroadcast.put("forecastWeatherBroadcast",WeatherInfo.get("forecastWeatherBroadcast"));


        System.out.println(todayActualWeatherInfo);

        weatherStatus = (TextView)findViewById(R.id.weatherStatus);
        temperature = (TextView)findViewById(R.id.temperature);
        date = (TextView)findViewById(R.id.date);
        PM25 = (TextView)findViewById(R.id.PM25);
        location = (TextView)findViewById(R.id.location);
        todayActWeatherIcon = (ImageView)findViewById(R.id.todayActWeatherIcon);

        todayWeatherIcon = (ImageView)findViewById(R.id.todayWeatherIcon);
        todayWeather = (TextView)findViewById(R.id.todayWeather);
        todayTemperature = (TextView)findViewById(R.id.toadyTemperature);

        tomorrowWeatherIcon = (ImageView)findViewById(R.id.tomorrowWeatherIcon);
        tomorrowWeather = (TextView)findViewById(R.id.tomorrowWeather);
        tomorrowTemperature = (TextView)findViewById(R.id.tomorrowTemperature);

        dayAfterTomorrowWeatherIcon = (ImageView)findViewById(R.id.dayAfterTomorrowWeatherIcon);
        dayAfterTomorrowWeather = (TextView)findViewById(R.id.dayAfterTomorrowWeather);
        dayAfterTomorrowTemperature = (TextView)findViewById(R.id.dayAfterTomorrowTemperature);

        liftSuggestionStatus = (TextView)findViewById(R.id.liftSuggestionStatus);
        liftSuggestionText = (TextView)findViewById(R.id.liftSuggestionText);
        CarWashingSuggestionStatus = (TextView)findViewById(R.id.CarWashingSuggestionStatus);
        CarWashingSuggestionText = (TextView)findViewById(R.id.CarWashingSuggestionText);
        clothesSuggestionStatus = (TextView)findViewById(R.id.clothesSuggestionStatus);
        clothesSuggestionText = (TextView)findViewById(R.id.clothesSuggestionText);
        fluSuggestionStatus = (TextView)findViewById(R.id.fluSuggestionStatus);
        fluSuggestionText = (TextView)findViewById(R.id.fluSuggestionText);
        sportsSuggestionStatus = (TextView)findViewById(R.id.sportsSuggestionStatus);
        sportsSuggestionText = (TextView)findViewById(R.id.sportsSuggestionText);
        travSuggestionStatus = (TextView)findViewById(R.id.travSuggestionStatus);
        travSuggestionText = (TextView)findViewById(R.id.travSuggestionText);
        ultravioletRaysSuggestionStatus = (TextView)findViewById(R.id.ultravioletRaysSuggestionStatus);
        ultravioletRaysSuggestionText = (TextView)findViewById(R.id.ultravioletRaysSuggestionText);

        TextView updateTime = (TextView)findViewById(R.id.weatherUpdateTime);

        weatherStatus.setText(todayActualWeatherInfo.get("txt"));
        temperature.setText(todayActualWeatherInfo.get("tmp") + "℃");
        date.setText("今日" + todayActualWeatherInfo.get("todayDate"));
        PM25.setText("PM2.5 : " + todayActualWeatherInfo.get("pm25") + " " + todayActualWeatherInfo.get("qlty"));
        location.setText(todayActualWeatherInfo.get("city") + "市");
        todayActWeatherIcon.setImageResource(this.getResources().getIdentifier("weather" + todayActualWeatherInfo.get("code"), "drawable", "example.snoarspeech"));

        todayWeatherIcon.setImageResource(this.getResources().getIdentifier("weather" + todayForecastWeather.get("code"), "drawable", "example.snoarspeech"));
        todayWeather.setText(todayForecastWeather.get("txt"));
        todayTemperature.setText(todayForecastWeather.get("minTmp") + "℃/" + todayForecastWeather.get("maxTmp") + "℃");

        tomorrowWeatherIcon.setImageResource(this.getResources().getIdentifier("weather" + tomorrowForecastWeather.get("code"), "drawable", "example.snoarspeech"));
        tomorrowWeather.setText(tomorrowForecastWeather.get("txt"));
        tomorrowTemperature.setText(tomorrowForecastWeather.get("minTmp") + "℃/" + todayForecastWeather.get("maxTmp") + "℃");

        dayAfterTomorrowWeatherIcon.setImageResource(this.getResources().getIdentifier("weather" + dayAfterTomorrowForecastWeather.get("code"), "drawable", "example.snoarspeech"));
        dayAfterTomorrowWeather.setText(dayAfterTomorrowForecastWeather.get("txt"));
        dayAfterTomorrowTemperature.setText(dayAfterTomorrowForecastWeather.get("minTmp") +  "℃/"+todayForecastWeather.get("maxTmp") + "℃");

        System.out.println(this.getResources().getIdentifier("weather" + dayAfterTomorrowForecastWeather.get("code"), "drawable", "example.snoarspeech"));

        liftSuggestionStatus.setText(weatherSuggestion.get("comfBrf"));
        liftSuggestionText.setText(weatherSuggestion.get("comfTxt"));
        CarWashingSuggestionStatus.setText(weatherSuggestion.get("cwBrf"));
        CarWashingSuggestionText.setText(weatherSuggestion.get("cwTxt"));
        clothesSuggestionStatus.setText(weatherSuggestion.get("drsgBrf"));
        clothesSuggestionText.setText(weatherSuggestion.get("drsgTxt"));
        fluSuggestionStatus.setText(weatherSuggestion.get("fluBrf"));
        fluSuggestionText.setText(weatherSuggestion.get("fluTxt"));
        sportsSuggestionStatus.setText(weatherSuggestion.get("sportBrf"));
        sportsSuggestionText.setText(weatherSuggestion.get("sportTxt"));
        travSuggestionStatus.setText(weatherSuggestion.get("travBrf"));
        travSuggestionText.setText(weatherSuggestion.get("travTxt"));
        ultravioletRaysSuggestionStatus.setText(weatherSuggestion.get("uvBrf"));
        ultravioletRaysSuggestionText.setText(weatherSuggestion.get("uvTxt"));

        updateTime.setText(todayActualWeatherInfo.get("updateTime"));


        testTextView = (TextView)findViewById(R.id.testTextView2);
        testTextView.setText("");


        System.out.println(WeatherInfo.get("actualWeatherBroadcast"));
        System.out.println(WeatherInfo.get("forecastWeatherBroadcast"));
        return weatherBroadcast;
    }






}

