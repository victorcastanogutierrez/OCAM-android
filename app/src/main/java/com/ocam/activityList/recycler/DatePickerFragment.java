package com.ocam.activityList.recycler;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ocam.R;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        Bundle date = this.getArguments();
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getLong("date"));

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }

    public void setOnDateCallback(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }
}