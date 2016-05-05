package example.snoarspeech.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

import example.snoarspeech.MyApplication;
import example.snoarspeech.service.LocationService;
import example.snoarspeech.R;


/**
 * Created by owen_ on 2016-05-03.
 */
public class WeatherChooseActivity extends Activity {

    private LocationService locationService;

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_choose_activity);

        Button returnButton = (Button)findViewById(R.id.weatherChooseReturnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WeatherChooseActivity.this,WeatherActivity.class));
                WeatherChooseActivity.this.finish();
            }
        });

        final EditText placeInput = (EditText)findViewById(R.id.placeInput);
        Button comfirmButton = (Button)findViewById(R.id.placeInputComfirmButton);
        comfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeatherActivity.chosePlace = placeInput.getText().toString();
                startActivity(new Intent(WeatherChooseActivity.this,WeatherActivity.class));
                WeatherChooseActivity.this.finish();
            }
        });

        Button weatherLocationButton = (Button)findViewById(R.id.weatherChooseLocationButton);
        weatherLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationService.start();
                locationService.stop();
            }
        });

    }

    //瀹氫綅閮ㄥ垎
    /***
     * Stop location service
     */
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

            WeatherActivity.nowLocation = location.getCity().toString();
            WeatherActivity.chosePlace = null;
            startActivity(new Intent(WeatherChooseActivity.this,WeatherActivity.class));
            WeatherChooseActivity.this.finish();

        }
    };
}

