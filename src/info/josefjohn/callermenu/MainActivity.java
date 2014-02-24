/*
 * Copyright (C) February 2014
 * Project for Tal Lavian, tlavian@gmail.com
 * @author Josef John, josefjohn88@gmail.com
 */

package info.josefjohn.callermenu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/*
 * MainActivity starts when the app is launched.
 * User can select language in this activity.
 */
public class MainActivity extends Activity {
	private Button done;
	protected static boolean calling = false;
	protected static boolean gotMenu = false;
	protected static String phoneNumber = null;
	protected static CompanyMenu cm = null;
	protected static String language;
	protected static SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sp = getSharedPreferences("LANGUAGE", MODE_PRIVATE);
		language = sp.getString("LANGUAGE", null);
		if (language == null) {
			language = "English";
			sp.edit().putString("LANGUAGE", "English").commit();
		}
		done = (Button) findViewById(R.id.main_done);
		done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_change_language:
			String msg = "Menu language changed to ";
			Editor e = sp.edit();
			if (language.equals("English")) {
				e.putString("LANGUAGE", "Spanish").commit();
			} else {
				e.putString("LANGUAGE", "English").commit();
			}
			language = sp.getString("LANGUAGE", null);
			msg += language;
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			return true;
		}
		return false;
	}
}
