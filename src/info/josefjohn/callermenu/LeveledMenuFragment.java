package info.josefjohn.callermenu;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class LeveledMenuFragment extends Fragment {
	String curOption;
	String[] values;
	OnCallSelectedListener callListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
		final ListView listView = (ListView) rootView.findViewById(R.id.listView);
		final TextView tv = (TextView) rootView.findViewById(R.id.company_name);
		tv.setText(MainActivity.cm.getCompanyName());
		curOption = "";
		values = MainActivity.cm.getChildren(curOption);
		if (values == null) {
			callListener.onCallSelected("CALL", MainActivity.phoneNumber);
		}
		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0;i<values.length; ++i) {
			list.add(values[i]);
		}
		final ListAdapter adapter = new ListAdapter(getActivity().getApplicationContext(), R.layout.list_row, R.id.listText, list);
		//final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_row, R.id.listText, list);
		//final ListAdapter adapter = new ListAdapter(getActivity(), R.layout.list_row, R.id.listText, R.id.listPic, list);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				Log.i("selected", list.get(index));
				if (list.get(index) == "Back") {
					curOption = curOption.substring(0, curOption.length()-1);
					loadOptions(curOption);
				} else {
					curOption += Constants.allChars[index];
					loadOptions(curOption);
				}
			}

			private void loadOptions(String option) {
				int numChildren = MainActivity.cm.getNumChildren(option);
				if (numChildren > 0) {
					values = MainActivity.cm.getChildren(option);
					list.clear();
					for (int i = 0; i < values.length; ++i) {
						list.add(values[i]);
					}
					if (option.length() > 0) {
						list.add("Back");
					}
					adapter.notifyDataSetChanged();
				} else {
					callListener.onCallSelected("CHECK", option);
				}
			}
		});

		return rootView;
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			callListener = (OnCallSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnCallSelectedListener");
		}
	}

	public interface OnCallSelectedListener {
		public void onCallSelected(String action, String number);
	}

	public void cancelCur() {
		Log.i("b4", curOption);
		curOption = curOption.substring(0, curOption.length()-1);
		Log.i("after", curOption);

	}
	
	public String getCur() {
		return curOption;
	}
}
