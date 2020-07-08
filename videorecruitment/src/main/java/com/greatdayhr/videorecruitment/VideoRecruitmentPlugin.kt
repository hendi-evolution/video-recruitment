package com.greatdayhr.videorecruitment

import android.app.Activity
import android.content.Intent
import org.json.JSONArray
import org.json.JSONException

class VideoRecruitmentPlugin(private val listener: VideoRecruitmentPluginListener? = null) {

    companion object {
        const val REQUEST = 6573
    }

//    fun getIntent(activity: Activity, questionsData: String): Intent {
//        return Intent(activity, VideoRecordingActivity::class.java).apply {
//            putExtra("q", questionsData)
//        }
//    }

    fun getIntent(activity: Activity, questionsData: String): Intent {
        return Intent(activity, RecruitmentActivity::class.java).apply {
            putExtra("q", questionsData)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data?.getStringExtra("result")
                try {
                    val jsonArray = JSONArray(result)
                    listener?.onComplete(jsonArray)
                } catch (e: JSONException) {

                }
            }
        }
    }
}

interface VideoRecruitmentPluginListener {
    fun onComplete(data: JSONArray);
}