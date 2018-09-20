package crif.android.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Master extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);


    }

    public static void START_SERVICE(Context context) {

        Intent intent = new Intent(context, DownloadService.class);

        context.startService(intent);
    }
}
