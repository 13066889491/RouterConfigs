package com.twowing.routeconfig.dialogview;

import java.util.HashMap;
import java.util.Map;

import snmp.routeritv.commservice.IAidlRouterCommService;
import snmp.routeritv.commservice.util.Constant;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.twowing.routeconfig.R;
import com.twowing.routeconfig.view.IpEditView;

public class EthernetStaticIpDialog extends Dialog implements
		android.view.View.OnClickListener {
	private static final String TAG = "EthernetStaticIpView";
	private Button mConfirmBtn;
	private IpEditView mIpaddrEd;
	private IpEditView mSubnetmaskEd;
	private IpEditView mGatewayEd;
	private IpEditView mDns1Ed;
	private IpEditView mDns2Ed;
	private Button mCancelBtn;
	private IAidlRouterCommService mRouterCommService;
	public static Map<String, String> valMap = new HashMap<String, String>();

	public EthernetStaticIpDialog(Context context, int stely,
			IAidlRouterCommService routerService) {
		super(context, stely);
		mRouterCommService = routerService;
	}

	public EthernetStaticIpDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ethernet_static_ip);
		initView();
		setListener();
	}

	private void setListener() {
		mCancelBtn.setOnClickListener((android.view.View.OnClickListener) this);
		mConfirmBtn
				.setOnClickListener((android.view.View.OnClickListener) this);
	}

	private void initView() {
		mCancelBtn = (Button) findViewById(R.id.btn_staticip_cancel);
		mConfirmBtn = (Button) findViewById(R.id.btn_ip_confirm);
		mIpaddrEd = (IpEditView) findViewById(R.id.iptxt_ipaddress);
		mSubnetmaskEd = (IpEditView) findViewById(R.id.iptxt_subnetmask);
		mGatewayEd = (IpEditView) findViewById(R.id.iptxt_gateway);
		mDns1Ed = (IpEditView) findViewById(R.id.iptxt_dns1);
		mDns2Ed = (IpEditView) findViewById(R.id.iptxt_dns2);
		editViews = new IpEditView[] { mIpaddrEd, mSubnetmaskEd, mGatewayEd,
				mGatewayEd, mDns1Ed, mDns2Ed };
	}

	@Override
	public void onClick(View v) {

		if (mRouterCommService == null) {
			Log.e(TAG, "onClick :: mRouterCommService=" + mRouterCommService);
			Toast.makeText(getContext(), "服务器异常，请检查设备。", 0).show();
			dismiss();
			return;
		}
		switch (v.getId()) {
		case R.id.btn_ip_confirm:
			valMap.clear();
			boolean isPass = true;
			final String mDns1 = mDns1Ed.getFormattedIp();
			final String mDns2 = mDns2Ed.getFormattedIp();
			final String mIpaddr = mIpaddrEd.getFormattedIp();
			final String mSubnetmask = mSubnetmaskEd.getFormattedIp();
			final String mGateway = mGatewayEd.getFormattedIp();

			for (int i = 0; i < editViews.length; i++) {
				String editMattedIp = editViews[i].getFormattedIp();
				Log.e(TAG, "getFormattedIp :: editMattedIp=" + editMattedIp);
				String[] mattedIp = editMattedIp.split("\\.");
				Log.e(TAG, "getFormattedIp :: mattedIp=" + mattedIp
						+ ",mattedIp.length=" + mattedIp.length);
				for (int j = 0; j < mattedIp.length; j++) {
					String ip = mattedIp[j];
					Log.e(TAG, "getFormattedIp :: ip=" + ip);
					if (Integer.valueOf(ip) > 255||Integer.valueOf(ip)<0) {
						isPass = false;
						break;
					}
				}
			}
			Log.e(TAG, "getFormattedIp :: isPass=" + isPass);
			if (!isPass) {
				Toast.makeText(getContext(), "请输入0-255之间的整数",
						Toast.LENGTH_SHORT).show();
				return;
			}
			isPass = true;
			Log.e(TAG, "getFormattedIp :: isPass =" + isPass);
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (mRouterCommService != null) {
						int wanIndex = 1; // wanIndex 只第几个wan口 ， 默认设置1,
											// 1指internet上网方式
						Map map = new HashMap();
						map.put(Constant.key.wan_PRIMARYDNS, mDns1);
						map.put(Constant.key.wan_SECONDARYDNS, mDns2);
						map.put(Constant.key.wan_PORTBIND, 1);
						map.put(Constant.key.wan_IPADDRESS, mIpaddr);
						map.put(Constant.key.wan_SUBNETMASK, mSubnetmask);
						map.put(Constant.key.wan_GATEWAY, mGateway);
						try {
							mRouterCommService
									.setWanStaticIpInfo(wanIndex, map);
						} catch (RemoteException e) {
							e.printStackTrace();
							Log.d(TAG,
									"send wan static internet info to service");
						}
					}
				}
			}).start();
			if (mStaticStateListener != null) {
				mStaticStateListener.showStaticStatus();
			}
			break;
		default:
			break;
		}
		dismiss();
	}

	public StaticStateListener mStaticStateListener;
	private IpEditView[] editViews;

	public interface StaticStateListener {
		void showStaticStatus();
	}

	public void connectStaticState(StaticStateListener listener) {
		if (listener != null) {
			mStaticStateListener = listener;
		}
	}
}