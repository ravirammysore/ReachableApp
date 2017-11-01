package in.appfocus.reachable;


import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_NOTIFICATION_POLICY;

/**
 * Created by Ravi on 15/05/2017.
 */

public class Utilities {

    //can use the below four members across apps :)
    static ArrayList<String> lstPermissionsMissing = new ArrayList<>();
    //Add all permissions needed by the app here

    public static String[] PERMISSIONS_ALL = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.ACCESS_NOTIFICATION_POLICY
    };

    public static boolean hasAllPermissions(Context context) {

        //in case of older android versions, this function will return true
        Boolean result = true;
        try{
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (String permission : Utilities.PERMISSIONS_ALL) {
                    if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        lstPermissionsMissing.add(permission);
                        result = false;
                    }
                }
            }
        }
        catch (Exception ex){
            Toast.makeText(context, "Error while checking permissions", Toast.LENGTH_SHORT).show();

        }

        return result;
    }

    public static void requestMissingPermissions(Activity activity){
        try{
            //we will request only the missing permissions
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(lstPermissionsMissing.size()>0){
                    String[] PERMISSIONS_MISSING = lstPermissionsMissing.toArray(new String[lstPermissionsMissing.size()]);
                    ActivityCompat.requestPermissions(activity,PERMISSIONS_MISSING, 1000);
                }
            }
        }
        catch (Exception ex){
            Toast.makeText(activity, "Error while requesting permissions", Toast.LENGTH_SHORT).show();
        }
    }

    public static void putPhoneToSilentMode(Context context){
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    public static void putPhoneToSNormalMode(Context context){
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    public static void increaseMediaVolume(Context context){
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
    }
}
