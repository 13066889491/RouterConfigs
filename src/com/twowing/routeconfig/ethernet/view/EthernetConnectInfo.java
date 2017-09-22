package com.twowing.routeconfig.ethernet.view;

import java.lang.ref.WeakReference;

import snmp.routeritv.commservice.IAidlRouterCommService;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twowing.routeconfig.R;
import com.twowing.routeconfig.callback.RouterCommCallback;
import com.twowing.routeconfig.callback.RouterCommCallback.RouterInternetInfoListener;
import com.twowing.routeconfig.utils.SharePrefsUtils;

public class EthernetConnectInfo extends LinearLayout {
	private static final String TAG = "EthernetConnectInfo";
	private TextView mConnectTypeTv;
	private TextView mConnectIpTv;
	private TextView mSubnetMaskTv;
	private TextView mDefaultGatewayTv;
	private TextView mPrimaryDnsTv;
	private TextView mSecondDnsTv;
	private Context mContext;
	private String mIpAddress;
	private String mSubnetMask;
	private String mDefaultGateway;
	private String mPrimaryDns;
	private String mSecondDns;
	private IAidlRouterCommService mRouterCommService;
	private Handler mHandler = new MyHandler(this);
	private static final int MSG_UPDATA_INTERNET_INFO = 0x1000;

	private RouterCommCallback mRouterCommCallback;

	private static class MyHandler extends Handler {
		private WeakReference<EthernetConnectInfo> mMain;

		public MyHandler(EthernetConnectInfo view) {
			mMain = new WeakReference<EthernetConnectInfo>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			EthernetConnectInfo view = mMain.get();
			if (view == null) {
				return;
			}
			switch (msg.what) {
			case MSG_UPDATA_INTERNET_INFO:
				view.updatEthernetInfo();
				break;
			default:
				break;
			}
		}
	}

	private void updatEthernetInfo() {
		initEthernetInfo(mIpAddress, mSubnetMask, mDefaultGateway, mPrimaryDns,
				mSecondDns);
		mHandler.removeMessages(MSG_UPDATA_INTERNET_INFO);
		mHandler.sendEmptyMessageDelayed(MSG_UPDATA_INTERNET_INFO, 5000);
	}

	public void getRouterServiceListener(IAidlRouterCommService l) {
		this.mRouterCommService = l;
		Log.e(TAG, "setRouterServiceListener  :: mRouterCommService="
				+ mRouterCommService);
	}

	public void getRouterCommCallback(RouterCommCallback routerCommCallback) {
		this.mRouterCommCallback = routerCommCallback;
		Log.e(TAG, "getRouterCommCallback  :: routerCommCallback="
				+ routerCommCallback);
		mRouterCommCallback.setRouterInternetListener(mRouterInfoListener);
	}

	private RouterInternetInfoListener mRouterInfoListener = new RouterInternetInfoListener() {

		@Override
		public void getRouterIpAddress(String ipAddress) {
			if (TextUtils.isEmpty(ipAddress)) {
				ipAddress = "";
			}
			mIpAddress = ipAddress;
		}

		@Override
		public void getRouterSubnetMask(String subnetMask) {
			Log.e(TAG, "mRouterInfoListener :: subnetMask=" + subnetMask);
			if (TextUtils.isEmpty(subnetMask)) {
				subnetMask = "";
			}
			mSubnetMask = subnetMask;
		}

		@Override
		public void getRouterDefaultGateway(String defaultGateway) {
			Log.e(TAG, "mRouterInfoListener :: defaultGateway="
					+ defaultGateway);
			if (TextUtils.isEmpty(defaultGateway)) {
				defaultGateway = "";
			}
			mDefaultGateway = defaultGateway;
		}

		@Override
		public void getRouterPrimaryDns(String primaryDns) {
			Log.e(TAG, "mRouterInfoListener :: primaryDns=" + primaryDns);
			if (TextUtils.isEmpty(primaryDns)) {
				primaryDns = "";
			}
			mPrimaryDns = primaryDns;
		}

		@Override
		public void getRouterSecondDns(String secondDns) {
			Log.e(TAG, "mRouterInfoListener :: secondDns=" + secondDns);
			if (TextUtils.isEmpty(secondDns)) {
				secondDns = "";
			}
			mSecondDns = secondDns;
		}
	};

	public EthernetConnectInfo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public EthernetConnectInfo(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EthernetConnectInfo(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mConnectTypeTv = (TextView) findViewById(R.id.ethernet_connect_type);
		mConnectIpTv = (TextView) findViewById(R.id.ethernet_connect_ip);
		mSubnetMaskTv = (TextView) findViewById(R.id.ethernet_subnet_mask);
		mDefaultGatewayTv = (TextView) findViewById(R.id.ethernet_default_gateway);
		mPrimaryDnsTv = (TextView) findViewById(R.id.ethernet_primary_dns);
		mSecondDnsTv = (TextView) findViewById(R.id.ethernet_second_dns);
		if (mConnectTypeTv == null || mConnectIpTv == null
				|| mSubnetMaskTv == null || mDefaultGatewayTv == null
				|| mPrimaryDnsTv == null || mSecondDnsTv == null) {
			throw new InflateException("Miss a child?");
		}
		mContext = getContext();
		String netWorkMode = SharePrefsUtils.getRouterInternetMode(mContext);
		if (!TextUtils.isEmpty(netWorkMode)) {
			mNetWorkMode = netWorkMode;
		}
		initEthernetInfo(null, null, null, null, null);
		initData();
	}

	private String mNetWorkMode = "PPPOE";

	private void initEthernetInfo(String ipAddress, String smubnetMask,
			String defaultGateway, String primaryDns, String secondDns) {
		String netWorkMode = SharePrefsUtils.getRouterInternetMode(mContext);
		Log.e(TAG, "initEthernetInfo:: netWorkMode=" + netWorkMode);
		if (!TextUtils.isEmpty(netWorkMode) && mNetWorkMode != netWorkMode) {
			mNetWorkMode = netWorkMode;
			mIpAddress = null;
			mSubnetMask = null;
			mDefaultGateway = null;
			mPrimaryDns = null;
			mSecondDns = null;
		}
		if (TextUtils.isEmpty(netWorkMode)) {
			mConnectTypeTv.setText("Internet 连接方式：" + "PPPOE");
		} else {
			mConnectTypeTv.setText("Internet 连接方式：" + netWorkMode);
		}
		if (TextUtils.isEmpty(ipAddress)) {
			mConnectIpTv.setText("IP 地址： " + "0.0.0.0");
		} else {
			mConnectIpTv.setText("IP 地址： " + ipAddress);
		}
		if (TextUtils.isEmpty(smubnetMask)) {
			mSubnetMaskTv.setText("子网掩码： " + "0.0.0.0");
		} else {
			mSubnetMaskTv.setText("子网掩码： " + smubnetMask);
		}
		if (TextUtils.isEmpty(defaultGateway)) {
			mDefaultGatewayTv.setText("默认网关：  " + "0.0.0.0");
		} else {
			mDefaultGatewayTv.setText("默认网关：  " + defaultGateway);
		}
		if (TextUtils.isEmpty(primaryDns)) {
			mPrimaryDnsTv.setText("主 DNS： " + "0.0.0.0");
		} else {
			mPrimaryDnsTv.setText("主 DNS： " + primaryDns);
		}
		if (TextUtils.isEmpty(secondDns)) {
			mSecondDnsTv.setText("从 DNS：  " + "0.0.0.0");
		} else {
			mSecondDnsTv.setText("从 DNS：  " + secondDns);
		}
	}

	
	private void initData() {
		mHandler.sendEmptyMessageDelayed(MSG_UPDATA_INTERNET_INFO, 5000);
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		Log.e(TAG, "onVisibilityChanged :: visibility=" + visibility);
		mHandler.removeMessages(MSG_UPDATA_INTERNET_INFO);
		if (visibility == View.VISIBLE) {
			mHandler.sendEmptyMessageDelayed(MSG_UPDATA_INTERNET_INFO, 5000);
		}
	}
}