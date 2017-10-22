package in.appfocus.reachable;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Switch swReceiver,swAutoResponseSMS;

    ComponentName SMSReceiverComponent;
    PackageManager pm;
    int intReceiverStatus;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!Utilities.hasAllPermissions(getApplicationContext()))
            Utilities.requestMissingPermissions(this);

        SMSReceiverComponent = new ComponentName(this, MyReceiver.class);
        pm = this.getPackageManager();

        sp=getApplication().getSharedPreferences("myPreferences",0);
        editor = sp.edit();

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
        intReceiverStatus = pm.getComponentEnabledSetting(SMSReceiverComponent);

        if(intReceiverStatus == PackageManager.COMPONENT_ENABLED_STATE_ENABLED){
            swReceiver.setChecked(true);
        }

        else{
            swReceiver.setChecked(false);
        }

        Boolean isSwAutoResponseEnabled =sp.getBoolean("isSwAutoResponseEnabled",false);
        swAutoResponseSMS.setChecked(isSwAutoResponseEnabled);
    }

    private void swReceiverStatusChanged(Boolean status){
        if(status==true){
            if(intReceiverStatus != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                pm.setComponentEnabledSetting(SMSReceiverComponent,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);

                intReceiverStatus=PackageManager.COMPONENT_ENABLED_STATE_ENABLED;

                Toast.makeText(this, "Reachable is now on", Toast.LENGTH_SHORT).show();
            }

            //lets enable the switch, DO NOT on the switch! Leave it to the user to decide
            swAutoResponseSMS.setEnabled(true);
        }
        else{
            if (intReceiverStatus != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                pm.setComponentEnabledSetting(SMSReceiverComponent,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);

                intReceiverStatus=PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

                Toast.makeText(this, "Reachable is now off", Toast.LENGTH_SHORT).show();
            }
            //lets off auto response and disable the switch
            swAutoResponseSMS.setChecked(false);
            swAutoResponseSMS.setEnabled(false);
        }
    }

    private void swAutoResponseSMSStatusChanged(Boolean status){
        //lets remember the switch state
        editor.putBoolean("isSwAutoResponseEnabled", status);
        editor.commit();
    }

    private void putPhoneOnSilentMode(){
        // TODO: 18-10-2017 reduce the ringer volume to min
    }
    
    private void putPhoneOnNormalMode(){
        // TODO: 18-10-2017 increase the ringer volume to max 
    }

}
