package com.greatdayhr.videorecruitment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.otaliastudios.cameraview.VideoResult
import kotlinx.android.synthetic.main.fragment_video_recording.*

class VideoRecordingFragment : Fragment() {

    private lateinit var cameraHelper: CameraHelper

    fun parent(): RecruitmentActivity {
        return activity as RecruitmentActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_video_recording, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setQuestionView()
        initCamera()
        initButtonListener()
    }

    private fun initCamera() {
        val cameraHelperListener = object : CameraHelperListener {
            override fun onVideoTaken(result: VideoResult) {
                parent().onVideoTaken(result.file.absolutePath)
            }

            override fun onRecordingStart() {
                layout_question.visibility = View.GONE
                layout_remaining.visibility = View.VISIBLE
                iv_record_indicator.visibility = View.VISIBLE
                btn_switch_camera.visibility = View.GONE
                btn_record.setImageResource(R.drawable.ic_video_rec_stop)
            }

            override fun onRecordingEnd() {
                iv_record_indicator.visibility = View.GONE
                layout_question.visibility = View.VISIBLE
                btn_switch_camera.visibility = View.VISIBLE
                tv_record_remaining.text = getString(R.string.press_record_button)
                btn_record.setImageResource(R.drawable.ic_video_rec_start)
            }

            override fun onRecordTimerTick(tickTime: Long) {
                tv_record_remaining.text = "${cameraHelper.formatTime(tickTime)} Remaining"
            }
        }

        val duration = parent().questions?.optJSONObject(parent().currentQuestion)?.optString("videoDuration")
        duration?.let {
            val durationMillis = it.toLong() * 60 * 1000
            cameraHelper = CameraHelper.Builder()
                    .setCameraView(camera)
                    .setFragment(this)
                    .setDuration(durationMillis)
                    .setListener(cameraHelperListener)
                    .build()
        }
    }

    private fun initButtonListener() {
        btn_close.setOnClickListener {
            activity?.finish()
        }

        btn_switch_camera.setOnClickListener {
            this.cameraHelper.getCamera()?.toggleFacing()
        }

        btn_record.setOnClickListener {
            this.cameraHelper.record("question-${parent().currentQuestion}.mp4")
        }
    }

    private fun setQuestionView() {
        view?.let {
            val currentQuestionLabel = "${parent().currentQuestion + 1} of ${parent().questions?.length()} Question(s)"
            val questionLabel = parent().questions?.optJSONObject(parent().currentQuestion)?.optString("q")
            val durationLabel = parent().questions?.optJSONObject(parent().currentQuestion)?.optString("videoDuration")

            it.findViewById<TextView>(R.id.tv_question_number).text = currentQuestionLabel
            it.findViewById<TextView>(R.id.tv_question).text = questionLabel
            it.findViewById<TextView>(R.id.tv_video_duration_label).text = "$durationLabel minutes video"
        }
    }
}