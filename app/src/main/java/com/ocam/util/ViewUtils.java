package com.ocam.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Victor on 02/04/2017.
 */

public class ViewUtils {

    public static void showToast(Context context, int duration, String text) {
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
