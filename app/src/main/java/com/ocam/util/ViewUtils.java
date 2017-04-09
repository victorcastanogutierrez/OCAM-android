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

    /**
     * Método obtenido de documentación para seleccionar el tipo de mapa
     * entre los permitidos de Google Maps
     */
    public static void showMapTypeSelectorDialog(final Context context, final GoogleMap mMap) {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Tipo de mapa";
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = mMap.getMapType() - 1;

        CharSequence[] MAP_TYPE_ITEMS =
                {"Road Map", "Hybrid", "Satellite", "Terrain"};

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }
}
