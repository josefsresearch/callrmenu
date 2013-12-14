package info.josefjohn.callermenu;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class OutgoingCallHandler extends BroadcastReceiver {
	private ConnectivityManager connectivityManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!MainActivity.calling) {
			connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			String phoneNumber = getResultData();
			if (phoneNumber == null) {
				// No reformatted number, use the original
				phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			}
			MainActivity.cm = null;
			MainActivity.phoneNumber = phoneNumber;
			//if not connected b4 call, our app doesn't run
			if (connectedToInternet() && phoneNumber != null && HelperMethods.hasNumber(phoneNumber)) {
				getCallerMenu(phoneNumber);
				if (MainActivity.cm == null) {
					Log.e("company menu", "returned null, no num"+phoneNumber);
				} else {
					// My app will bring up the call, so cancel the call broadcast
					setResultData(null);
					Intent i = new Intent(context, CallerActivity.class);
					i.putExtra("PHONE_NUMBER", phoneNumber);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				}
			} else {
				Log.i("no internet or no number", "continue normal call");
			}
		} else {
			Log.i("Our app", "Making our call");
		}
	}

	private void getCallerMenu(String phoneNumber) {
		new RequestTask().execute();
		int i = 0;
		while (!MainActivity.gotMenu) {
		}
		MainActivity.gotMenu = false;
		return;
	}

	private boolean connectedToInternet() {
		if (connectivityManager.getActiveNetworkInfo() != null &&
				connectivityManager.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}
}
