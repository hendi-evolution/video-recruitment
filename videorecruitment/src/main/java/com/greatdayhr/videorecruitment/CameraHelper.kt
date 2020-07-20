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

class CameraHelper private constructor(
        private val cameraView: CameraView?,
        private val fragment: Fragment?,
        private val duration: Long?,
        private val listener: CameraHelperListener?
) : CameraListener(), LifecycleObserver {

    private var mLifecycle: Lifecycle? = null
    private var recordTimer: CountDownTimer? = null

    class Builder(
            private var cameraView: CameraView? = null,
            private var fragment: Fragment? = null,
            private var duration: Long? = null,
            private var listener: CameraHelperListener? = null
    ) {
        fun setCameraView(cameraView: CameraView) = apply { this.cameraView = cameraView }
        fun setFragment(fragment: Fragment) = apply { this.fragment = fragment }
        fun setDuration(duration: Long) = apply { this.duration = duration }
        fun setListener(listener: CameraHelperListener) = apply { this.listener = listener }
        fun build() = CameraHelper(cameraView, fragment, duration, listener)
    }

    init {
        if (cameraView == null) {
            throw NullPointerException("Please set camera view");
        }
        if (fragment == null) {
            throw NullPointerException("Please set fragment");
        }
        if (duration == null) {
            throw NullPointerException("Please set duration");
        }

        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE)
        cameraView?.setLifecycleOwner(fragment!!)
        cameraView?.addCameraListener(this)
        cameraView?.videoMaxDuration = duration?.toInt()!!

        mLifecycle = fragment?.lifecycle
        mLifecycle?.addObserver(this)
    }

    fun getCamera(): CameraView? {
        return cameraView;
    }

    fun record(fileName: String) {
        if (cameraView?.isTakingVideo!!) {
            cameraView?.stopVideo()
            return
        }
        cameraView.takeVideoSnapshot(File(fragment?.context?.cacheDir, fileName), duration?.toInt()!!)
    }

    fun formatTime(timeSecond: Long): String {
        val minutes = timeSecond / 60
        val seconds = timeSecond % 60

        val strMinutes = if (minutes < 10) {
            "0$minutes"
        } else {
            "$minutes"
        }

        val strSeconds = if (seconds < 10) {
            "0$seconds"
        } else {
            "$seconds"
        }

        return "$strMinutes:$strSeconds"
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        recordTimer?.cancel()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        recordTimer?.cancel()
    }

    // MARK: CameraListener Implementation
    override fun onVideoTaken(result: VideoResult) {
        listener?.onVideoTaken(result)
    }

    override fun onVideoRecordingStart() {
        listener?.onRecordingStart()
        recordTimer?.cancel()
        listener?.onRecordTimerTick(duration!! / 1000)
        recordTimer = object : CountDownTimer(duration!!, 1000) {
            override fun onFinish() {
                // Do nothing
            }

            override fun onTick(millisUntilFinished: Long) {
                listener?.onRecordTimerTick(millisUntilFinished / 1000)
            }
        }
        recordTimer?.start()
    }

    override fun onVideoRecordingEnd() {
        recordTimer?.cancel()
        listener?.onRecordingEnd()
    }
}

interface CameraHelperListener {
    fun onVideoTaken(result: VideoResult)
    fun onRecordingStart()
    fun onRecordingEnd()
    fun onRecordTimerTick(tickTime: Long) // in second
}