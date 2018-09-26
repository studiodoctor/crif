package com.crif.android.crif_library;

import android.content.Context;
import android.content.Intent;

public class CRIFData {

    public static DownloadService localService;
    public static boolean isBound = false;

    public static void UPLOAD_DATA(Context context, String message) {

        //  Toast.makeText(context, message, Toast.LENGTH_SHORT).show();


//        Intent playerservice = new Intent();
//        playerservice.setAction("com.crif.android.crif_library.DownloadService");
//
//        Intent explicitIntent = convertImplicitIntentToExplicitIntent(playerservice, context);
//        if(explicitIntent != null){
//            context.startService(explicitIntent);
//        }
        // context.bindService(playerservice,connection , Service.BIND_AUTO_CREATE);

//        Intent intent = new Intent("com.crif.android.crif_library.DownloadService");
//        intent.setPackage("com.crif.android.crif_library");
//        context.startService(intent);

        Intent intent = new Intent(Intent.ACTION_SYNC, null, context, DownloadService.class);
        context.startService(intent);
    }

//    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
//        //Retrieve all services that can match the given intent
//        PackageManager pm = context.getPackageManager();
//        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
//
//        //Make sure only one match was found
//        if (resolveInfo == null || resolveInfo.size() != 1) {
//            return null;
//        }
//
//        //Get component info and create ComponentName
//        ResolveInfo serviceInfo = resolveInfo.get(0);
//        String packageName = serviceInfo.serviceInfo.packageName;
//        String className = serviceInfo.serviceInfo.name;
//        ComponentName component = new ComponentName(packageName, className);
//
//        //Create a new intent. Use the old one for extras and such reuse
//        Intent explicitIntent = new Intent(implicitIntent);
//
//        //Set the component to be explicit
//        explicitIntent.setComponent(component);
//
//        return explicitIntent;
//    }
//
//    public static ServiceConnection connection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            DownloadService.LocalBinder binder = (DownloadService.LocalBinder) service;
//            localService = binder.getService();
//            isBound = true;
//
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            isBound = false;
//        }
//    };

}
