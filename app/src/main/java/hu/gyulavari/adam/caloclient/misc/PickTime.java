package hu.gyulavari.adam.caloclient.misc;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by Adam on 2016.01.03..
 */
public class PickTime extends DialogFragment {

    private Integer mHour, mMin;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour, min;
        if (mHour == null || mMin == null) {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            min = c.get(Calendar.MINUTE);
        } else {
            hour = mHour;
            min = mMin;
        }

        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener)getActivity(), hour, min, true);
    }

    public DialogFragment setData(Object s) {
        if (s != null) {
            String[] t = ((String)s).split(":");
            if (t.length == 2) {
                mHour = Integer.parseInt(t[0]);
                mMin = Integer.parseInt(t[1]);
            }
        }
        return this;
    }
}
