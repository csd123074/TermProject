package com.example.nj_ba.termproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PhoneCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String sPhoneNum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Toast.makeText(context, "Phone # = " + sPhoneNum, Toast.LENGTH_SHORT).show();
        } else if (action.equals(Intent.ACTION_CALL_BUTTON)) {
            String sKey = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Toast.makeText(context, "Call button = " + sKey, Toast.LENGTH_SHORT).show();
        }
    }
}