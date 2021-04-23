package com.puerlink.common;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;

/**
 * Created by wangxm on 2017/4/19.
 */

public class VoicePlayer {

    public static interface OnPlayListener
    {
        void completed();
        void error();
    }

    private AudioManager mAudioManager = null;
    private MediaPlayer mMediaPlayer = null;

    private OnPlayListener mListener = null;

    public VoicePlayer(Context context, String voiceFile)
    {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager.isWiredHeadsetOn())
        {
            switchToReceiver();
        }
        else//扬声器模式
        {
            switchToSpeaker();
        }

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try
        {
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mListener != null)
                    {
                        mListener.completed();
                    }
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if (mListener != null)
                    {
                        mListener.error();
                    }
                    return false;
                }
            });

            mMediaPlayer.setDataSource(voiceFile);
            mMediaPlayer.prepare();
        }
        catch (Exception exp)
        {
        }
    }

    public void setPlayListener(OnPlayListener listener)
    {
        mListener = listener;
    }

    public boolean isPlaying()
    {
        if (mMediaPlayer != null)
        {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public void play()
    {
        if (mMediaPlayer != null)
        {
            try
            {
                mMediaPlayer.start();
            }
            catch (Exception exp)
            {
            }
        }
    }

    public void pause()
    {
        if (mMediaPlayer != null)
        {
            try
            {
                mMediaPlayer.pause();
            }
            catch (Exception exp)
            {
            }
        }
    }

    public void stop()
    {
        if (mMediaPlayer != null)
        {
            try
            {
                mMediaPlayer.stop();
            }
            catch (Exception exp)
            {
            }
        }
    }

    //切换到外放
    public void switchToSpeaker()
    {
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        mAudioManager.setSpeakerphoneOn(true);
    }

    //切换到耳机
    public void switchToHeadset()
    {
        mAudioManager.setSpeakerphoneOn(false);
    }

    //切换到听筒
    public void switchToReceiver()
    {
        mAudioManager.setSpeakerphoneOn(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        }
    }

    public void release()
    {
        if (mMediaPlayer != null)
        {
            try
            {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            catch (Exception exp)
            {
            }
        }
    }

}
