package com.greatdayhr.videorecruitment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.otaliastudios.cameraview.VideoResult
import kotlinx.android.synthetic.main.activity_video_recording.*
import org.json.JSONArray
import org.json.JSONException

class VideoRecordingActivity : AppCompatActivity() {

    private var questions: JSONArray? = null
    private var currentQuestion = 0
    private val previewRequest = 8978;
    private lateinit var cameraHelper: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_recording)
        actionBar?.hide()

        initCamera()
        initQuestions()
        initButtonListener()

        setQuestionView()
    }

    private fun initCamera() {
        val cameraHelperListener = object : CameraHelperListener {
            override fun onVideoTaken(result: VideoResult) {
                questions?.getJSONObject(currentQuestion)?.putOpt("video_path", result.file.absolutePath)

                val intent = Intent(this@VideoRecordingActivity, VideoPreviewActivity::class.java).apply {
                    putExtra("q", questions?.toString())
                    putExtra("current_question", currentQuestion)
                }
                startActivityForResult(intent, previewRequest)
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
        cameraHelper = CameraHelper(camera, this, cameraHelperListener)
    }

    private fun initQuestions() {
        val q = intent.extras?.getString("q")
        try {
            questions = JSONArray(q)
            if (questions?.length() == 0) {
                throw Exception("Questions must be provided")
            }
        } catch (e: JSONException) {
            throw Exception("Parsing questions data error")
        }
    }

    private fun initButtonListener() {
        btn_close.setOnClickListener {
            this.finish()
        }

        btn_switch_camera.setOnClickListener {
            this.cameraHelper.getCamera().toggleFacing()
        }

        btn_record.setOnClickListener {
            this.cameraHelper.record("question-$currentQuestion.mp4")
        }
    }

    private fun setQuestionView() {
        findViewById<TextView>(R.id.tv_question_number).text = "${currentQuestion + 1} of ${questions?.length()} Question(s)"
        findViewById<TextView>(R.id.tv_question).text = questions?.optJSONObject(currentQuestion)?.optString("q")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == previewRequest) {
            if (resultCode == Activity.RESULT_OK) {
                currentQuestion++
                if (currentQuestion < questions?.length()!!) {
                    setQuestionView()
                } else {
                    val results = questions?.toString()
                    val intentResult = Intent().apply {
                        putExtra("result", results)
                    }
                    setResult(Activity.RESULT_OK, intentResult)
                    finish()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
