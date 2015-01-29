package com.fallen.ultra.listeners;

import com.fallen.ultra.services.UltraPlayerService;
import com.fallen.ultra.utils.Params;
import com.fallen.ultra.utils.UtilsUltra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.telephony.TelephonyManager;

public class IncomingCallReciever extends BroadcastReceiver{

//	PhoneCallCallback mPhoneCallCallback;
//	public IncomingCallReciever(PhoneCallCallback mPhoneCallCallback) {
//		// TODO Auto-generated constructor stub
//		this.mPhoneCallCallback = mPhoneCallCallback;
//	}
	Context context;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		this.context = context; 
		if (intent != null)
			checkIntent(intent);
		
		//mPhoneCallCallback.onRecievePhoneEvent(intent.getAction());
		
	}

	private void checkIntent(Intent intent) {
		// TODO Auto-generated method stub
		UtilsUltra.printLog("call recieved " + intent.getStringExtra(TelephonyManager.EXTRA_STATE));
		
		String extraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		if (extraState!=null)
			parseState(extraState);
		 
	}

	private void parseState(String extraState) {
		// TODO Auto-generated method stub
		int actionToService = -1;
		if (extraState.equals(TelephonyManager.EXTRA_STATE_RINGING))
		 {
			 actionToService = Params.ACTION_PHONE_CALL;
			 
		 }
		 else if (extraState.equals(TelephonyManager.EXTRA_STATE_IDLE))
		 {
			 actionToService = Params.ACTION_PHONE_CALL_DONE;
		 }
		 Intent serviceIntent = new Intent(context, UltraPlayerService.class);
		 UtilsUltra.printLog("actionToService " + actionToService);
		serviceIntent.putExtra(Params.ACTION_FROM_BROADCAST, actionToService);
		if (context !=null)
			context.startService(serviceIntent);
	}

}
