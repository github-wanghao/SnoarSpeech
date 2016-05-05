package example.snoarspeech.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import example.snoarspeech.R;
import example.snoarspeech.database.DBManager;
import example.snoarspeech.service.OfflineSpeechAction;

/**
 * Created by ASUS on 2016/5/6.
 */
public class OfflineMassageEditActivity extends Activity {
private DBManager dbManager;
private Bundle bundle;
private EditText contactName,phoneNumber,content;
public List<String> phoneName = new ArrayList<String>();
public List<String> phoneId = new ArrayList<String>();
private String name = null;
private String Massage = null;
private String number = null;
private Toast info;
private boolean isnet;
SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);;
        setContentView(R.layout.massage_edit_activity);


        info=Toast.makeText(this, "", Toast.LENGTH_SHORT);
        Intent intent = getIntent();
        bundle = intent.getExtras();
        Log.v("intent", bundle.toString());
        if(bundle.getSerializable("Name") != null)  {
            name = bundle.getSerializable("Name").toString();
            ;
        }

        //  Log.v("name", name);
        final OfflineSpeechAction vbutton = new OfflineSpeechAction(this);
        vbutton.initIflytek();
        vbutton.speechRecognition();

        dbManager = new DBManager(this);

        contactName = (EditText)findViewById(R.id.massageContactNameEditText);
        phoneNumber = (EditText)findViewById(R.id.massagePhoneNumberEditText);
        content = (EditText)findViewById(R.id.massageContentEditText);

        if( name != null){
            Log.v("1", "1");
            contactName.setText(name);
        }

        Button returnButton = (Button)findViewById(R.id.massageEditReturnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(OfflineMassageEditActivity.this, MassegeActivity.class));;
                OfflineMassageEditActivity.this.finish();
            }
        });

        Button btnSendMassage = (Button)findViewById(R.id.massageEditDeleteButton);
        btnSendMassage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PhoneName = contactName.getText().toString();
                String Num = phoneNumber.getText().toString();
                Massage = content.getText().toString();
                if(Num.equals(null)){
                    if(Massage != null) {
                        SmsManager manager = SmsManager.getDefault();
                        ArrayList<String> list = manager.divideMessage(Massage);
                        Log.v("Num",String.valueOf(Num));
                        for (String text : list) {
                            manager.sendTextMessage(Num, null, text, null, null);

                        }
                        showTip("发送成功!");
                    }

                }else if(PhoneName != null){
                    if(Massage != null) {
                        SendMassage(PhoneName, Massage);
                    }
                }
            }
        });







        bundle = getIntent().getExtras();

//        loadPage(bundle.getInt("tag"));
    }

//    public void loadPage(int tag)
//    {
//        if(tag != -1)
//        {
//            Map<String,String> massageRecord = dbManager.queryOneMassageRecord(tag);
//            contactName.setText(massageRecord.get("contactName"));
//            phoneNumber.setText(massageRecord.get("phonenumber"));
//            content.setText(massageRecord.get("content"));
//        }
//    }






    public void SendMassage(String PhoneName ,String MassageContent){
        String GetPinYinName = getPinYin(PhoneName);
        char[] CharPinYin = PhoneName.toCharArray();
        FuzzySearch(String.valueOf(CharPinYin[0]), this);
        if (phoneName.size() > 0) {
            for (int i = 0; i < phoneName.size(); i++) {
                String GetFuzzyPinYin = getPinYin(phoneName.get(i));
                if (GetPinYinName.equals(GetFuzzyPinYin) && phoneId.size() > 0) {
                    Long PhoneNum = GainPhoneNum(phoneId.get(i), this);
                    number = String.valueOf(PhoneNum);
                    Log.v("na",PhoneName);
                    if (number == null) {

                    } else {

                        SmsManager manager = SmsManager.getDefault();
                        ArrayList<String> list = manager.divideMessage(Massage);
                        Log.v("Num", String.valueOf(number));
                        for (String text : list) {
                            manager.sendTextMessage(number, null, text, null, null);

                        }
                    }
                    if (number == null) {

                    } else {
                        //
                    }

                }
            }
        }
    }


    public  String getPinYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        char[] input = inputString.trim().toCharArray();
        StringBuffer output = new StringBuffer("");

        try {
            for (int i = 0; i < input.length; i++) {
                if (Character.toString(input[i]).matches("[\u4E00-\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(
                            input[i], format);
                    output.append(temp[0]);
                    output.append(" ");
                } else
                    output.append(Character.toString(input[i]));
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public void FuzzySearch(String key,Context context) {
        String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID };
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                ContactsContract.PhoneLookup.DISPLAY_NAME + " LIKE" + " '%"
                        + key + "%'", null, null); // Sort order.
        String[] SearchName = new String[cursor.getCount()];
        if (cursor == null) {

        }
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            String uname = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            phoneName.add(uname);
            String number = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            phoneId.add(number);
        }
        cursor.close();
    }


    public Long GainPhoneNum(String phoneNameId, Context context) {
        int i;
        Long phone = null;
        String phoneNumber = null;
		/*
		 * 閺屻儲澹樼拠銉ㄤ粓缁姹夐惃鍒緃one娣団剝浼?
		 */
        Cursor phones = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                        + phoneNameId, null, null);
        int phoneIndex = 0;
        if (phones.getCount() > 0) {
            phoneIndex = phones
                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        }
        while (phones.moveToNext()) {
            phoneNumber = phones.getString(phoneIndex);
            Log.i("null", phoneNumber);
        }
        phone = PhoneToNum(phoneNumber);
        return phone;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public Long PhoneToNum(String Num) {
        String No = null;
        char[] temp = new char[Num.length()];
        temp = Num.toCharArray();
        char[] copyPhone = Arrays.copyOf(temp, temp.length);
        String[] copy = new String[copyPhone.length];
        for (int n = 0; n < copyPhone.length; n++) {
            copy[n] = String.valueOf(copyPhone[n]);

            if (!copy[n].endsWith(" ")) {
                if (!copy[n].equals("-")) {

                    if (No == null) {
                        No = copy[n];
                    } else {
                        No = No + copy[n];
                    }
                }
            }
        }
        return Long.parseLong(No.trim());
    }
    private void showTip(final String str) {
        info.setText(str);
        info.show();
    }

}


