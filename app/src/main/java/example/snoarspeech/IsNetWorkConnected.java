package example.snoarspeech;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class IsNetWorkConnected {

    public boolean net(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return true;
            }
        }
        return false;
    }
}
