package com.ocam.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by Victor on 02/04/2017.
 */

public class ViewUtils {

    public static void showToast(Context context, int duration, String text) {
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
