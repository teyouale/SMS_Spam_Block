package com.teyouale.smsspamblock.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static androidx.core.content.ContextCompat.checkSelfPermission;


public class Permissions {
    public static final String TAG = Permissions.class.getSimpleName();
    public static final Map<String, Boolean> permissionsResults = new ConcurrentHashMap<>();
    // Permissions names
    public static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS";
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final String WRITE_SMS = "android.permission.WRITE_SMS";
    public static final String READ_SMS = "android.permission.READ_SMS";
    public static final String SEND_SMS = "android.permission.SEND_SMS";
    public static final String READ_CONTACTS = "android.permission.READ_CONTACTS";
    public static final String WRITE_CONTACTS = "android.permission.WRITE_CONTACTS";
    public static final String READ_CALL_LOG = "android.permission.READ_CALL_LOG";
    public static final String WRITE_CALL_LOG = "android.permission.WRITE_CALL_LOG";
    public static final String VIBRATE = "android.permission.VIBRATE";

    // All Permissions in ArrayList
    private static ArrayList<String> PERMISSIONS = new ArrayList<>(Arrays.asList(
            RECEIVE_SMS, WRITE_EXTERNAL_STORAGE, WRITE_SMS, READ_SMS, SEND_SMS,
            READ_CONTACTS, WRITE_CONTACTS, READ_CALL_LOG, WRITE_CALL_LOG, VIBRATE
    ));

    // Request Code
    private static final int REQUEST_CODE = (Permissions.class.hashCode() & 0xffff);

    // Checking Permission Status
    public static boolean isPermissionGranted(Activity context, String permission) {
        // Check the Platform
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // it Granted On Installation Time
            return true;
        }
        Boolean result = permissionsResults.get(permission);
        if (result == null) {
            int permissionCheck = checkSelfPermission(context, permission);
            result = (permissionCheck == PackageManager.PERMISSION_GRANTED);
            //permissionsResults.put(permission, result);
        }
        return result;
    }

    // Requesting Permissions
    public static void checkAndRequest(@NonNull final Activity context) {
        // Check the Platform
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check the Permission
            if (checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
                //Permissions/is Already available
                Log.d(TAG, "checkAndRequest: Permission is Available now");
            }
            // Explain to the user Why app requires a permissions
            else if (ActivityCompat.shouldShowRequestPermissionRationale(context, RECEIVE_SMS)) {
                new AlertDialog.Builder(context)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed because of this and that")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(context,
                                        new String[]{RECEIVE_SMS}, REQUEST_CODE);
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
                ActivityCompat.requestPermissions(context, new String[]{RECEIVE_SMS}, REQUEST_CODE);
            }
        }
    }

    public static void check(@NonNull final Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new LinkedList<>();
            for (String permission : PERMISSIONS) {
                if (!isPermissionGranted(context, permission)) {
                    permissions.add(permission);
                }
            }
            if (!permissions.isEmpty()) {
                String[] array = permissions.toArray(new String[permissions.size()]);
                ActivityCompat.requestPermissions(context, array, REQUEST_CODE);
            }
        }
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, final Activity context) {
        if (requestCode == REQUEST_CODE && permissions.length == grantResults.length) {
            HashMap<String, Integer> ps = new HashMap<>();
            int deniedCount = 0;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    ps.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }
            if (deniedCount == 0) {
                // process
            } else {
                for (Map.Entry<String, Integer> entry : ps.entrySet()) {
                    String permName = entry.getKey();
                    int permResult = entry.getValue();

                    if (ActivityCompat.shouldShowRequestPermissionRationale(context, permName)) {

                        showDialog(context, "", "This App needs This Permissiion", "Yes ,Grant Permission",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        check(context);
                                    }
                                }, "No Exit App", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        context.finish();
                                    }
                                }
                                ,false
                        );
                    }
                    else {
                        showDialog(context, "", "You Have Denied Some Permissions Allow all permission at Setting ->Permission",
                                "Go to the Setting",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        // Go to app Setting
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package",context.getPackageName(),null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                        context.finish();
                                    }
                                }, "No Exit App", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        context.finish();
                                    }
                                }
                                ,false);
                        break;
                    }
                }
            }

        }
    }
    public static AlertDialog showDialog(Activity context, String title, String msg, String postiveLable,
                                         DialogInterface.OnClickListener positivrOnClick,
                                         String negativeLabel, DialogInterface.OnClickListener negativeOnClick, boolean isCanceAble) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setCancelable(isCanceAble).setMessage(msg)
                .setPositiveButton(postiveLable, positivrOnClick)
                .setNegativeButton(negativeLabel, negativeOnClick);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }
}
