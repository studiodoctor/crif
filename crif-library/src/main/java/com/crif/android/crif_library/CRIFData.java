package com.crif.android.crif_library;

import android.content.Context;
import android.content.Intent;

public class CRIFData {

    public static DownloadService localService;
    public static boolean isBound = false;

    public static void UPLOAD_DATA(Context context, String id) {

        Intent intent = new Intent(Intent.ACTION_SYNC, null, context, DownloadService.class);
        intent.putExtra("Id", id);
        context.startService(intent);
    }
}
