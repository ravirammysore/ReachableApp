package in.appfocus.reachable;

import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class AlertActivity extends AppCompatActivity {
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        setTitle("");

        mp = MediaPlayer.create(this, R.raw.siren10sec);
        mp.setLooping(true);
        // TODO: 24/10/17 player does not play when locked :(

        //can we use alarm manager to play a sound instead?

        //throwing exception, need to see wjat this funda about wake locks is
        //mp.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mp.start();

        Log.d("mytag","onCreate-AlertActivity-"+this.hashCode());
    }

    public void btnStopClicked(View v){

        if(mp.isPlaying()) mp.stop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mp.isPlaying()) mp.stop();
        Log.d("mytag","onDestroy-AlertActivity-"+this.hashCode());
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mp.isPlaying()) mp.stop();
        Log.d("mytag","onStop-AlertActivity-"+this.hashCode());
    }
}
