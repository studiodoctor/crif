package com.crif.android.crif_library;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.widget.Toast;

import java.util.List;

public class CRIFData {

    public static DownloadService localService;
    public static boolean isBound = false;

    public static void UPLOAD_DATA(Context context, String message) {

      //  Toast.makeText(context, message, Toast.LENGTH_SHORT).show();


        Intent playerservice = new Intent();
        playerservice.setAction("com.crif.android.crif_library.DownloadService");

        Intent explicitIntent = convertImplicitIntentToExplicitIntent(playerservice, context);
        if(explicitIntent != null){
            context.startService(explicitIntent);
        }

       // context.bindService(playerservice,connection , Service.BIND_AUTO_CREATE);

    }

    public static Intent convertImplicitIntentToExplicitIntent(Intent implicitIntent, Context context) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryIntentServices(implicitIntent, 0);

        if (resolveInfoList == null || resolveInfoList.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfoList.get(0);
        ComponentName component = new ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    public static ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DownloadService.LocalBinder binder = (DownloadService.LocalBinder) service;
            localService = binder.getService();
            isBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

}
