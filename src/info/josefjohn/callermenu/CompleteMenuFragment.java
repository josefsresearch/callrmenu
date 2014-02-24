/*
 * Copyright (C) February 2014
 * Project for Tal Lavian, tlavian@gmail.com
 * @author Josef John, josefjohn88@gmail.com
 */

package info.josefjohn.callermenu;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

public class CompleteMenuFragment extends Fragment {
	String[] values;
	OnCallSelectedListenerTwo callListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
		final TextView tv = (TextView) rootView.findViewById(R.id.company_name);
		tv.setText(MainActivity.cm.getCompanyName());
		final ListView listView = (ListView) rootView.findViewById(R.id.listView);

		values = MainActivity.cm.getAllOptions();
		final ArrayList<String> list = new ArrayList<String>();

		for (int i = 0; i < values.length; ++i) {
			list.add(values[i]);
		}
		final ListAdapter adapter = new ListAdapter(getActivity().getApplicationContext(), R.layout.list_row, R.id.listText, list);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
					String selection = MainActivity.cm.getCompleteMapping(index);
					Log.i("index="+index, "selection="+selection);
					Log.i("selection is", selection);
					callListener.onCallSelected("CHECK", selection);
			}
		});
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			callListener = (OnCallSelectedListenerTwo) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
		}
	}

	public interface OnCallSelectedListenerTwo {
		public void onCallSelected(String action, String number);
	}
}
