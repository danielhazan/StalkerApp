package com.example.myspecialstalker;

import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BCreceiver extends BroadcastReceiver
{
    private final MutableLiveData<String> CallMade = new MutableLiveData<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            CallMade.postValue(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
        }
    }

    public MutableLiveData<String> status(){
        return CallMade;
    }
}
