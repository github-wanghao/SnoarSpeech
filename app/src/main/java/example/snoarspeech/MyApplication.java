package example.snoarspeech;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;

import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.mapapi.SDKInitializer;

import example.snoarspeech.service.LocationService;


public class MyApplication extends Application {

    public LocationService locationService;
    public Vibrator mVibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        ApiStoreSDK.init(this, "ee91c474f435155dfa0fa4eeae3e8635");


        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());
    }
}

