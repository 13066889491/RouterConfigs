package com.twowing.routeconfig.wifi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.twowing.routeconfig.R;
import com.twowing.routeconfig.view.MarqueeView;
import com.twowing.routeconfig.wifi.entity.WifiListInfoBean;

public class WifiAInfodapter extends ArrayAdapter<WifiListInfoBean> {

	private LayoutInflater mInflater;
	private Context mContext;

	public WifiAInfodapter(Context context) {
		super(context, 0);
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		WifiListInfoBean info = getItem(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.wifi_list_item_view,
					parent, false);
			viewHolder.listNumber = (TextView) convertView
					.findViewById(R.id.wifi_list_number);

			viewHolder.deviceNameTitle = (TextView) convertView
					.findViewById(R.id.wifi_device_name_title);
			viewHolder.deviceName = (MarqueeView) convertView
					.findViewById(R.id.wifi_device_name);
			viewHolder.macAddress = (TextView) convertView
					.findViewById(R.id.wifi_mac_address);
			viewHolder.ipAddress = (TextView) convertView
					.findViewById(R.id.wifi_ip_address);
			Log.e("WifiAInfodapter", "getView :: info=" + info);
			viewHolder.deviceNameTitle.setVisibility(View.GONE);
			viewHolder.deviceName.setVisibility(View.VISIBLE);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.listNumber.setText(info.getNumber());
		viewHolder.deviceName.setText(info.getDeviceName());
		viewHolder.macAddress.setText(info.getMacAddress());
		viewHolder.ipAddress.setText(info.getIpAddress());
		return convertView;
	}

	private static class ViewHolder {
		TextView listNumber;
		TextView deviceNameTitle;
		MarqueeView deviceName;
		TextView macAddress;
		TextView ipAddress;
	}
}
