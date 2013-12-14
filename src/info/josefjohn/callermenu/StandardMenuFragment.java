package info.josefjohn.callermenu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class StandardMenuFragment extends Fragment {
	String[] values;
	List<String> curSelection = new ArrayList<String>();
	OnCallSelectedListenerThree callListener;
	String[] standardMenu;

	@Override
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_menu, container, false);

		final ListView listView = (ListView) rootView.findViewById(R.id.listView);
		final TextView tv = (TextView) rootView.findViewById(R.id.company_name);
		tv.setText(MainActivity.cm.getCompanyName());
		standardMenu = MainActivity.cm.getStandardMenu();
		values = MainActivity.cm.getStandardMenu();
		if (values == null) {
			CallerActivity.callConfirmation = true;
			callListener.onCallSelected("CALL", MainActivity.phoneNumber);
		}
		final ArrayList<String> list = new ArrayList<String>();

		for (int i = 0; i < values.length; ++i) {
			list.add(values[i]);
		}
		//final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
		final ListAdapter adapter = new ListAdapter(getActivity().getApplicationContext(), R.layout.list_row, R.id.listText, list);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				if (list.get(index) == "Back") {
					Log.i("curselectionback", curSelection.get(0));
					int a = curSelection.size()-1;
					Log.i("CurSelectionSize", String.valueOf(a));

					curSelection.remove(a);
				} else {
					if (curSelection.size() == 0) {
						curSelection.add(standardMenu[index]);
						Log.i("curselection", curSelection.get(0));
					} else if (curSelection.size() == 1 && curSelection.get(0) == "More") {
						curSelection.add(list.get(index));
					} else {
						curSelection.add(Constants.allChars[index]);
					}
				}
				loadOptions(curSelection);
			}

			private void loadOptions(List<String> cur) {
				if (cur.size() == 0) {
					values = standardMenu;
					if (values != null && values.length > 0) {
						list.clear();
						for (int i = 0; i < values.length; ++i) {
							list.add(values[i]);
						}
//						if (cur.size() > 0) {
//							list.add("Back");
//						}
						adapter.notifyDataSetChanged();
						return;
					}
				}
				String selection = MainActivity.cm.getSelection(cur);
				values = MainActivity.cm.getStandardChildren(cur);
				if (values != null && values.length > 0) {
					list.clear();
					for (int i = 0; i < values.length; ++i) {
						list.add(values[i]);
					}
					if (cur.size() > 0) {
						list.add("Back");
					}
					adapter.notifyDataSetChanged();
				} else {
					callListener.onCallSelected("CHECK", selection);
				}
			}
		});
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			callListener = (OnCallSelectedListenerThree) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnCallSelectedListener");
		}
	}

	public interface OnCallSelectedListenerThree {
		public void onCallSelected(String action, String number);
	}

	void cancelCur() {
		curSelection.remove(curSelection.size()-1);
	}

	public String getCur() {
		return MainActivity.cm.getSelection(curSelection);
	}
}
