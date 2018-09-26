package com.crif.android.crif_library;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class DownloadService extends IntentService {

    public DownloadService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Toast.makeText(this, "Service Entered", Toast.LENGTH_SHORT).show();
    }
}
