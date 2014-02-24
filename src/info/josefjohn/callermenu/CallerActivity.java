/*
 * Copyright (C) February 2014
 * Project for Tal Lavian, tlavian@gmail.com
 * @author Josef John, josefjohn88@gmail.com
 */

package info.josefjohn.callermenu;

import java.util.HashMap;
import java.util.List;

import info.josefjohn.callermenu.CompleteMenuFragment.OnCallSelectedListenerTwo;
import info.josefjohn.callermenu.LeveledMenuFragment.OnCallSelectedListener;
import info.josefjohn.callermenu.StandardMenuFragment.OnCallSelectedListenerThree;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView.FindListener;
import android.widget.EditText;

public class CallerActivity extends FragmentActivity implements 
ActionBar.TabListener, OnCallSelectedListener, OnCallSelectedListenerTwo,
OnCallSelectedListenerThree {

	private ViewPager viewPager;
	private TabsPagerAdapter tabAdapter;
	private ActionBar actionBar;
	private String[] tabs = {"Tree", "Flat", "Standard"};
	protected static HashMap<String, String> questions;
	protected static String selection;
	private DialogFragment dialog;
	private String title;
	protected static boolean callConfirmation = false;
	private boolean forceCall = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_caller);

		questions = new HashMap<String, String>();
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		tabAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(tabAdapter);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		for (String tabName : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tabName)
					.setTabListener(this));
		}
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int i) {
				actionBar.setSelectedNavigationItem(i);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}

			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {}

	@Override
	public void onCallSelected(String action, String number) {
		Log.i(action, number);
		if (action == "CHECK") {
			String isLink = MainActivity.cm.isLink(number);
			String hasQuestion = MainActivity.cm.hasQuestion(number);
			if (isLink != null) {
				onCallSelected("POPUP_WEBSITE", isLink);
			} else if (hasQuestion != null) {
				selection = number;
				onCallSelected("POPUP_QUESTION", hasQuestion);
			} else {
				onCallSelected("POPUP_CONFIRM", number);
			}
		} else if (action == "CALL") {
			if (callConfirmation) {
				callConfirmation = false;
				Log.i("CALLER_ACTIVITY", "CALLING "+number);
				MainActivity.calling = true;//do i need to false onPause?
				PhoneCallListener phoneListener = new PhoneCallListener();
				TelephonyManager telephonyManager = (TelephonyManager) this
						.getSystemService(Context.TELEPHONY_SERVICE);
				telephonyManager.listen(phoneListener,
						PhoneStateListener.LISTEN_CALL_STATE);
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:"+number));
				startActivity(callIntent);
			} else {
				showDialog("POPUP_CONFIRM", number);
			}
		} else if (action == "POPUP_QUESTION") {
			title = number;
			showDialog(action, number);
		} else if (action == "POPUP_CONFIRM") {
			showDialog(action, number);
		} else if (action == "POPUP_WEBSITE") {
			showDialog(action, number);
		}
	}

	private class PhoneCallListener extends PhoneStateListener {
		private boolean isPhoneCalling = false;
		String LOG_TAG = "CALL_LISTENER";

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			if (TelephonyManager.CALL_STATE_RINGING == state) {
				Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
			}

			if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
				Log.i(LOG_TAG, "OFFHOOK");
				isPhoneCalling = true;
			}

			if (TelephonyManager.CALL_STATE_IDLE == state) {
				// run when class initial and phone call ended, need detect flag
				// from CALL_STATE_OFFHOOK
				Log.i(LOG_TAG, "IDLE");
				if (isPhoneCalling) {
					MainActivity.calling = false;
					MainActivity.cm = null;
					MainActivity.phoneNumber = null;
					isPhoneCalling = false;
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_caller, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_call:
			forceCall();
			return true;
		}
		return false;
	}

	private void forceCall() {
		int tab = viewPager.getCurrentItem();
		List<Fragment> lf = getSupportFragmentManager().getFragments();
		if (tab == 0) {
			LeveledMenuFragment l = (LeveledMenuFragment) lf.get(0);
			selection = l.getCur();
			forceCall = true;
			onCallSelected("POPUP_CONFIRM", selection);
		} else if (tab == 1) {
			CompleteMenuFragment f = (CompleteMenuFragment) lf.get(1);
			onCallSelected("POPUP_CONFIRM", "");
		} else {
			StandardMenuFragment f = (StandardMenuFragment) lf.get(2);
			selection = f.getCur();
			forceCall = true;
			onCallSelected("POPUP_CONFIRM", selection);
		}
	}

	public static String getInput(String string) {
		if (!questions.containsKey(string)) {
			return null;
		}
		return questions.get(string);
	}

	public static void putInput(String key, String value) {
		questions.put(key, value);
	}

	void showDialog(String type, String title) {
		dialog = InputDialogFragment.newInstance(type, title);
		dialog.show(getFragmentManager(), "dialog");
	}

	void doPositiveClick() {
		Bundle args = dialog.getArguments();
		forceCall = false;
		if (args.getString("type") == "POPUP_QUESTION") {
			EditText e = (EditText) dialog.getDialog().findViewById(R.id.dialog_input_answer);
			String resp = e.getText().toString();
			if (resp.length() < 1) {
				if (questions.containsKey(title)) {
					questions.remove(title);
				}
			} else {
				questions.put(title, resp);
			}
			dialog.getDialog().cancel();
			Log.i("QUestion selection", selection);
			onCallSelected("CALL", selection);
		} else if (args.getString("type") == "POPUP_WEBSITE") {
			Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(args.getString("title")));
			dialog.getDialog().cancel();
			startActivity(myIntent);
		} else {
			callConfirmation = true;
			dialog.getDialog().cancel();
			onCallSelected("CALL", MainActivity.cm.getFinalSelection(args.getString("title")));
		}
	}

	void doNegativeClick() {
		Bundle args = dialog.getArguments();
		if (args.getString("type") == "POPUP_QUESTION") {
			if (questions.containsKey(args.get("title"))) {
				questions.remove(args.get("title"));
			}
			selection = null;
		}
		if (!forceCall) {
			forceCall = false;
			String tag;
			int tab = viewPager.getCurrentItem();
			List<Fragment> lf = getSupportFragmentManager().getFragments();
			if (tab == 0) {
				LeveledMenuFragment l = (LeveledMenuFragment) lf.get(0);
				l.cancelCur();
			} else if (tab == 1) {
				CompleteMenuFragment f = (CompleteMenuFragment) lf.get(1);
			} else {
				StandardMenuFragment f = (StandardMenuFragment) lf.get(2);
				f.cancelCur();
			}
		}
		dialog.getDialog().cancel();
	}

	public static class InputDialogFragment extends DialogFragment {

		public static InputDialogFragment newInstance(String type, String title) {
			InputDialogFragment frag = new InputDialogFragment();
			Bundle args = new Bundle();
			args.putString("title", title);
			args.putString("type", type);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			String title = getArguments().getString("title");
			String type = getArguments().getString("type");

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();
			if (type == "POPUP_QUESTION") {
				builder.setView(inflater.inflate(R.layout.dialog_input, null));
				builder.setTitle("Please input");
				builder.setMessage(title);
			} else if (type == "POPUP_WEBSITE") {
				builder.setTitle("Visit now?");
				builder.setMessage(title);
			} else {
				builder.setTitle("Please confirm");
				builder.setMessage("Call now?");
			}

			// Add action buttons
			builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					((CallerActivity) getActivity()).doPositiveClick();
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					((CallerActivity) getActivity()).doNegativeClick();
				}
			});     

			return builder.create();
		}
	}

}
