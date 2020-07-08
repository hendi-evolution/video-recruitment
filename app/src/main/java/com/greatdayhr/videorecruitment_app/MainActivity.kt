package com.greatdayhr.videorecruitment_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.greatdayhr.videorecruitment.VideoRecruitmentPlugin
import com.greatdayhr.videorecruitment.VideoRecruitmentPluginListener
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private var videoRecruitmentPlugin: VideoRecruitmentPlugin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        val questions = "[{\"id\":1,\"q\":\"Question number 1\",\"video_path\":\"\",\"type\":2},{\"id\":1,\"q\":\"Question number 2\",\"video_path\":\"\",\"type\":1},{\"id\":2,\"q\":\"Question number 3\",\"video_path\":\"\",\"type\":2}]"
//        val questions = "[{\"id\":1,\"q\":\"Question number 1\",\"video_path\":\"\",\"type\":1},{\"id\":1,\"q\":\"Question number 1\",\"video_path\":\"\",\"type\":1}]"

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