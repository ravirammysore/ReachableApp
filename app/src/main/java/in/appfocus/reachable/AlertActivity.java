package in.appfocus.reachable;

import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class AlertActivity extends AppCompatActivity {
    String APP_NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        //APP_NAME = getResources().getString(R.string.app_name);
       // setTitle(APP_NAME);

        TextView tvInstructions = (TextView)findViewById(R.id.tvInstructions);
    }

    public void btnCloseClicked(View v){
            finish();
    }
}
