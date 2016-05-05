package example.snoarspeech.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import example.snoarspeech.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2016/4/16.
 */
public class NewsActivity extends Activity {
    List Viewlist = new ArrayList();
    Bitmap bitmap = null;
    ListView listView ;
    //    ImageView imgView;
    URL url= null;
//    TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.news_view);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String httpUrl = (String)bundle.getSerializable("Url");
        listView = (ListView) findViewById(R.id.listView_News);

        new Thread(){
            @Override
            public void run()
            {

                //String httpUrl ="http://apis.baidu.com/txapi/world/world";
                Log.v("news",httpUrl);
                String httpArg = "num=20&page=1";
                String jsonResult = request(httpUrl, httpArg);
                System.out.println(jsonResult);

                try {
                    JSONObject person = new JSONObject(jsonResult);
                    JSONArray list = person.getJSONArray("newslist");
                    //  String name = list.getString(1);
                    for(int i = 0 ;i < list.length();i++) {
                        JSONObject object = list.getJSONObject(i);

                        Viewlist.add(object);
                        init(Viewlist);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }.start();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                Object object = Viewlist.get(position);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(String.valueOf(object));
                    try {
                        String url = jsonObject.getString("url");

                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button ReturnNews = (Button)findViewById(R.id.phoneReturnButton);
        ReturnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ret = new Intent(NewsActivity.this,MainActivity.class);
                startActivity(ret);
            }
        });
    }



    private void init(final List list) {

        if (list == null) {
            // Toast.makeText(this, "wifi未锟津开ｏ拷", Toast.LENGTH_LONG).show();
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.setAdapter(new MyAdapter(NewsActivity.this, list));
                }
            });
        }

    }



    public class MyAdapter extends BaseAdapter {

        LayoutInflater inflater  = LayoutInflater.from(NewsActivity.this);
        List list;

        public MyAdapter(Context context, List list) {
            // TODO Auto-generated constructor stub
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            //  View view = null;
            ViewHolder viewHolder = new ViewHolder();;
            if(convertView == null ) {
                convertView = inflater.inflate(R.layout.news_items, null);
                viewHolder.imgView = (ImageView) convertView.findViewById(R.id.txtImg);
                viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txtNews);
                convertView.setTag(viewHolder);

            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Object object = list.get(position);
            try {
                JSONObject jsonObject = new JSONObject(String.valueOf(object));
                String title = jsonObject.getString("title");
                //  Uri url = Uri.parse(jsonObject.getString("picUrl"));
                try {
                    url = new URL(jsonObject.getString("picUrl"));
                    viewHolder.imgView.setTag(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                viewHolder.imgView.setImageResource(R.drawable.note);
                ImageView imageView = (ImageView)listView.findViewWithTag(url);
                //    Log.v("tag",viewHolder.imgView.getTag().toString());
//                if(imageView != null){
//                    Log.v("in","in");
//                    imageView.setImageBitmap(bitmap);
//                }

                if (viewHolder.imgView.getTag() != null && viewHolder.imgView.getTag().equals(url)) {
//                    new Thread(downloadRun).start();
                    //   Log.v("bitmap",bitmap.toString());
//                    viewHolder.imgView.clearAnimation();
//                    viewHolder.imgView.setImageBitmap(bitmap);
                }
                new Thread(downloadRun).start();
                viewHolder.imgView.setImageBitmap(bitmap);;


                viewHolder.txtTitle.setText(title);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }

    class ViewHolder {
        ImageView imgView;
        TextView txtTitle;


    }




    Runnable downloadRun = new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                Log.v("in",url.toString());
                bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
                if(bitmap.toString().equals(null)){
                    Log.v("null","null");
                }
            } catch (IOException e) {
                Log.v("error","error");
                e.printStackTrace();
            }

        }
    };




    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("apikey",  "12809d7195c413d8f1459397293500df");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

