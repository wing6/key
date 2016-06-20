package com.wings6.nature;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/2/17.
 */
public class PhoneStatReceiver extends BroadcastReceiver {

    private static final String TAG1 = "PhoneStatReceiver";
    private static boolean incomingFlag = false;
    private static String incoming_number = null;



    @Override
    public void onReceive(Context context, Intent intent) {
//        //如果是拨打电话
//        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
//            incomingFlag = false;
//            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//            Log.i(TAG1, "call OUT:" + phoneNumber);
//            Toast.makeText(context, "正在拨打电话中", Toast.LENGTH_SHORT).show();
//
//        }else{
//            //如果是来电
//            TelephonyManager tm =
//                    (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
//            switch (tm.getCallState()) {
//                case TelephonyManager.CALL_STATE_RINGING:
//                    incomingFlag = true;//标识当前是来电
//                    incoming_number = intent.getStringExtra("incoming_number");
//                    Log.i(TAG1, "RINGING :"+ incoming_number);
//                    Toast.makeText(context, "来电提醒1", Toast.LENGTH_SHORT).show();
//                    break;
//                case TelephonyManager.CALL_STATE_OFFHOOK:
//                    if(incomingFlag){
//                        Log.i(TAG1, "incoming ACCEPT :"+ incoming_number);
//                    }
//                    Toast.makeText(context, "来电提醒2", Toast.LENGTH_SHORT).show();
//                    break;
//
//                case TelephonyManager.CALL_STATE_IDLE:
//                    if(incomingFlag){
//                        Log.i(TAG1, "incoming IDLE");
//                    }
//                    Toast.makeText(context, "来电提醒3", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }





    }







}