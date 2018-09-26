package com.crif.android.crif_library;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

public class CRIFData {

    public static DownloadService localService;
    public static boolean isBound = false;

    public static void UPLOAD_DATA(Context context, String message) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(context, DownloadService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);

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
