package com.magiclock;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import receiver.lockScreenReceiver;

public class LockService extends Service {

	BroadcastReceiver mReceiver;

	@Override
	public IBinder onBind(Intent arg0) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {

		KeyguardManager.KeyguardLock k1;
		KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		k1 = km.newKeyguardLock("IN");
		k1.disableKeyguard();
		// проверяем в фоне все время работы экрана
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		// фильтруем на появление флага выключения экрана
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		// если это так то запускаем рессивер
		mReceiver = new lockScreenReceiver();
		registerReceiver(mReceiver, filter);
		Log.d("service", "Start");
		super.onCreate();
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    Log.d("service", "onStartCommand");
	    return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {

		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
}
