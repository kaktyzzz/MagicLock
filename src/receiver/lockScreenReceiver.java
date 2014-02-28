package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.magiclock.MagicLock;

public class lockScreenReceiver extends BroadcastReceiver {

	public static boolean wasScreenOn = true;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Receiver", "Screen OFF");
		// если экран выключен то запускаем наш лок скрин
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			wasScreenOn = false;
			Log.d("Receiver", "Screen ON");
			Intent intent11 = new Intent(context, MagicLock.class);
			intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent11);
		}
	}
}
