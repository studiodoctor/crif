package com.crif.android.crif_library;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CRIFData {

    public static void UPLOAD_DATA(Context context, String message) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        Intent playerservice = new Intent();
        playerservice.setAction("MYSERVICE");
        context.startService(playerservice);
//       context.bindService(playerservice,conn , Service.BIND_AUTO_CREATE);

    }
}
