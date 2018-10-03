package crif.android.sdk;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.crif.android.crif_library.CRIFData;
import com.crif.android.crif_library.DataInterface;

public class Master extends AppCompatActivity implements DataInterface {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CRIFData.UPLOAD_DATA(Master.this, "13");
            }
        });

    }
    @Override
    public void callfromlibrary() {

        Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();

    }
}
