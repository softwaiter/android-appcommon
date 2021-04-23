package com.puerlink.common;

import android.media.MediaRecorder;
import android.text.TextUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Date;

/**
 * Created by wangxm on 2016/8/18.
 */
public class VoiceRecordingController {

    public interface OnSoundRecordingListener
    {
        /**
         * 开始录制
         * @param recorder
         */
        void start(VoiceRecordingController recorder);

        /**
         * 录制过程中振幅变化情况
         * @param amplitude
         */
        void amplitudeChanged(int amplitude);

        /**
         * 停止录制
         * @param recorder
         */
        void stop(VoiceRecordingController recorder);

        /**
         * 取消录制
         * @param recorder
         */
        void cancel(VoiceRecordingController recorder);

        /**
         * 达到最大录制时间，自动停止录制
         * @param recorder
         */
        void finish(VoiceRecordingController recorder);

        /**
         * 录制发生错误
         * @param errCode
         * @param errMsg
         */
        void error(int errCode, String errMsg);
    }

    static class VoiceRecordingAmplitudeMonitor extends Thread
    {
        private WeakReference<VoiceRecordingController> mController;

        public VoiceRecordingAmplitudeMonitor(VoiceRecordingController controller)
        {
            mController = new WeakReference<VoiceRecordingController>(controller);
        }

        private VoiceRecordingController getController()
        {
            if (mController != null)
            {
                return mController.get();
            }
            return null;
        }

        @Override
        public void run() {
            while (true)
            {
                try
                {
                    VoiceRecordingController controller = getController();
                    if (controller != null && controller.isRecording())
                    {
                        controller.amplitudeChanged();

                        Thread.sleep(100L);
                    }
                    else
                    {
                        break;
                    }
                }
                catch (Exception exp)
                {
                }
            }

            mController.clear();
            mController = null;
        }
    }

    private MediaRecorder mRecorder;
    private String mFilePath;
    private long mStartTime, mEndTime;

    private boolean mIsRecording = false;

    private VoiceRecordingAmplitudeMonitor mMonitor;

    private OnSoundRecordingListener mListener;

    public VoiceRecordingController()
    {
    }

    public VoiceRecordingController(OnSoundRecordingListener listener)
    {
        mListener = listener;
    }

    private void createRecorder(int maxDuration, String filePath)
    {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setMaxDuration(maxDuration);
        mRecorder.setAudioChannels(1);
        mRecorder.setAudioSamplingRate(8000);
        mRecorder.setAudioEncodingBitRate(64);
        mRecorder.setOutputFile(filePath);
        mRecorder.setOnInfoListener(onRecordingInfoListener);

        mFilePath = filePath;
    }

    MediaRecorder.OnInfoListener onRecordingInfoListener = new MediaRecorder.OnInfoListener()
    {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
            {
                stopMonitor();

                try
                {
                    mEndTime = new Date().getTime();

                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;

                    mIsRecording = false;
                }
                catch (Exception exp)
                {
                }
                finally {
                    if (mListener != null)
                    {
                        mListener.finish(VoiceRecordingController.this);
                    }
                }
            }
        }
    };

    public boolean isRecording()
    {
        return mIsRecording;
    }

    public boolean start(int maxDuration, String filePath)
    {
        if (!mIsRecording)
        {
            if (!TextUtils.isEmpty(filePath)) {
                createRecorder(maxDuration, filePath);

                if (mRecorder != null) {
                    try {
                        mRecorder.prepare();
                        mRecorder.start();
                        mIsRecording = true;

                        mStartTime = new Date().getTime();

                        if (mListener != null)
                        {
                            mListener.start(this);
                        }

                        startMonitor();

                        return true;
                    } catch (Exception exp) {
                        if (mListener != null)
                        {
                            mListener.error(-4, exp.getMessage());
                        }
                    }
                }
            }
        }
        return false;
    }

    private void amplitudeChanged()
    {
        if (mListener != null)
        {
            int amplitude = getCurrentAmplitude();
            mListener.amplitudeChanged(amplitude);
        }
    }

    private void startMonitor()
    {
        mMonitor = new VoiceRecordingAmplitudeMonitor(VoiceRecordingController.this);
        mMonitor.start();
    }

    private void stopMonitor()
    {
        try
        {
            if (mMonitor != null)
            {
                if (mMonitor.isAlive())
                {
                    mMonitor.interrupt();
                }
                mMonitor = null;
            }
        }
        catch (Exception exp)
        {
        }
    }

    public void reset()
    {
        stop(false);
    }

    public int stop()
    {
        return stop(true);
    }

    public int stop(boolean triggerListener)
    {
        if (mIsRecording)
        {
            if (mRecorder != null)
            {
                stopMonitor();

                try {
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;

                    mIsRecording = false;

                    if (!TextUtils.isEmpty(mFilePath))
                    {
                        File f = new File(mFilePath);
                        if (f != null && f.exists() && f.isFile())
                        {
                            if (f.length() == 0)
                            {
                                f.delete();

                                if (mListener != null && triggerListener)
                                {
                                    mListener.error(-3, "录音文件长度无效。");
                                }

                                return -3;
                            }
                            else
                            {
                                mEndTime = new Date().getTime();

                                if (mListener != null && triggerListener)
                                {
                                    mListener.stop(this);
                                }

                                return (int)((mEndTime - mStartTime) / 1000);
                            }
                        }
                        else
                        {
                            if (mListener != null && triggerListener)
                            {
                                mListener.error(-2, "未找到录音文件。");
                            }
                            return -2;
                        }
                    }
                    else
                    {
                        if (mListener != null && triggerListener)
                        {
                            mListener.error(-1, "未指定文件名。");
                        }
                        return -1;
                    }
                }
                catch (Exception exp)
                {
                    if (mListener != null && triggerListener)
                    {
                        mListener.error(-4, exp.getMessage());
                    }
                    return -4;
                }
            }
        }
        return 0;
    }

    public void cancel()
    {
        if (mIsRecording) {
            if (mRecorder != null) {
                stopMonitor();

                try {
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;

                    mIsRecording = false;

                    if (!TextUtils.isEmpty(mFilePath)) {
                        File f = new File(mFilePath);
                        if (f != null && f.exists() && f.isFile()) {
                            f.delete();
                        }
                    }

                    if (mListener != null)
                    {
                        mListener.cancel(this);
                    }
                } catch (Exception exp) {
                    if (mListener != null)
                    {
                        mListener.error(-4, exp.getMessage());
                    }
                }
            }
        }
    }

    public int getCurrentAmplitude()
    {
        if (mRecorder != null && mIsRecording)
        {
            return mRecorder.getMaxAmplitude();
        }
        return 0;
    }

    public int getDuration()
    {
        return Math.max(0, (int) ((mEndTime - mStartTime) / 1000));
    }

    public String getFileName()
    {
        return mFilePath;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (mRecorder != null)
        {
            mRecorder.release();
            mRecorder = null;
        }
    }
}