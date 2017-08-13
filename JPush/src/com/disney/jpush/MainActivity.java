package com.disney.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import  com.disney.push.R;

public class MainActivity extends InstrumentedActivity implements OnClickListener{
    public static boolean isForeground = false;

	private Button initBtn;
	private Button settingBtn;
	private Button stopBtn;
	private Button resumeBtn;
	private Button showIdBtn;
	private TextView registerIdView;
	private EditText msgText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initView();   
		registerMessageReceiver();  // used for receive msg
	}
	
	private void initView(){
		TextView imeiView = (TextView) findViewById(R.id.tv_imei);
		String udid =  PushUtil.getImei(getApplicationContext(), "");
        if (null != udid) imeiView.setText("IMEI: " + udid);
        
		TextView appKeyView = (TextView) findViewById(R.id.tv_appkey);
		String appKey = PushUtil.getAppKey(getApplicationContext());
		if (null == appKey) appKey = "AppKey Exception";
		appKeyView.setText("AppKey: " + appKey);

		registerIdView = (TextView) findViewById(R.id.tv_regId);
		registerIdView.setText("RegId:");

		String packageName =  getPackageName();
		TextView mPackage = (TextView) findViewById(R.id.tv_package);
		mPackage.setText("PackageName: " + packageName);

		String deviceId = PushUtil.getDeviceId(getApplicationContext());
		TextView mDeviceId = (TextView) findViewById(R.id.tv_device_id);
		mDeviceId.setText("deviceId:" + deviceId);
		
		String versionName =  PushUtil.getVersion(getApplicationContext());
		TextView mVersion = (TextView) findViewById(R.id.tv_version);
		mVersion.setText("Version: " + versionName);
		
	    initBtn = (Button)findViewById(R.id.init);
		initBtn.setOnClickListener(this);
		stopBtn = (Button)findViewById(R.id.stopPush);
		stopBtn.setOnClickListener(this);
		resumeBtn = (Button)findViewById(R.id.resumePush);
		resumeBtn.setOnClickListener(this);
		showIdBtn = (Button) findViewById(R.id.getRegistrationId);
		showIdBtn.setOnClickListener(this);
		settingBtn = (Button)findViewById(R.id.setting);
		settingBtn.setOnClickListener(this);
		msgText = (EditText)findViewById(R.id.msg_rec);
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.init:
			init();
			break;
		case R.id.setting:
			Intent intent = new Intent(MainActivity.this, PushSetActivity.class);
			startActivity(intent);
			break;
		case R.id.stopPush:
			JPushInterface.stopPush(getApplicationContext());
			break;
		case R.id.resumePush:
			JPushInterface.resumePush(getApplicationContext());
			break;
		case R.id.getRegistrationId:
			String rid = JPushInterface.getRegistrationID(getApplicationContext());
			if (!rid.isEmpty()) {
				registerIdView.setText("RegId:" + rid);
			} else {
				Toast.makeText(this, "Get registration fail, JPush init failed!", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	
	//JPush initialization。if initialization is done, and login fail, re-login
	private void init(){
		 JPushInterface.init(getApplicationContext());
	}


	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();
	}


	@Override
	protected void onPause() {
		isForeground = false;
		super.onPause();
	}


	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}
	

	//for receive customer msg from jpush server
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	
	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
					String message = intent.getStringExtra(KEY_MESSAGE);
					String extras = intent.getStringExtra(KEY_EXTRAS);
					StringBuilder showMsg = new StringBuilder();
					showMsg.append(KEY_MESSAGE + " : " + message + "\n");
					if (!PushUtil.isEmpty(extras)) {
						showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
					}
					setCustomMsg(showMsg.toString());
				}
			} catch (Exception e){
			}
		}
	}
	
	private void setCustomMsg(String msg){
		 if (null != msgText) {
			 msgText.setText(msg);
			 msgText.setVisibility(android.view.View.VISIBLE);
         }
	}

}