package com.porunga.phone2phone;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class C2DMRegisterReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String registrationId = intent.getStringExtra("registration_id");
		String error = intent.getStringExtra("error");

		if (registrationId != null) {
			// registration_idをアプリケーションサーバに送信する。
			SharedPreferences pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("registration_id", registrationId);
			// データの保存
			editor.commit();
			this.sendRegistrationId(context, registrationId);
		} else if (error != null) {
			Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
			Log.d("Phone2Phone", error);
		}
	}

	private void sendRegistrationId(Context context, String registrationId) {
		String name = Build.MODEL;
		String api = "http://localhost/message/register";
		// String api = "http://192.168.11.105:3000/c2dm/save_device";
		HttpResponse objResponse = null;

		HttpClient objHttp = new DefaultHttpClient();
		HttpPost objPost = new HttpPost(api);

		HttpEntity entity = null;
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("registration_id", registrationId));
		params.add(new BasicNameValuePair("name", name));
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
		} finally {
			if(objResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED){
				Toast.makeText(context, "registered", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(context, "failer", Toast.LENGTH_LONG).show();
			}
		}
	}
}