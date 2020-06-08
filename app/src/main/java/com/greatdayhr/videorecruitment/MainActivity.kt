package com.greatdayhr.videorecruitment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private var videoRecruitmentPlugin: VideoRecruitmentPlugin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        val questions = "[{\"id\":1,\"q\":\"Question number 1\",\"video_path\":\"\"},{\"id\":2,\"q\":\"Question number 2\",\"video_path\":\"\"}]"

        videoRecruitmentPlugin = VideoRecruitmentPlugin(object : VideoRecruitmentPluginListener {
            override fun onComplete(data: JSONArray) {
                Toast.makeText(this@MainActivity, data.toString(), Toast.LENGTH_LONG).show()
            }
        })

        btn_record.setOnClickListener {
            videoRecruitmentPlugin?.let {
                val intent = it.getIntent(this, questions)
                startActivityForResult(intent, VideoRecruitmentPlugin.REQUEST)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        videoRecruitmentPlugin?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}