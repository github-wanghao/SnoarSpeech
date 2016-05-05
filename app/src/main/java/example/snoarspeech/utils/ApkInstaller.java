package example.snoarspeech.utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;

import com.iflytek.cloud.SpeechUtility;





public class ApkInstaller {
    private Activity mActivity ;

    public ApkInstaller(Activity activity) {
        mActivity = activity;
    }

    public void install(){
        AlertDialog.Builder builder = new Builder(mActivity);
        builder.setMessage("检测到您未安装语记！\n是否前往下载语记？");
        builder.setTitle("下载提示");
        builder.setPositiveButton("确认前往", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String url = SpeechUtility.getUtility().getComponentUrl();
                String assetsApk="SpeechService.apk";
                processInstall(mActivity, url,assetsApk);
            }
        });
        builder.setNegativeButton("残忍拒绝", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
        return;
    }

    private boolean processInstall(Context context ,String url,String assetsApk){
        Uri uri = Uri.parse(url);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(it);
        return true;
    }
}
