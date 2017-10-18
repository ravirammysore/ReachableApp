package in.appfocus.reachable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Switch swReceiver,swAutoResponseSMS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!Utilities.hasAllPermissions(getApplicationContext()))
            Utilities.requestMissingPermissions(this);

        swReceiver = (Switch)  findViewById(R.id.swReceiver);
        swAutoResponseSMS = (Switch)  findViewById(R.id.swAutoResponseSMS);

        swReceiver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    turnOnReceiver();
                else
                    turnOffReceiver();

                //second switch UI state will depend on first switch
                swAutoResponseSMS.setEnabled(isChecked);
                swAutoResponseSMS.setChecked(isChecked);

            }
        });

        swAutoResponseSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                sendAutoResponseSMS();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: 18-10-2017 check status and update switch states accordingly 
    }

    private void turnOnReceiver(){
        // TODO: 18-10-2017 enable receiver
        // TODO: 18-10-2017 ask if the user wants to put phone on silent mode 
    }

    private void turnOffReceiver(){
        // TODO: 18-10-2017 disable receiver
        // TODO: 18-10-2017 put phone ringer on normal mode 
    }

    private void sendAutoResponseSMS(){
        // TODO: 18-10-2017 remember this preference
    }
    
    private void putPhoneOnSilentMode(){
        // TODO: 18-10-2017 reduce the ringer volume to min
    }
    
    private void putPhoneOnNormalMode(){
        // TODO: 18-10-2017 increase the ringer volume to max 
    }

}
