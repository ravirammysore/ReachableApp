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

        initSwitches();

    }

    @Override
    protected void onResume() {
        super.onResume();
        recallSwitchStates();
    }

    private void initSwitches(){
        swReceiver = (Switch)  findViewById(R.id.swReceiver);
        swAutoResponseSMS = (Switch)  findViewById(R.id.swAutoResponseSMS);

        swReceiver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                swReceiverStatusChanged(isChecked);
            }
        });

        swAutoResponseSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                swAutoResponseSMSStatusChanged(isChecked);
            }
        });
    }

    private void recallSwitchStates(){

    }

    private void swReceiverStatusChanged(Boolean status){

    }

    private void swAutoResponseSMSStatusChanged(Boolean status){

    }

    private void putPhoneOnSilentMode(){
        // TODO: 18-10-2017 reduce the ringer volume to min
    }
    
    private void putPhoneOnNormalMode(){
        // TODO: 18-10-2017 increase the ringer volume to max 
    }

}
