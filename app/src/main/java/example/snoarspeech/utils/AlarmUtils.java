package example.snoarspeech.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by owen_ on 2016-04-27.
 */
public class AlarmUtils {

    public String getStringDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateString = formatter.format(date);
        return dateString;
    }

    public String hasZero(int i)
    {
        if(i < 10)
        {
            return "0"+ i;
        }
        else{
            return "" + i;
        }
    }
}

