package com.ocam.osm;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.maps.GoogleMap;

import org.osmdroid.views.MapView;

import java.util.List;


public class MapTypeSelector {

    private static int selected = 0;

    /**
     * Método para seleccionar el tipo de mapa deseado
     */
    public static void show(final Context context, final MapView mapView) {
        final String fDialogTitle = "Tipo de mapa";
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(fDialogTitle);
        final MapTypesManager manager = MapTypesManager.getInstance();
        final List<String> mapListName = manager.getMapTypeNames();

        CharSequence[] MAP_TYPE_ITEMS =
                mapListName.toArray(new CharSequence[mapListName.size()]);

        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                selected,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        selected = item;
                        manager.getMapTypes().get(mapListName.get(item)).setMap(mapView);
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
