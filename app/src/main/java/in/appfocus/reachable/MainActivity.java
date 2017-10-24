package in.appfocus.reachable;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("mytag","oncreate");
        Log.d("mytag",String.valueOf(this.hashCode()));

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("mytag","mainactivity-destroyed");
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
        // TODO: 24/10/17 start a sticky notification (which will open our app when clicked)
        Utilities.putPhoneToSilentMode(getApplicationContext());
        if(status==true){
            if(intReceiverStatus != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                pm.setComponentEnabledSetting(SMSReceiverComponent,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);

                intReceiverStatus=PackageManager.COMPONENT_ENABLED_STATE_ENABLED;

                Toast.makeText(this, "Reachable is now on, You can close the app", Toast.LENGTH_LONG).show();
            }

            //lets enable the switch, DO NOT on the switch! Leave it to the user to decide
            swAutoResponseSMS.setEnabled(true);
        }
        else{
            // TODO: 24/10/17 remove the sticky notification
            Utilities.putPhoneToSNormalMode(getApplicationContext());

            if (intReceiverStatus != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                pm.setComponentEnabledSetting(SMSReceiverComponent,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);

                intReceiverStatus=PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

                Toast.makeText(this, "Reachable is now off", Toast.LENGTH_LONG).show();
            }
            //let's off auto response and disable the switch
            swAutoResponseSMS.setChecked(false);
            swAutoResponseSMS.setEnabled(false);
        }
    }

    private void swAutoResponseSMSStatusChanged(Boolean status){
        //lets remember the switch state
        editor.putBoolean("isSwAutoResponseEnabled", status);
        editor.commit();
    }


}
