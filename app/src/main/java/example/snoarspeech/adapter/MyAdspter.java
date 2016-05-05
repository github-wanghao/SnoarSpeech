package example.snoarspeech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;


import example.snoarspeech.R;

import java.util.List;
import java.util.Map;

/**
 * Created by owen_ on 2016-04-21.
 */
public class MyAdspter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public MyAdspter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 组件集合，对应list.xml中的控件
     *
     * @author Administrator
     *         .
     */
    public final class Zujian {
        public ImageView image;
        public TextView title;
        public TextView text;
        public ToggleButton view;
    }

    @Override
    public int getCount() {
        return data.size();
    }


    @Override
    public Object getItem(int position) {
        return data.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Zujian zujian = null;
        if (convertView == null) {
            zujian = new Zujian();
            convertView = layoutInflater.inflate(R.layout.alarmclock_content, null);
            zujian.image = (ImageView) convertView.findViewById(R.id.alarmClockIcon);
            zujian.title = (TextView) convertView.findViewById(R.id.alarmClockTitle);
            zujian.text = (TextView) convertView.findViewById(R.id.alarmClockText);
            zujian.view = (ToggleButton) convertView.findViewById(R.id.mTogBtn);
            convertView.setTag(zujian);
        } else {
            zujian = (Zujian) convertView.getTag();
        }
        zujian.image.setBackgroundResource((Integer) data.get(position).get("image"));
        zujian.title.setText((String) data.get(position).get("title"));
        zujian.text.setText((String) data.get(position).get("text"));
        return convertView;
    }

}

