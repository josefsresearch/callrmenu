/*
 * Copyright (C) February 2014
 * Project for Tal Lavian, tlavian@gmail.com
 * @author Josef John, josefjohn88@gmail.com
 */

package info.josefjohn.callermenu;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/*
 * Requests the menu from the server on a separate (not the UI) thread
 */
public class RequestTask extends AsyncTask<String, String, String>{
	@Override
	protected String doInBackground(String... arg0) {
		try {
			if (MainActivity.phoneNumber == null) {
				Log.e("ERROR request task", "got null phone number");
			}
			String lan = "0";
			if (MainActivity.language != null && MainActivity.language.equals("Spanish")) {
				lan = "1";
			}
			Log.i("language is ", MainActivity.language);
			try {
				URL url = new URL(PrivateConstant.REQUEST_MENU+MainActivity.phoneNumber+"/"+lan+"/");
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				MainActivity.cm = readStream(con.getInputStream());
			} catch (FileNotFoundException e) {
				URL url = new URL(PrivateConstant.REQUEST_MENU+MainActivity.phoneNumber+"/0/");
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				MainActivity.cm = readStream(con.getInputStream());
			}
			MainActivity.gotMenu = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private CompanyMenu readStream(InputStream in) {
		BufferedReader reader = null;
		StringBuilder response  = new StringBuilder();
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		JSONObject json = null;
		try {
			json = new JSONObject(response.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("json is", "null");
		}
		return new CompanyMenu(json);
	} 
}
