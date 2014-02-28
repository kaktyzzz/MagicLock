package com.magiclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class StartLockScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Log.d("StartLock", "Start");
		// запускаем сервис
		startService(new Intent(this, LockService.class));
		// убиваем активность
		finish();
	}
}
