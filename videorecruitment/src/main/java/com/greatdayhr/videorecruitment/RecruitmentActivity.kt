package com.greatdayhr.videorecruitment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class RecruitmentActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_PREVIEW_VIDEO = 8978
    }

    var questions: JSONArray? = null
    var currentQuestion = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        initQuestions()
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

    private fun setFragment(data: JSONObject) {
        val type = data.getInt("type")
        if (type == 1) {
            supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, VideoRecordingFragment(), "video")
                    .disallowAddToBackStack()
                    .commit()
        } else {
            supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, AnswerTextFragment(), "text")
                    .disallowAddToBackStack()
                    .commit()
        }
    }

    fun onVideoTaken(path: String) {
        questions?.getJSONObject(currentQuestion)?.putOpt("video_path", path)

        val intent = Intent(this, VideoPreviewActivity::class.java).apply {
            putExtra("q", questions?.toString())
            putExtra("current_question", currentQuestion)
        }
        startActivityForResult(intent, REQUEST_PREVIEW_VIDEO)
    }

    fun onTextAnswered(answer: String) {
        questions?.getJSONObject(currentQuestion)?.putOpt("answer", answer)

        currentQuestion++
        if (currentQuestion >= questions?.length()!!) {
            val results = questions?.toString()
            val intentResult = Intent().apply {
                putExtra("result", results)
            }
            setResult(Activity.RESULT_OK, intentResult)
            finish()
        } else {
            questions?.getJSONObject(currentQuestion)?.let {
                setFragment(it)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PREVIEW_VIDEO) {
            if (resultCode == Activity.RESULT_OK) {
                currentQuestion++
                if (currentQuestion >= questions?.length()!!) {
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

    override fun onPostResume() {
        super.onPostResume()
        if (currentQuestion < questions?.length()!!) {
            questions?.getJSONObject(currentQuestion)?.let {
                setFragment(it)
            }
        }
    }
}
