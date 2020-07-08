package com.greatdayhr.videorecruitment

import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraLogger
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.VideoResult
import java.io.File

class CameraHelper(private val cameraView: CameraView, private val fragment: Fragment, private val listener: CameraHelperListener) : CameraListener(), LifecycleObserver {

    private val videoDuration: Long = 30 * 1000
    private val preRecordDuration: Long = 1000
    private var mLifecycle: Lifecycle
    private var isClosed = false;
    private var recordTimer: CountDownTimer? = null

    init {
        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE)
        cameraView.setLifecycleOwner(fragment)
        cameraView.addCameraListener(this)

        mLifecycle = fragment.lifecycle
        mLifecycle.addObserver(this)
    }

    fun getCamera(): CameraView {
        return cameraView;
    }

    fun record(fileName: String) {
        if (cameraView.isTakingVideo) {
            cameraView.stopVideo()
            return
        }
        //cameraView.takeVideo(File(activity.filesDir, fileName), videoDuration.toInt())
        cameraView.takeVideoSnapshot(File(fragment.context?.cacheDir, fileName), videoDuration.toInt())
    }

    fun formatTime(time: Long): String {
        if (time > 9) {
            return "00:$time"
        }
        return "00:0$time"
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        isClosed = true
        recordTimer?.cancel()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        isClosed = true
        recordTimer?.cancel()
    }

    // MARK: CameraListener Implementation
    override fun onVideoTaken(result: VideoResult) {
        listener.onVideoTaken(result)
    }

    override fun onVideoRecordingStart() {
        listener.onRecordingStart()
        recordTimer?.cancel()
        listener.onRecordTimerTick(videoDuration / 1000)
        recordTimer = object : CountDownTimer(videoDuration, 1000) {
            override fun onFinish() {
                // Do nothing
            }

            override fun onTick(millisUntilFinished: Long) {
                listener.onRecordTimerTick(millisUntilFinished / 1000)
            }
        }
        recordTimer?.start()
    }

    override fun onVideoRecordingEnd() {
        recordTimer?.cancel()
        listener.onRecordingEnd()
    }
}

interface CameraHelperListener {
    fun onVideoTaken(result: VideoResult)
    fun onRecordingStart()
    fun onRecordingEnd()
    fun onRecordTimerTick(tickTime: Long) // in second
}