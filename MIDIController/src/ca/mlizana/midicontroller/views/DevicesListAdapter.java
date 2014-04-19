package ca.mlizana.midicontroller.views;

import java.util.ArrayList;

import ca.mlizana.midicontroller.MainActivity;
import ca.mlizana.midicontroller.MainActivity.LOGTAG;
import ca.mlizana.midicontroller.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class DevicesListAdapter extends ArrayAdapter<String> {
	private LayoutInflater mInflater;
	private final Activity context;
	private ArrayList<String> devices;

	public DevicesListAdapter(Activity context, ArrayList<String> d) {
		super(context, R.layout.devices_list, d);
		this.context = context;
		mInflater = LayoutInflater.from(context);
		devices = d;
	}

	public int getCount() {
		return devices.size();
	}

	public String getItem(int position) {
		return "";
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		MainActivity.log("updating row "+position, LOGTAG.INFO);
		View rowView = convertView;
	    if (rowView == null) {
	      LayoutInflater inflater = context.getLayoutInflater();
	      rowView = inflater.inflate(R.layout.devices_list, null);
	      ViewHolder viewHolder = new ViewHolder();
	      viewHolder.image = (ImageView) rowView.findViewById(R.id.deviceImage);
	      rowView.setTag(viewHolder);
	    }

	    ViewHolder holder = (ViewHolder) rowView.getTag();
	    String s = devices.get(position);
        holder.image.setImageResource(context.getResources().getIdentifier(s,"drawable",context.getPackageName()));
	
	    return rowView;
	}

	static class ViewHolder {
		ImageView image;
	}
}