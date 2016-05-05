package example.snoarspeech.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gc.materialdesign.views.LayoutRipple;

import example.snoarspeech.activity.AlarmClockMainActivity;
import example.snoarspeech.activity.MainActivity;
import example.snoarspeech.activity.MassegeActivity;
import example.snoarspeech.activity.PhoneActivity;
import example.snoarspeech.R;
import example.snoarspeech.activity.RouteMapActivity;
import example.snoarspeech.activity.WeatherActivity;
import example.snoarspeech.service.OnlineSpeechAction;

public class ChatMsgViewAdapter extends BaseAdapter {
    int backgroundColor = Color.parseColor("#1E88E5");
    public ArrayList<OnlineSpeechAction.SiriListItem> list;
    private Context ctx;
    private LayoutInflater mInflater;
    private boolean isnet;
    /*private LocationService locationService;*/
    /*IsNetWorkConnected isNetWorkConnected = new IsNetWorkConnected();*/

    public ChatMsgViewAdapter(Context context, ArrayList<OnlineSpeechAction.SiriListItem> l) {
        ctx = context;
        list = l;
        mInflater = LayoutInflater.from(context);
    }




    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        Activity context = (Activity)ctx;
        ViewHolder viewHolder = null;
        OnlineSpeechAction.SiriListItem item=list.get(position);
        if(position == 0 && context instanceof MainActivity){
            convertView = mInflater.inflate(R.layout.main_ui,null);
            LayoutRipple layoutRipple = (LayoutRipple)convertView.findViewById(R.id.phoneitems);
           layoutRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(ctx, PhoneActivity.class);
                    intent.putExtra("BACKGROUND", backgroundColor);
                    ctx.startActivity(intent);
                }
            });

            LayoutRipple Massage = (LayoutRipple)convertView.findViewById(R.id.messageitems);
            Massage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(ctx, MassegeActivity.class);
                    intent.putExtra("BACKGROUND", backgroundColor);
                    ctx.startActivity(intent);
                }
            });

            LayoutRipple Weather = (LayoutRipple)convertView.findViewById(R.id.weatheritems);
            Weather.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(ctx, WeatherActivity.class);
                    intent.putExtra("BACKGROUND", backgroundColor);
                    ctx.startActivity(intent);
                }
            });




            LayoutRipple Widgets = (LayoutRipple)convertView.findViewById(R.id.itemWidgets);
            Widgets.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(ctx, AlarmClockMainActivity.class);
                    intent.putExtra("BACKGROUND", backgroundColor);
                    ctx.startActivity(intent);
                }
            });


            LayoutRipple noteitems = (LayoutRipple)convertView.findViewById(R.id.noteitems);
            noteitems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(ctx, RouteMapActivity.class);
                    intent.putExtra("BACKGROUND", backgroundColor);
                    ctx.startActivity(intent);
                }
            });

        }
        else{

            convertView = mInflater.inflate(R.layout.list_item, null);
            viewHolder=new ViewHolder(
                    (View) convertView.findViewById(R.id.list_child),
                    (TextView) convertView.findViewById(R.id.chat_msg)
            );
            convertView.setTag(viewHolder);

            String siri = String.valueOf(item.isSiri);
            if(item.isSiri){
                viewHolder.child.setBackgroundResource(R.drawable.msgbox_rec);
            }
            else {
                Log.v("1234","?????");
                viewHolder.child.setBackgroundResource(R.drawable.msgbox_send);
            }

            viewHolder.msg.setText(item.message);
            Log.v("????",item.message);

        }

        return convertView;
    }

    class ViewHolder {
        protected View child;
        protected TextView msg;
        public ViewHolder(View child, TextView msg){
            this.child = child;
            this.msg = msg;
        }
    }

}
