package info.josefjohn.callermenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter {
	private Context context;
	private LayoutInflater inflater;
	private int resource;
	private int textId;
	private int imgId;
	private List<String> data;

	public ListAdapter(Context context, int resource, int textId, ArrayList<String> objects) {
		super(context, resource, textId, objects);
		this.context = context;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		data = objects;
		this.resource = resource;
		this.textId = textId;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, resource);
	}

	private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
		View view;
		TextView text;
		

		if (convertView == null) {
			view = inflater.inflate(resource, parent, false);
		} else {
			view = convertView;
		}		
		try {
			if (textId == 0) {
				//  If no custom text is assigned, assume the whole resource is a TextView
				text = (TextView) view;
			} else {
				//  Otherwise, find the TextView text within the layout
				text = (TextView) view.findViewById(textId);
			}
		} catch (ClassCastException e) {
			Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
			throw new IllegalStateException(
					"ArrayAdapter requires the resource ID to be a TextView", e);
		}

		Object item = getItem(position);
		String option = item.toString();

		if (item instanceof CharSequence) {
			text.setText((CharSequence)item);
		} else {
			text.setText(item.toString());
		}
		int img = HelperMethods.getImage(option);
		if (img == 0) {
			text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		} else {
			text.setCompoundDrawablesWithIntrinsicBounds(0, 0, img, 0);
		}
		return view;
	}
}
