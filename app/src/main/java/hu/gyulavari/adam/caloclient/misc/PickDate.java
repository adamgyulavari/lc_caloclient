package hu.gyulavari.adam.caloclient.misc;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

import hu.gyulavari.adam.caloclient.MainActivity;

/**
 * Created by Adam on 2016.01.03..
 */
public class PickDate extends DialogFragment {
    private Integer mY, mM, mD;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year;
        int month;
        int day;

        if (mY == null || mM == null || mD == null) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        } else {
            year = mY;
            month = mM - 1;
            day = mD;
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)getActivity(), year, month, day);
    }

    public DialogFragment setData(Object s) {
        if (s != null) {
            String[] t = ((String)s).split("-");
            if (t.length == 3) {
                mY = Integer.parseInt(t[0]);
                mM = Integer.parseInt(t[1]);
                mD = Integer.parseInt(t[2]);
            }
        }
        return this;
    }
}
