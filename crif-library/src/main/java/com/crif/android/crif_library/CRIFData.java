package com.crif.android.crif_library;

import android.content.Context;
import android.content.Intent;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

public class CRIFData {

    public static DownloadService localService;
    public static boolean isBound = false;

    public static void UPLOAD_DATA(Context context, String id, GoogleAccountCredential googleCredentials) {

        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("Id", id);
        intent.putExtra("GoogleCredentials", String.valueOf(googleCredentials));
        context.startService(intent);

    }
}
