package com.porunga.phone2phone;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import com.porunga.phone2phone.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
			ArrayList<String> list = getRegistUser();
			displayDialog(list);
		} else {
			setContentView(R.layout.main);
			SharedPreferences pref = this.getSharedPreferences("pref", Activity.MODE_PRIVATE);
			String regId = pref.getString("regId", "");

			if (regId != "") {
				Button registerButton = (Button) findViewById(R.id.registerButton);
				registerButton.setEnabled(false);
			}
		}
	}

	private void displayDialog(ArrayList<String> list) {

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("User");
		final String[] items = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			items[i] = list.get(i);
		}
		dialog.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				pushMessage(items[which]);
			}
		});
		dialog.show();

	}

	private ArrayList<String> getRegistUser() {
		ArrayList<String> list = new ArrayList<String>();
		Account[] accounts = AccountManager.get(this).getAccounts();
		String email = "";
		for(int i =0;i< accounts.length;i++){
			if(accounts[i].type.equals("com.google")) {
				email = accounts[i].name;
				break;
			}
		}
		String api = "http://***/c2dm/devices?format=json";
		HttpResponse objResponse = null;

		HttpClient objHttp = new DefaultHttpClient();
		api += "&";
		api += "sender_device=" + email+"-"+Build.MODEL;
		HttpGet objGet = new HttpGet(api);

		try {
			objResponse = objHttp.execute(objGet);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (objResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
			String result;
			try {
				result = EntityUtils.toString(objResponse.getEntity(), "UTF-8");
				list = RegistUser.parse(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	private void pushMessage(String email) {

		String uri = getIntent().getExtras().getCharSequence(Intent.EXTRA_TEXT).toString();
		String api = "http://***/c2dm/send_message";
		HttpResponse objResponse = null;

		HttpClient objHttp = new DefaultHttpClient();
		HttpPost objPost = new HttpPost(api);

		HttpEntity entity = null;
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uri", uri));
		params.add(new BasicNameValuePair("email", email));
		try {
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		objPost.setEntity(entity);

		try {
			objResponse = objHttp.execute(objPost);
		} catch (Exception e) {
			e.printStackTrace();
		}

		finish();
	}

	public void registerDevice(View v) {
		Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
		intent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
		intent.putExtra("sender", "***@gmail.com");
		startService(intent);
	}

	public void unregisterDevice(View v) {
		Intent intent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
		intent.putExtra(getApplicationInfo().packageName, PendingIntent.getBroadcast(this, 0, new Intent(), 0));
		intent.putExtra("sender", "***@gmail.com");
		startService(intent);
	}
}
