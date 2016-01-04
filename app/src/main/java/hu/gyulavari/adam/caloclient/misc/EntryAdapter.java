package hu.gyulavari.adam.caloclient.misc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import hu.gyulavari.adam.caloclient.R;
import hu.gyulavari.adam.caloclient.model.Entry;

/**
 * Created by Adam on 2016.01.03..
 */
public class EntryAdapter extends ArrayAdapter<Entry> {
    public EntryAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_entry, parent, false);

        Entry current = getItem(position);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView num = (TextView) convertView.findViewById(R.id.num);
        TextView time = (TextView) convertView.findViewById(R.id.time);

        title.setText(current.title);
        num.setText(String.valueOf(current.num));
        time.setText(current.entry_date + " " + Utils.toTime(current.entry_time));

        return convertView;
    }

    public int getSum() {
        int sum = 0;
        for(int i = 0; i < getCount(); i++)
            sum+=getItem(i).num;
        return sum;
    }
}
