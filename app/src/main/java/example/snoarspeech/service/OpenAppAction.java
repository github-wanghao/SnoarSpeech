package example.snoarspeech.service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Created by ASUS on 2016/5/5.
 */
public class OpenAppAction {
    // 打开App或wifi
    Context ctx;
    public OpenAppAction(Context context){
        this.ctx = context;
    }

    public Boolean OpenApplication(String AppName) {
        final PackageManager packageManager = ctx
                .getPackageManager();
        final List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);


        for (int i = 0; i < pInfo.size(); i++) {
            PackageInfo p = pInfo.get(i);
            String label = packageManager
                    .getApplicationLabel(p.applicationInfo).toString();
            if (label.equals(AppName.trim())) { // 比较label
                String pName = p.packageName; // 获取包名
                Intent intent = new Intent();
                intent = packageManager.getLaunchIntentForPackage(pName);
                ctx.startActivity(intent);
                return true;
            }

        }
        return false;
    }
}

