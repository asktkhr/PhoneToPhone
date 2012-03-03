package com.porunga.phone2phone;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegistUser {
	private String email;

	public RegistUser() {

	}
	public RegistUser(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static ArrayList<String> parse(String data) {
		ArrayList<String> list = new ArrayList<String>();
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(data);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				list.add(json.getString("email"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}
}
