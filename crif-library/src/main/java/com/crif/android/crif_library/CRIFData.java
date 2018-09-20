package com.crif.android.crif_library;

import android.content.Context;
import android.widget.Toast;

public class CRIFData {

    private Context context;

    public static void UPLOAD_DATA(Context context, String message) {

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();


    }
}
