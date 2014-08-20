package com.lishier.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lishier.app.fragments.SampleListFragment;
import com.mcxiaoke.popupmenu.PopupMenuCompat;


public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    public MainActivity() {
        super(R.string.content);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SampleListFragment()).commit();

        //open listener
        getSlidingMenu().setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        });

        //closed listener
        getSlidingMenu().setOnClosedListener(new SlidingMenu.OnClosedListener() {
            @Override
            public void onClosed() {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

        IntentFilter intentFilter = new IntentFilter("android.intent.action.MyBroadCast");
        registerReceiver(broadcastReceiver, intentFilter);


    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this, "onReceive", Toast.LENGTH_SHORT).show();
        }
    };

    //销毁
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }


    /**
     * show content
     */
    public void switchContent(final Fragment fragment) {
        if (fragment == null) {
            toggle();
            return;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSlidingMenu().showContent();
            }
        }, 50);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.more) {
            View view = findViewById(id);
            showPopupMenu(view);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showPopupMenu(View view) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "showPopupMenu()");
        }
        final PopupMenuCompat.OnMenuItemClickListener onMenuItemClickListener =
                new PopupMenuCompat.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        Toast.makeText(MainActivity.this,TAG,Toast.LENGTH_SHORT).show();
                        return false;
                    }
                };
//        final PopupMenuCompat.OnDismissListener onDismissListener =
//                new PopupMenuCompat.OnDismissListener() {
//                    @Override
//                    public void onDismiss(PopupMenuCompat PopupMenu) {
//
//                    }
//                };
        PopupMenuCompat popupMenu = new PopupMenuCompat(this, view);
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);
//        popupMenu.setOnDismissListener(onDismissListener);
        popupMenu.inflate(R.menu.menu);
        popupMenu.show();
    }

}
