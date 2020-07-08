package com.greatdayhr.videorecruitment

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_video_preview.*
import org.json.JSONArray
import org.json.JSONException

class VideoPreviewActivity : AppCompatActivity() {
    private var videoPath: String? = null
    private var questions: JSONArray? = null
    private var currentQuestion: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_preview)
        actionBar?.hide()

        currentQuestion = intent.extras?.getInt("current_question", 0)!!

        initQuestions()
        initVideoView()
        initRetakeView()
        initListener()
        setQuestionView()
    }

    private fun initListener() {
        video_view.setOnClickListener {
            playVideo()
        }

        btn_close.setOnClickListener {
            this.finish()
        }

        btn_retake_video.setOnClickListener {
            this.finish()
        }

        btn_next_question.setOnClickListener {
            this.setResult(Activity.RESULT_OK)
            this.finish()
        }

        btn_play.setOnClickListener {
            playVideo()
        }
    }

    private fun initVideoView() {
        player_controller.setOnClickListener {
            // TODO
        }

        video_view.setVideoURI(Uri.parse(videoPath))
        video_view.setOnPreparedListener { mp ->
            val lp = video_view.layoutParams
            val videoWidth = mp.videoWidth
            val videoHeight = mp.videoHeight
            val viewWidth = video_view.width
            lp.height = viewWidth * videoHeight / videoWidth
            video_view.layoutParams = lp
            video_view.seekTo(1)
        }

        video_view.setOnCompletionListener {
            btn_play.visibility = View.VISIBLE
            layout_question.visibility = View.VISIBLE
            layout_retake.visibility = View.VISIBLE
        }
    }

    private fun initRetakeView() {
        if (currentQuestion == questions?.length()!! - 1) {
            btn_next_question.text = "Done"
        } else {
            btn_next_question.text = "Next Question"
        }
    }

    private fun initQuestions() {
        val q = intent.extras?.getString("q")
        try {
            questions = JSONArray(q)
            videoPath = questions?.getJSONObject(currentQuestion)?.getString("video_path")
            if (videoPath.isNullOrEmpty()) {
                finish()
            }
        } catch (e: JSONException) {
            finish()
        }
    }

    private fun playVideo() {
        if (!video_view.isPlaying) {
            layout_question.visibility = View.GONE
            layout_retake.visibility = View.GONE
            btn_play.visibility = View.GONE
            video_view.start()
        } else {
            btn_play.visibility = View.VISIBLE
            layout_question.visibility = View.VISIBLE
            layout_retake.visibility = View.VISIBLE
            video_view.pause()
        }
    }

    private fun setQuestionView() {
        findViewById<TextView>(R.id.tv_question_number).text = "${currentQuestion + 1} of ${questions?.length()} Question(s)"
        findViewById<TextView>(R.id.tv_question).text = questions?.optJSONObject(currentQuestion)?.optString("q")
    }
}