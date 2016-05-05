package example.snoarspeech.model;

import java.util.Calendar;

/**
 * Created by owen_ on 2016-04-28.
 */
public class AlarmClock {

    public int id;
    public String alarmName;
    public int hour, minute;
    public String memorandum;

    public AlarmClock(int id, String alarmName, Calendar cal, String memorandum) {
        this.id = id;
        this.alarmName = alarmName;
        this.hour = cal.get(Calendar.HOUR_OF_DAY);
        this.minute = cal.get(Calendar.MINUTE);
        this.memorandum = memorandum;
    }

    public  AlarmClock(){}
}
