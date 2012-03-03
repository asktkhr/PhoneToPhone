package com.porunga.phone2phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class C2DMDataReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String message = intent.getStringExtra("message");
		Uri uri = Uri.parse(message);
		Intent bootActivityIntent = new Intent(Intent.ACTION_VIEW,uri);
		bootActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(bootActivityIntent);

	}

}
