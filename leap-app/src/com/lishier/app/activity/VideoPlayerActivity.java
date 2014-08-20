package com.lishier.app.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.actionbarsherlock.view.MenuItem;
import com.lishier.app.BaseActivity;
import com.lishier.app.R;
import com.lishier.app.util.DateUtil;

import java.io.IOException;

/**
 * Created by longjianlin on 14-8-15.
 * V 1.0
 * *********************************
 * Desc: 视频播放器
 * *********************************
 */
public class VideoPlayerActivity extends BaseActivity implements SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener {
    private static final String TAG = "VideoPlayerActivity";
    //private String videoUrl = "http://daily3gp.com/vids/family_guy_penis_car.3gp";//视频播放地址
    //private String videoUrl = "/sdcard/MIUI/Gallery/DemoVideo/XiaomiPhone.mp4";//视频播放地址
    //private String videoUrl = "http://video.leap.cn/20140810/86046d9b6fe0f9788112e02972457072.mp4";
    private String videoUrl = "/sdcard/DCIM/Camera/20140819_173958.mp4";//视频播放地址

    private ProgressBar pro_bar;            //加载
    private SurfaceView surfaceview;        //SurfaceView 视频画面输出
    private SeekBar seekBar;                //视频播放进度条
    private TextView tv_current_time;       //播放当前时间
    private TextView tv_duration;           //视频总时长
    private ImageButton ib_play;            //播放,暂停 按钮
    private LinearLayout ll_tools;          //视频播放器工具条
    private ImageButton ib_fullscreen;      //全屏
    private LinearLayout ll_content;        //内容容器
    private MediaPlayer mediaPlayer;        //MediaPlayer 播放器视频
    private Button btn_horsepower;          //马力

    private int currentPosition = 0;        //当前播放位置
    private int duration = 0;               //视频总时长
    private boolean isPlaying;              //是否正在播放视频
    private boolean isFullScreen = false;   //是否全屏

    public VideoPlayerActivity() {
        super(R.string.video_player);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_frame);

        pro_bar = (ProgressBar) findViewById(R.id.pro_bar);
        surfaceview = (SurfaceView) findViewById(R.id.surfaceview);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tv_current_time = (TextView) findViewById(R.id.tv_currenttime);
        tv_duration = (TextView) findViewById(R.id.tv_duration);
        ib_play = (ImageButton) findViewById(R.id.ib_play);
        ll_tools = (LinearLayout) findViewById(R.id.ll_tools);
        ib_fullscreen = (ImageButton) findViewById(R.id.ib_fullscreen);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        btn_horsepower = (Button) findViewById(R.id.btn_horsepower);

        //为SurfaceView添加回调函数
        surfaceview.getHolder().addCallback(this);

        ib_play.setOnClickListener(this);//播放,暂停按钮监听事件
        seekBar.setOnSeekBarChangeListener(this);//进度条监听事件
        ib_fullscreen.setOnClickListener(this);//全屏点击事件
        surfaceview.setOnClickListener(this);//点击事件

        getSlidingMenu().setSlidingEnabled(false);//静止滑动
    }


    /**
     * *******************视频播放器 SurfaceView Callback 回调函数 start****************************
     */

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "*************************** surfaceCreated(SurfaceHolder holder)");
        // 设置播放的视频源
        try {
            mediaPlayer = new MediaPlayer();
            //设置边播放变缓冲
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(videoUrl);
            // 设置显示视频的SurfaceHolder
            mediaPlayer.setDisplay(surfaceview.getHolder());
            mediaPlayer.prepareAsync();//开始装载
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnInfoListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "*************************** surfaceDestroyed(SurfaceHolder holder)");
        //销毁SurfaceHolder的时候记录当前的播放位置并停止播放
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
        }
    }
    /** ********************视频播放器 SurfaceView Callback 回调函数 start**************************** */


    /**
     * MediaPlayer 装载回调方法
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i(TAG, "********************** onPrepared(MediaPlayer mp) , 装载完成开始播放视频.");

        hideProgressBar();//隐藏加载组件
        showTools();//显示工具栏

        mediaPlayer.start();
        mediaPlayer.seekTo(currentPosition); //按照初始位置播放
        currentPosition = 0;

        duration = mediaPlayer.getDuration();//获取视频总时长
        tv_duration.setText(String.valueOf(DateUtil.dateFormat.format(duration)));
        seekBar.setMax(duration);// 设置进度条的最大进度为视频流的最大播放时长

        //开始线程，更新进度条的刻度
        new Thread() {
            @Override
            public void run() {
                try {
                    isPlaying = true;
                    while (isPlaying) {
                        int current = mediaPlayer.getCurrentPosition();
                        handler.sendEmptyMessage(0);
                        seekBar.setProgress(current);
                        sleep(500);
                    }
                } catch (Exception e) {
                    Log.e("SeekBar setProgress error", e.getMessage());
                }
            }
        }.start();
    }


    private Handler handler = new Handler() {
        /**
         * 更新播放时间
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            tv_current_time.setText(String.valueOf(DateUtil.dateFormat.format(mediaPlayer.getCurrentPosition())));
        }

        ;
    };


    /**
     * *******************进度条回调事件 SeekBar.OnSeekBarChangeListener start****************************
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // 当进度条停止修改的时候触发
        // 取得当前进度条的刻度
        int progress = seekBar.getProgress();
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.seekTo(progress); // 设置当前播放的位置
        }
    }

    /**
     * *******************进度条回调事件 SeekBar.OnSeekBarChangeListener end****************************
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == ib_play.getId()) {//视频播放
            if (ib_play.isSelected()) {//播放
                ib_play.setImageResource(R.drawable.ib_stop);
                ib_play.setSelected(false);
                if (isPlaying) {
                    mediaPlayer.start();
                }
            } else {//暂停
                ib_play.setImageResource(R.drawable.ib_player);
                ib_play.setSelected(true);
                if (isPlaying) {
                    mediaPlayer.pause();
                }
            }
        } else if (v.getId() == ib_fullscreen.getId()) {//全屏播放
            if (isFullScreen) {//当前处于全屏状态
                ib_fullscreen.setImageResource(R.drawable.ib_no_fullscreen);
                isFullScreen = false;
                ll_content.setVisibility(View.VISIBLE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
                getSupportActionBar().show();
                horsepowerVisibility(false);//隐藏马力
            } else {//不是处于全屏状态
                ib_fullscreen.setImageResource(R.drawable.ib_fullscreen);
                isFullScreen = true;
                ll_content.setVisibility(View.GONE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
                getSupportActionBar().hide();
                horsepowerVisibility(true);//显示马力
            }
        } else if (v.getId() == surfaceview.getId()) {
            if (ll_tools.getVisibility() == View.GONE) {//隐藏
                showTools();
            } else if (ll_tools.getVisibility() == View.VISIBLE) {//显示
                hideTools();
            }
        }
    }

    /**
     * 防止横竖屏切换的时候重新加载Activity
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //Toast.makeText(VideoPlayerActivity.this, "new Config", Toast.LENGTH_SHORT).show();
    }


    /**
     * 销毁
     * onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        if (mediaPlayer != null)
            mediaPlayer.release();
    }

    /**
     * *******************播放完成 onCompletion ****************************
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        currentPosition = 0;
    }

    /**
     * 显示ProgressBar
     */
    private void showProgressBar() {
        if (pro_bar != null) pro_bar.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏ProgressBar
     */
    private void hideProgressBar() {
        if (pro_bar != null) pro_bar.setVisibility(View.GONE);
    }


    /**
     * 显示Tools
     */
    private void showTools() {
        if (ll_tools != null) {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            ll_tools.setVisibility(View.VISIBLE);
            ll_tools.startAnimation(animation);
        }
    }

    /**
     * 隐藏Tools
     */
    private void hideTools() {
        if (ll_tools != null) {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            ll_tools.setVisibility(View.GONE);
            ll_tools.startAnimation(animation);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                isPlaying = false;
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控返回键
            if (isFullScreen) {//当前处于全屏状态
                ib_fullscreen.setImageResource(R.drawable.ib_no_fullscreen);
                isFullScreen = false;
                ll_content.setVisibility(View.VISIBLE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
                getSupportActionBar().show();
                horsepowerVisibility(false);//隐藏马力
            } else {//返回上一级菜单
                isPlaying = false;
                onBackPressed();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * *******************视频播放错误监听 onError ****************************
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.i(TAG, "*******************视频不能播放:  onError(MediaPlayer mp, int percent)");
        Toast.makeText(VideoPlayerActivity.this, "视频不能播放", Toast.LENGTH_SHORT).show();
        isPlaying = false;
        mediaPlayer.release();//释放资源
        onBackPressed();
        return false;
    }

    /**
     * 是否需要自动恢复播放，用于自动暂停，恢复播放
     * *******************视频信息监听 onInfo ****************************
     */
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START: //开始缓冲
                showProgressBar();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END: //缓冲完成
                hideProgressBar();
                break;
        }
        return true;
    }

    /**
     * 隐藏或显示马力
     * ******************* showHorsepower() ****************************
     */
    private void horsepowerVisibility(Boolean bool) {
        if (bool) {//显示马力
            btn_horsepower.setVisibility(View.VISIBLE);
        } else {//隐藏马力
            btn_horsepower.setVisibility(View.GONE);
        }
    }
}
