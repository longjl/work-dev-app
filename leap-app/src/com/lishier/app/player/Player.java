package com.lishier.app.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.lishier.app.util.DateUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by longjianlin on 14-8-15.
 * V 1.0
 * *********************************
 * Desc: 视频播放器
 * *********************************
 */
public class Player implements MediaPlayer.OnPreparedListener,
        SurfaceHolder.Callback,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, View.OnClickListener {
    private Context context;
    private SurfaceView surfaceView;
    private SeekBar seekBar;
    public MediaPlayer mediaPlayer;
    private int currentPosition = 0;//当前播放位置
    private int duration = 0;//视频总时长
    private String videoUrl;//视频播放地址
    private Timer mTimer = new Timer();//时间定时器
    private TextView tv_currenttime;
    private TextView tv_duration;
    private LinearLayout ll_tools;//工具条
    private ImageButton ib_play;//播放按钮

    private Message message;
    private Animation animation;

    public Player() {
        Log.i("****************", "hello player()");
    }

    public Player(final Context context, SurfaceView surfaceView, SeekBar seekBar, String videoUrl,
                  TextView tv_time, TextView tv_duration, LinearLayout ll_tools,ImageButton ib_play) {
        this.context = context;
        this.surfaceView = surfaceView;
        this.seekBar = seekBar;
        this.videoUrl = videoUrl;

        this.tv_currenttime = tv_time;
        this.tv_duration = tv_duration;
        this.ll_tools = ll_tools;
        this.ib_play = ib_play;

        //添加回调函数
        this.surfaceView.getHolder().addCallback(this);
        this.surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.surfaceView.setOnClickListener(this);
    }


    /**
     * init player
     */
    private void initPlayer() {
        try {
           // mediaPlayer.reset();
            mediaPlayer = new MediaPlayer();
            //设置边播放变缓冲
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(videoUrl);
            // 设置显示视频的SurfaceHolder
            mediaPlayer.setDisplay(surfaceView.getHolder());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(this);//添加预加载监听器
            mediaPlayer.setOnBufferingUpdateListener(this);////添加缓冲监听
            mediaPlayer.setOnCompletionListener(this);//播放完成

            mTimer.schedule(mTimerTask, 0, 1000);
        } catch (IOException e) {
            System.out.println(e.fillInStackTrace());
            Log.e("视频处理失败", e.getMessage());
        }
    }


    /**
     * ****************************************************
     * 通过定时器和Handler来更新进度条
     * ****************************************************
     */
    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer == null)
                return;

            if (mediaPlayer.isPlaying() && seekBar.isPressed() == false) {
                message = new Message();
                message.what = 1;
                handle.sendMessage(message);
            }
        }
    };


    Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int position = mediaPlayer.getCurrentPosition();
                    duration = mediaPlayer.getDuration();

                    // 计算进度（获取进度条最大刻度*当前视频播放位置 / 当前音乐时长）
                    if (duration > 0) {
                        long pos = seekBar.getMax() * position / duration;
                        seekBar.setProgress((int) pos);
                    }
                    tv_currenttime.setText(String.valueOf(DateUtil.dateFormat.format(position)));
                    tv_duration.setText(String.valueOf(DateUtil.dateFormat.format(duration)));
                    break;
                case 2:
                    if (ll_tools.getVisibility() == View.VISIBLE) {
                        animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
                        ll_tools.setVisibility(View.GONE);
                        ll_tools.startAnimation(animation);
                    } else if (ll_tools.getVisibility() == View.GONE) {
                        animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                        ll_tools.setVisibility(View.VISIBLE);
                        ll_tools.startAnimation(animation);
                    }
                    break;
                case 3:
                    ib_play.setSelected(true);
                    break;
                default:
                    break;
            }

        }

        ;
    };
    //*****************************************************

    /**
     * 预加载 加载完成后调用该方法
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d("onPrepared", "装载完成 自动播放视频");
        mediaPlayer.start();
        //mediaPlayer.seekTo(currentPosition);//跳到指定位置播放

        //显示或隐藏工具条
        message = new Message();
        message.what = 2;
        handle.sendMessage(message);
    }


    /**
     * surface 被创建时触发该事件
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initPlayer();
    }

    /**
     * surface 大小被改变时触发该事件
     *
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * surface 销毁时触发该事件
     *
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    /**
     * 缓冲事件处理
     *
     * @param mp
     * @param percent
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    /**
     * 暂停或播放
     */
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }


    /**
     * 播放完成
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        message = new Message();
        message.what = 3;
        handle.sendMessage(message);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == surfaceView.getId()) {
            message = new Message();
            message.what = 2;
            handle.sendMessage(message);
        }
    }
}
