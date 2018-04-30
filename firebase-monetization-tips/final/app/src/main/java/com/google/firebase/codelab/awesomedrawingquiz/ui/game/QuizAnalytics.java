// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.firebase.codelab.awesomedrawingquiz.ui.game;

import com.google.firebase.analytics.FirebaseAnalytics;

import android.os.Bundle;
import android.support.annotation.NonNull;

public final class QuizAnalytics {

    private static final String EVENT_AD_PROMPT = "ad_prompt";

    private static final String EVENT_LEVEL_FAIL = "level_fail";

    private static final String EVENT_LEVEL_SUCCESS = "level_success";

    private static final String EVENT_LEVEL_WRONG_ANSWER = "level_wrong_answer";

    private static final String EVENT_STAGE_START = "stage_start";

    private static final String EVENT_STAGE_END = "stage_end";

    private static final String PARAM_AD_UNIT_ID = "ad_unit_id";

    private static final String PARAM_ELAPSED_TIME_SEC = "elapsed_time_sec";

    private static final String PARAM_HINT_USED = "hint_used";

    private static final String PARAM_NUMBER_OF_ATTEMPTS = "number_of_attempts";

    private static final String PARAM_NUMBER_OF_CORRECT_ANSWERS = "number_of_correct_answers";

    public static void logStageStart(@NonNull FirebaseAnalytics instance) {
        instance.logEvent(EVENT_STAGE_START, null);
    }

    public static void logLevelStart(
            @NonNull FirebaseAnalytics instance, @NonNull String levelName) {
        Bundle param = new Bundle();
        param.putString(FirebaseAnalytics.Param.LEVEL_NAME, levelName);
        instance.logEvent(FirebaseAnalytics.Event.LEVEL_START, param);
    }

    public static void logLevelWrongAnswer(
            @NonNull FirebaseAnalytics instance, @NonNull String levelName) {
        Bundle param = new Bundle();
        param.putString(FirebaseAnalytics.Param.LEVEL_NAME, levelName);
        instance.logEvent(EVENT_LEVEL_WRONG_ANSWER, param);
    }

    public static void logAdPrompt(
            @NonNull FirebaseAnalytics instance, @NonNull String adUnitId) {
        Bundle param = new Bundle();
        param.putString(PARAM_AD_UNIT_ID, adUnitId);
        instance.logEvent(EVENT_AD_PROMPT, param);
    }

    public static void logLevelSuccess(
            @NonNull FirebaseAnalytics instance, @NonNull String levelName,
            int numberOfAttempts, int elapsedTimeSec, boolean hintUsed) {
        Bundle param = new Bundle();
        param.putString(FirebaseAnalytics.Param.LEVEL_NAME, levelName);
        param.putInt(PARAM_NUMBER_OF_ATTEMPTS, numberOfAttempts);
        param.putInt(PARAM_ELAPSED_TIME_SEC, elapsedTimeSec);
        param.putLong(PARAM_HINT_USED, hintUsed ? 1 : 0);
        instance.logEvent(EVENT_LEVEL_SUCCESS, param);
    }

    public static void logLevelFail(
            @NonNull FirebaseAnalytics instance, @NonNull String levelName,
            int numberOfAttempts, int elapsedTimeSec, boolean hintUsed) {
        Bundle param = new Bundle();
        param.putString(FirebaseAnalytics.Param.LEVEL_NAME, levelName);
        param.putInt(PARAM_NUMBER_OF_ATTEMPTS, numberOfAttempts);
        param.putInt(PARAM_ELAPSED_TIME_SEC, elapsedTimeSec);
        param.putLong(PARAM_HINT_USED, hintUsed ? 1 : 0);
        instance.logEvent(EVENT_LEVEL_FAIL, param);
    }

    public static void logStageEnd(
            @NonNull FirebaseAnalytics instance, int numberOfCorrectAnswers) {
        Bundle param = new Bundle();
        param.putInt(PARAM_NUMBER_OF_CORRECT_ANSWERS, numberOfCorrectAnswers);
        instance.logEvent(EVENT_STAGE_END, param);
    }
}
