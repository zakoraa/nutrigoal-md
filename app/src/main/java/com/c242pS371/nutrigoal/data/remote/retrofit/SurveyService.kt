package com.c242pS371.nutrigoal.data.remote.retrofit

import com.c242pS371.nutrigoal.data.remote.entity.SurveyRequest
import com.c242pS371.nutrigoal.data.remote.response.SurveyResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface SurveyService {
    @POST("predict")
    suspend fun getSurveyResult(
        @Body request: SurveyRequest
    ): SurveyResponse

}
