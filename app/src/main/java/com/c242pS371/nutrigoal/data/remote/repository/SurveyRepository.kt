package com.c242pS371.nutrigoal.data.remote.repository

import com.c242pS371.nutrigoal.data.ResultState
import com.c242pS371.nutrigoal.data.remote.entity.SurveyRequest
import com.c242pS371.nutrigoal.data.remote.response.SurveyResponse
import com.c242pS371.nutrigoal.data.remote.retrofit.SurveyService
import com.c242pS371.nutrigoal.data.extension.asResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SurveyRepository(private val surveyService: SurveyService) {

    fun getSurveyResult(surveyRequest: SurveyRequest): Flow<ResultState<SurveyResponse>> {
        return flow {
            val result = surveyService.getSurveyResult(surveyRequest)

            emit(result)
        }.asResultState()
    }

}