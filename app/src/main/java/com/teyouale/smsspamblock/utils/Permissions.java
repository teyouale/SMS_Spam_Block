package com.teyouale.smsspamblock.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class Permissions{
    public static final String TAG = Permissions.class.getSimpleName();

    // Permissions names
    public static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS";

    // Request Code
    private static final int REQUEST_CODE = (Permissions.class.hashCode() & 0xffff);

    // Requesting Permissions
    public static void checkAndRequest(@NonNull final Activity context) {
        // Check the Platform
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check the Permission
            if(checkSelfPermission(context,Manifest.permission.RECEIVE_SMS )== PackageManager.PERMISSION_GRANTED){
                //Permissions/is Already available
                Log.d(TAG, "checkAndRequest: Permission is Available now");
            }
            // Explain to the user Why app requires a permissions
            else if(ActivityCompat.shouldShowRequestPermissionRationale(context,RECEIVE_SMS)){
                new AlertDialog.Builder(context)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed because of this and that")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(context,
                                        new String[] {RECEIVE_SMS},REQUEST_CODE);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            }
            // Requesting a Permission
            else {
                ActivityCompat.requestPermissions(context,new String[] {RECEIVE_SMS},REQUEST_CODE);
            }
        }
    }
}
