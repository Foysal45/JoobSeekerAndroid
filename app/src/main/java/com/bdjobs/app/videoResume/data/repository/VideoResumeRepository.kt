package com.bdjobs.app.videoResume.data.repository

import android.content.Context
import com.bdjobs.app.videoResume.data.models.Question

class VideoResumeRepository(val context: Context) {

    fun getAllQuestions() : List<Question> {
        return listOf(
                Question(1,"Lorem ipsum dolor sit amet, consectetur elit. Maecenas venenatis euismod ante. non egestas. Nulla ornare rhoncus felis vitae dictum. Curabitur in euismod ligula.","00:60", isNew = false, isSubmitted = false),
                Question(2,"Lorem ipsum dolor sit amet, consectetur elit. Maecenas venenatis euismod ante. non egestas. Nulla ornare rhoncus felis vitae dictum. Curabitur in euismod ligula.","00:50", isNew = false, isSubmitted = false),
                Question(3,"Lorem ipsum dolor sit amet, consectetur elit. Maecenas venenatis euismod ante. non egestas. Nulla ornare rhoncus felis vitae dictum. Curabitur in euismod ligula.","00:40", isNew = false, isSubmitted = false),
                Question(4,"Lorem ipsum dolor sit amet, consectetur elit. Maecenas venenatis euismod ante. non egestas. Nulla ornare rhoncus felis vitae dictum. Curabitur in euismod ligula.","00:30", isNew = false, isSubmitted = false),
                Question(5,"Lorem ipsum dolor sit amet, consectetur elit. Maecenas venenatis euismod ante. non egestas. Nulla ornare rhoncus felis vitae dictum. Curabitur in euismod ligula.","00:20", isNew = true, isSubmitted = false),
        )
    }
}