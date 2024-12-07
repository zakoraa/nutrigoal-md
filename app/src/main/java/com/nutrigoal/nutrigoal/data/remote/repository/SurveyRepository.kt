package com.nutrigoal.nutrigoal.data.remote.repository

import com.nutrigoal.nutrigoal.data.ResultState
import com.nutrigoal.nutrigoal.data.remote.entity.SurveyRequest
import com.nutrigoal.nutrigoal.data.remote.response.SurveyResponse
import com.nutrigoal.nutrigoal.data.remote.retrofit.SurveyService
import com.nutrigoal.nutrigoal.utils.asResultState
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