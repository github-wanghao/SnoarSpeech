package example.snoarspeech.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by owen_ on 2016-04-11.
 */
public class WeatherUtils extends Activity{


    String jsonStr;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);



    }






    public String switchCity(String cityName)
    {
        String cityEngName;

        switch (cityName)
        {
            case "北京市":cityEngName = "Beijing";break;
            case "上海市":cityEngName = "Shanghai";break;
            case "广州市":cityEngName = "Guangzhou";break;
            case "深圳市":cityEngName = "Shenzhen";break;
            case "珠海市":cityEngName = "Zhuhai";break;
            case "东莞市":cityEngName = "Dongguan";break;
            case "天津市":cityEngName = "Tianjin";break;
            case "重庆市":cityEngName = "Chongqing";break;
            case "北京":cityEngName = "Beijing";break;
            case "上海":cityEngName = "Shanghai";break;
            case "广州":cityEngName = "Guangzhou";break;
            case "深圳":cityEngName = "Shenzhen";break;
            case "珠海":cityEngName = "Zhuhai";break;
            case "东莞":cityEngName = "Dongguan";break;
            case "天津":cityEngName = "Tianjin";break;
            case "重庆":cityEngName = "Chongqing";break;
            case "香港特别行政区":cityEngName = "Xianggang";break;
            default:cityEngName = "ERROR";break;
        }
        return cityEngName;
    }

    public Map<String,Map<String,String>> getNowWeatherInJson(String jsonString)
//    public Map<String,String> getNowWeatherInJson(String jsonString)
    {
        Map<String,Map<String,String>> WeatherInfo = new HashMap<>();
        Map<String,String> todayActualWeatherInfo = new HashMap<>();
        Map<String,String> todayForecastWeather = new HashMap<>();
        Map<String,String> tomorrowForecastWeather = new HashMap<>();
        Map<String,String> dayAfterTomorrowForecastWeather = new HashMap<>();
        Map<String,String> weatherSuggestion = new HashMap<>();
        Map<String,String> actualWeatherBroadcast = new HashMap<>();
        Map<String,String> forecastWeatherBroadcast = new HashMap<>();

        Date todayDate;

        try {
            JSONObject jsonStr = new JSONObject(jsonString);
            JSONArray service = jsonStr.getJSONArray("HeWeather data service 3.0");
            //  JSONArray dailyForecast = service.getJSONArray(0);

            JSONObject test = new JSONObject(service.getString(0));

            if(test.getString("status").equals("anr"))
                return WeatherInfo;

            JSONObject todayWeather = test.getJSONObject("now");

            JSONObject cond = todayWeather.getJSONObject("cond");


            JSONObject wind = todayWeather.getJSONObject("wind");

            Iterator key = cond.keys() ;
            while (key.hasNext()){

                String jsonKey = String.valueOf(key.next());
                String value = (String)cond.get(jsonKey);
                System.out.println(value);

                todayActualWeatherInfo.put(jsonKey,value);
            }

            JSONObject basic = test.getJSONObject("basic");
            JSONObject update = basic.getJSONObject("update");

            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(update.get("loc").toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            todayActualWeatherInfo.put("todayDate", simpleDateFormat.format(date));
            System.out.println(simpleDateFormat.format(date));
            JSONObject aqi = test.getJSONObject("aqi");
            JSONObject city = aqi.getJSONObject("city");

            todayActualWeatherInfo.put("tmp",(String)todayWeather.get("tmp"));
            todayActualWeatherInfo.put("pm25",(String)city.get("pm25"));
            todayActualWeatherInfo.put("qlty",(String)city.get("qlty"));
            todayActualWeatherInfo.put("updateTime",(String)update.get("loc"));
            todayActualWeatherInfo.put("city",(String)basic.get("city"));


            JSONArray dailyForecast = test.getJSONArray("daily_forecast");


            JSONObject todayForecast = new JSONObject(dailyForecast.getString(0));
            JSONObject todayForecastCond = todayForecast.getJSONObject("cond");
            JSONObject todayForecastTmp = todayForecast.getJSONObject("tmp");
            JSONObject todayForecastWind = todayForecast.getJSONObject("wind");

            todayForecastWeather.put("code",(String)todayForecastCond.get("code_d"));
            todayForecastWeather.put("txt",(String)todayForecastCond.get("txt_d"));
            todayForecastWeather.put("maxTmp",(String)todayForecastTmp.get("max"));
            todayForecastWeather.put("minTmp",(String)todayForecastTmp.get("min"));


            JSONObject tomorrowForecast = new JSONObject(dailyForecast.getString(1));
            JSONObject tomorrowForecastCond = tomorrowForecast.getJSONObject("cond");
            JSONObject tomorrowForecastTmp = tomorrowForecast.getJSONObject("tmp");
            JSONObject tomorrowForecastWind = tomorrowForecast.getJSONObject("wind");

            tomorrowForecastWeather.put("code",(String)tomorrowForecastCond.get("code_d"));
            tomorrowForecastWeather.put("txt",(String)tomorrowForecastCond.get("txt_d"));
            tomorrowForecastWeather.put("maxTmp",(String)tomorrowForecastTmp.get("max"));
            tomorrowForecastWeather.put("minTmp",(String)tomorrowForecastTmp.get("min"));


            JSONObject dayAfterTomorrowForecast = new JSONObject(dailyForecast.getString(2));
            JSONObject dayAfterTomorrowForecastCond = dayAfterTomorrowForecast.getJSONObject("cond");
            JSONObject dayAfterTomorrowForecastTmp = dayAfterTomorrowForecast.getJSONObject("tmp");
            JSONObject dayAfterTomorrowForecastWind = dayAfterTomorrowForecast.getJSONObject("wind");

            dayAfterTomorrowForecastWeather.put("code",(String)dayAfterTomorrowForecastCond.get("code_d"));
            dayAfterTomorrowForecastWeather.put("txt",(String)dayAfterTomorrowForecastCond.get("txt_d"));
            dayAfterTomorrowForecastWeather.put("maxTmp",(String)dayAfterTomorrowForecastTmp.get("max"));
            dayAfterTomorrowForecastWeather.put("minTmp",(String)dayAfterTomorrowForecastTmp.get("min"));


            JSONObject suggestionsJson = test.getJSONObject("suggestion");

            JSONObject comf = suggestionsJson.getJSONObject("comf");
            weatherSuggestion.put("comfBrf",(String)comf.get("brf"));
            weatherSuggestion.put("comfTxt",(String)comf.get("txt"));

            JSONObject cw = suggestionsJson.getJSONObject("cw");
            weatherSuggestion.put("cwBrf",(String)cw.get("brf"));
            weatherSuggestion.put("cwTxt",(String)cw.get("txt"));

            JSONObject drsg = suggestionsJson.getJSONObject("drsg");
            weatherSuggestion.put("drsgBrf",(String) drsg.get("brf"));
            weatherSuggestion.put("drsgTxt",(String) drsg.get("txt"));

            JSONObject flu = suggestionsJson.getJSONObject("flu");
            weatherSuggestion.put("fluBrf",(String)flu.get("brf"));
            weatherSuggestion.put("fluTxt",(String)flu.get("txt"));

            JSONObject sport = suggestionsJson.getJSONObject("sport");
            weatherSuggestion.put("sportBrf",(String)sport.get("brf"));
            weatherSuggestion.put("sportTxt",(String)sport.get("txt"));

            JSONObject trav = suggestionsJson.getJSONObject("trav");
            weatherSuggestion.put("travBrf",(String)trav.get("brf"));
            weatherSuggestion.put("travTxt",(String)trav.get("txt"));

            JSONObject uv = suggestionsJson.getJSONObject("uv");
            weatherSuggestion.put("uvBrf",(String)uv.get("brf"));
            weatherSuggestion.put("uvTxt",(String)uv.get("txt"));

            //***************************************** broadcast part ********************************************

            //actualWeatherBroadcast
            actualWeatherBroadcast.put("txt",cond.getString("txt"));
            actualWeatherBroadcast.put("tmp",todayWeather.getString("tmp"));
            actualWeatherBroadcast.put("fl",todayWeather.getString("fl"));
            actualWeatherBroadcast.put("hum",todayWeather.getString("hum"));
            actualWeatherBroadcast.put("vis",todayWeather.getString("vis"));
            actualWeatherBroadcast.put("dir",wind.getString("dir"));
            actualWeatherBroadcast.put("sc",wind.getString("sc"));
            actualWeatherBroadcast.put("pm25",city.getString("pm25"));
            actualWeatherBroadcast.put("qlty", city.getString("qlty"));

            //forecastWeatherBroadcast -- today
            forecastWeatherBroadcast.put("today_txt_d",todayForecastCond.getString("txt_d"));
            forecastWeatherBroadcast.put("today_txt_n",todayForecastCond.getString("txt_n"));
            forecastWeatherBroadcast.put("today_max",todayForecastTmp.getString("max"));
            forecastWeatherBroadcast.put("today_min",todayForecastTmp.getString("min"));
            forecastWeatherBroadcast.put("today_dir",todayForecastWind.getString("dir"));
            forecastWeatherBroadcast.put("today_sc",todayForecastWind.getString("sc"));

            //forecastWeatherBroadcast -- tomorrow
            forecastWeatherBroadcast.put("tomorrow_txt_d",tomorrowForecastCond.getString("txt_d"));
            forecastWeatherBroadcast.put("tomorrow_txt_n",tomorrowForecastCond.getString("txt_n"));
            forecastWeatherBroadcast.put("tomorrow_max",tomorrowForecastTmp.getString("max"));
            forecastWeatherBroadcast.put("tomorrow_min",tomorrowForecastTmp.getString("min"));
            forecastWeatherBroadcast.put("tomorrow_dir",tomorrowForecastWind.getString("dir"));
            forecastWeatherBroadcast.put("tomorrow_sc",tomorrowForecastWind.getString("sc"));


            forecastWeatherBroadcast.put("dayAfterTomorrow_txt_d",dayAfterTomorrowForecastCond.getString("txt_d"));
            forecastWeatherBroadcast.put("dayAfterTomorrow_txt_n",dayAfterTomorrowForecastCond.getString("txt_n"));
            forecastWeatherBroadcast.put("dayAfterTomorrow_max",dayAfterTomorrowForecastTmp.getString("max"));
            forecastWeatherBroadcast.put("dayAfterTomorrow_min",dayAfterTomorrowForecastTmp.getString("min"));
            forecastWeatherBroadcast.put("dayAfterTomorrow_dir",dayAfterTomorrowForecastWind.getString("dir"));
            forecastWeatherBroadcast.put("dayAfterTomorrow_sc",dayAfterTomorrowForecastWind.getString("sc"));


            WeatherInfo.put("todayActualWeatherInfo",todayActualWeatherInfo);
            WeatherInfo.put("todayForecastWeather",todayForecastWeather);
            WeatherInfo.put("tomorrowForecastWeather",tomorrowForecastWeather);
            WeatherInfo.put("dayAfterTomorrowForecastWeather",dayAfterTomorrowForecastWeather);
            WeatherInfo.put("weatherSuggestion",weatherSuggestion);
            WeatherInfo.put("actualWeatherBroadcast",actualWeatherBroadcast);
            WeatherInfo.put("forecastWeatherBroadcast",forecastWeatherBroadcast);

            return WeatherInfo;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return WeatherInfo;
    }




    public String MapToStr(Map<String,String> weatherInfoMap)
    {
        String returnStr = "";
        Iterator<String> keys = weatherInfoMap.keySet().iterator();

        while (keys.hasNext())
        {
            String key = keys.next().toString();
            returnStr = returnStr + key + " : ";
            returnStr = returnStr + weatherInfoMap.get(key) + " @ ";
        }
        return returnStr;
    }

    //weather broadcast
    public void weatherBroadcast(Map<String,Map<String,String>> broadcast)
    {

        Map<String,String> actualWeatherBroadcast = broadcast.get("actualWeatherBroadcast");
        Map<String,String> forecastWeatherBroadcast = broadcast.get("forecastWeatherBroadcast");



        System.out.println("现在天气 " + actualWeatherBroadcast.get("txt") + " 温度" + actualWeatherBroadcast.get("tmp") + "摄氏度 体感温度" + actualWeatherBroadcast.get("fl") + "摄氏度 相对湿度百分之" + actualWeatherBroadcast.get("hum") + " 能见度" + actualWeatherBroadcast.get("vis") + "公里 吹" + actualWeatherBroadcast.get("dir") + " 风力" + actualWeatherBroadcast.get("sc") + "级 PM2.5指数为" + actualWeatherBroadcast.get("pm25") + " 空气质量" + actualWeatherBroadcast.get("qlty"));
        System.out.println("今日预测 白天" + forecastWeatherBroadcast.get("today_txt_d") + " 夜间" + forecastWeatherBroadcast.get("today_txt_n") + " 最高温度" + forecastWeatherBroadcast.get("today_max") + "摄氏度 最低温度" + forecastWeatherBroadcast.get("today_min") + "摄氏度 风向" + forecastWeatherBroadcast.get("today_dir") + " 风力" + forecastWeatherBroadcast.get("today_sc"));
        System.out.println("明日预测 白天" + forecastWeatherBroadcast.get("tomorrow_txt_d") + " 夜间" + forecastWeatherBroadcast.get("tomorrow_txt_n") + " 最高温度" + forecastWeatherBroadcast.get("tomorrow_max") + "摄氏度 最低温度" + forecastWeatherBroadcast.get("tomorrow_min") + "摄氏度 风向" + forecastWeatherBroadcast.get("tomorrow_dir") + " 风力" + forecastWeatherBroadcast.get("tomorrow_sc"));
        System.out.println("后日预测 白天" + forecastWeatherBroadcast.get("dayAfterTomorrow_txt_d") + " 夜间" + forecastWeatherBroadcast.get("dayAfterTomorrow_txt_n") + " 最高温度" + forecastWeatherBroadcast.get("dayAfterTomorrow_max") + "摄氏度 最低温度" + forecastWeatherBroadcast.get("dayAfterTomorrow_min") + "摄氏度 风向" + forecastWeatherBroadcast.get("dayAfterTomorrow_dir") + " 风力" + forecastWeatherBroadcast.get("dayAfterTomorrow_sc"));

        StringBuffer sb = new StringBuffer();
        sb.append("现在天气 " + actualWeatherBroadcast.get("txt") + " 温度" + actualWeatherBroadcast.get("tmp") + "摄氏度 体感温度" + actualWeatherBroadcast.get("fl") + "摄氏度 相对湿度百分之" + actualWeatherBroadcast.get("hum") + " 能见度" + actualWeatherBroadcast.get("vis") + "公里 吹" + actualWeatherBroadcast.get("dir") + " 风力" + actualWeatherBroadcast.get("sc") + "级 PM2.5指数为" + actualWeatherBroadcast.get("pm25") + " 空气质量" + actualWeatherBroadcast.get("qlty"));
        sb.append("\n今日预测 白天" + forecastWeatherBroadcast.get("today_txt_d") + " 夜间" + forecastWeatherBroadcast.get("today_txt_n") + " 最高温度" + forecastWeatherBroadcast.get("today_max") + "摄氏度 最低温度" + forecastWeatherBroadcast.get("today_min") + "摄氏度 风向" + forecastWeatherBroadcast.get("today_dir") + " 风力" + forecastWeatherBroadcast.get("today_sc"));
        sb.append("\n明日预测 白天" + forecastWeatherBroadcast.get("tomorrow_txt_d") + " 夜间" + forecastWeatherBroadcast.get("tomorrow_txt_n") + " 最高温度" + forecastWeatherBroadcast.get("tomorrow_max") + "摄氏度 最低温度" + forecastWeatherBroadcast.get("tomorrow_min") + "摄氏度 风向" + forecastWeatherBroadcast.get("tomorrow_dir") + " 风力" + forecastWeatherBroadcast.get("tomorrow_sc"));
        sb.append("\n后日预测 白天" + forecastWeatherBroadcast.get("dayAfterTomorrow_txt_d") + " 夜间" + forecastWeatherBroadcast.get("dayAfterTomorrow_txt_n") + " 最高温度" + forecastWeatherBroadcast.get("dayAfterTomorrow_max") + "摄氏度 最低温度" + forecastWeatherBroadcast.get("dayAfterTomorrow_min") + "摄氏度 风向" + forecastWeatherBroadcast.get("dayAfterTomorrow_dir") + " 风力" + forecastWeatherBroadcast.get("dayAfterTomorrow_sc"));

//        return sb.toString();
    }
}

