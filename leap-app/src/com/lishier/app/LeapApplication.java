package com.lishier.app;

import android.app.Application;
import android.util.Log;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

/**
 * Created by longjianlin on 14-8-20.
 * V 1.0
 * *********************************
 * Desc:
 * *********************************
 */
public class LeapApplication extends Application {
    private static final String TAG = "LeapApplication";

    @Override
    public void onCreate() {
        try {
            //处理在android所有版本中一直显示overflow效果的解决方案
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            Log.i(TAG, "onCreate() " + e.getMessage());
        }
        super.onCreate();
    }
}
