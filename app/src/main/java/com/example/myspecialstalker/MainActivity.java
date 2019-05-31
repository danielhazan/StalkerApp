package com.example.myspecialstalker;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;



public class MainActivity extends AppCompatActivity {
    public SharedPreferences sp;
    final BCreceiver bCreciever = new BCreceiver();
    EditText targetSMS ;
    EditText contentSMS;
    TextView infoMissing;
    final String READY_MSG = "ready!!!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        targetSMS = findViewById(R.id.numPhone);
        contentSMS = findViewById(R.id.Cont);
        infoMissing = findViewById(R.id.MissingInformation);
        IntentFilter myFilter = new IntentFilter();
        myFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        myFilter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(bCreciever,myFilter);

        //now checking if already has permissions

        boolean hasPermissionForSMS = ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PERMISSION_GRANTED;
        boolean hasPermissionForCalling = ActivityCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS)  == PERMISSION_GRANTED;
        boolean hasPermissionForPhoneStatus = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)  == PERMISSION_GRANTED;

        if (hasPermissionForCalling && hasPermissionForPhoneStatus && hasPermissionForSMS){
            initializeSettings();
        }
        else {
            requestPermissionFromUser();

        }



        bCreciever.status().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (!s.isEmpty()){
                    sendSMS(s);
                }
            }
        });


    }

    public void requestPermissionFromUser(){

        String[] permits = new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.PROCESS_OUTGOING_CALLS
        };
        ActivityCompat.requestPermissions(this,permits,1546);

    }

    @Override
    public void onRequestPermissionsResult (int requestCode,
                                          @NonNull String[] permossions,
                                          @NonNull int[] grantResults)
    {
        int granted = PackageManager.PERMISSION_GRANTED;
        if(grantResults.length == 3){
            if(grantResults[0] == granted && grantResults[1] == granted && grantResults[2] == granted){
                initializeSettings();
            }
            else {
                ActivityCompat.requestPermissions(this,permossions,1546);
            }
        }
    }

    public void initializeSettings(){

        targetSMS.addTextChangedListener(MyWatcher);
        contentSMS.addTextChangedListener(MyWatcher);
    }

    private final TextWatcher MyWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String phonenum;
            String content;
            phonenum = targetSMS.getText().toString().trim();
            content = contentSMS.getText().toString().trim();

            if(!phonenum.isEmpty() && !content.isEmpty()){
                infoMissing.setText(READY_MSG);
                infoMissing.setTextColor(Color.parseColor("#26EE16"));

            }
            else{
                infoMissing.setText("MISSING INFORMATION!! \nplease fill in all desired fields");
                infoMissing.setTextColor(Color.parseColor("#F10D1F"));

            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    } ;

    public void sendSMS(String phoneNum){
        if(infoMissing.getText().equals(READY_MSG)){
            SmsManager.getDefault().sendTextMessage(targetSMS.getText().toString().trim(),null,
                    contentSMS.getText().toString().trim() + " " + phoneNum,null,null);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("contentSNS",contentSMS.getText().toString());
        editor.putString("targetSMS",targetSMS.getText().toString());
        editor.apply();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistableBundle){
        super.onRestoreInstanceState(savedInstanceState,persistableBundle);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        targetSMS.setText(sp.getString("targetSMS",null));
        contentSMS.setText(sp.getString("contentSMS",null));
    }
}
