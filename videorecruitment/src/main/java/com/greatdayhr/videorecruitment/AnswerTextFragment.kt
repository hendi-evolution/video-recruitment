package com.greatdayhr.videorecruitment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_answer_text.*

class AnswerTextFragment : Fragment() {

    private fun parent(): RecruitmentActivity {
        return activity as RecruitmentActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_answer_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize question
        setQuestionView()

        // Initialize submit button
        if (parent().currentQuestion == parent().questions?.length()!! - 1) {
            btn_submit.text = getString(R.string.done)
        } else {
            btn_submit.text = getString(R.string.next_question)
        }
        btn_submit.setOnClickListener {
            val answer = et_answer.text.trim().toString()
            if (answer.isNullOrEmpty()) {
                Toast.makeText(this.activity, "Answer can not be empty", Toast.LENGTH_SHORT).show()
            } else {
                parent().onTextAnswered(answer)
            }
        }
    }

    private fun setQuestionView() {
        view?.let {
            it.findViewById<TextView>(R.id.tv_question_number).text = "${parent().currentQuestion + 1} of ${parent().questions?.length()} Question(s)"
            it.findViewById<TextView>(R.id.tv_question).text = parent().questions?.optJSONObject(parent().currentQuestion)?.optString("q")
            it.findViewById<TextView>(R.id.tv_video_duration_label).visibility = View.GONE
        }
    }
}