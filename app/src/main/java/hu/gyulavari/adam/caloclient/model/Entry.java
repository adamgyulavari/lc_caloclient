package hu.gyulavari.adam.caloclient.model;

import hu.gyulavari.adam.caloclient.misc.Utils;

/**
 * Created by Adam on 2016.01.02..
 */
public class Entry {
    public int id;
    public String title;
    public int num;
    public String entry_date;
    public int entry_time;

    public Entry() {
    }

    public Entry(String title, int num, String entry_date, String entry_time) {
        this.title = title;
        this.num = num;
        this.entry_date = entry_date;
        if (entry_time != null) {
            this.entry_time = Utils.fromTime(entry_time);
        }
    }
}
