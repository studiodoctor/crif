package crif.android.sdk;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class DownloadService extends IntentService {

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();

    }
}
