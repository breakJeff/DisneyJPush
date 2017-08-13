package com.disney.jpush;

import android.app.Application;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

public class DisneyPushApplication extends Application {
    private static final String TAG = "JIGUANG-Example";

    @Override
    public void onCreate() {    	     
    	 Log.d(TAG, "[ExampleApplication] onCreate");
         super.onCreate();

         JPushInterface.setDebugMode(true);
         JPushInterface.init(this);
    }
}
