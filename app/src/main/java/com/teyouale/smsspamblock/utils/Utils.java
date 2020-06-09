package com.teyouale.smsspamblock.utils;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class Utils {
    /**
     * AlertDialog Box
     **/
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
