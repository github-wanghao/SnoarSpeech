package example.snoarspeech.service;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by ASUS on 2016/4/28.
 */
public class BrightnessAction {

    private Context ctx;
    public BrightnessAction(Context context){
        ctx = context;
    }

    public int screenBrightness_check() {

        try {
            if (android.provider.Settings.System.getInt(ctx.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE) == android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                android.provider.Settings.System
                        .putInt(ctx.getContentResolver(),
                                android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,
                                android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        int intScreenBrightness = (int) (android.provider.Settings.System
                .getInt(ctx.getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS, 255));
        String stringScreenBrightness = String.valueOf(intScreenBrightness);
        Log.d("test", stringScreenBrightness);
        return intScreenBrightness;
    }


    public void setScreenBritness(int brightness) {
        String StringBrightness = String.valueOf(brightness);
        Log.d("BrightnessBefore", StringBrightness);
        if (brightness <= 1) {
            brightness = 1;
        } else if (brightness >= 255) {
            brightness = 255;
        }

        WindowManager.LayoutParams lp = ((Activity)ctx).getWindow().getAttributes();

        lp.screenBrightness = Float.valueOf(brightness / 255f);
        ((Activity)ctx).getWindow().setAttributes(lp);
        Log.d("BrightnessAfter", StringBrightness);


        android.provider.Settings.System.putInt(ctx.getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);

    }
}

