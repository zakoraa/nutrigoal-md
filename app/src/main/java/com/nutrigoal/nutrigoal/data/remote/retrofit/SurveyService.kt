package com.nutrigoal.nutrigoal.data.remote.retrofit

import com.nutrigoal.nutrigoal.data.remote.entity.SurveyRequest
import com.nutrigoal.nutrigoal.data.remote.response.SurveyResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface SurveyService {
    @POST("predictjson")
    suspend fun getSurveyResult(
        @Body request: SurveyRequest
    ): SurveyResponse

}
