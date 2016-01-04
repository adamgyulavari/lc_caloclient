package hu.gyulavari.adam.caloclient.model;

import hu.gyulavari.adam.caloclient.misc.Utils;

/**
 * Created by Adam on 2016.01.03..
 */
public class Filter {
    public String fromDate;
    public String toDate;
    public int fromTime;
    public int toTime;

    public Filter(Object fromDate, Object toDate, Object fromTime, Object toTime) {
        if (fromDate != null)
            this.fromDate = (String)fromDate;
        if (toDate != null)
            this.toDate = (String)toDate;
        if (fromTime != null)
            this.fromTime = Utils.fromTime((String)fromTime);
        if (toTime != null)
            this.toTime = Utils.fromTime((String)toTime);
    }

    public boolean noFilter() {
        return fromDate == null && toDate == null && fromTime == 0 && toTime == 0;
    }
}
