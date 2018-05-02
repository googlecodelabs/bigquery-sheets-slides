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

package com.google.firebase.codelab.awesomedrawingquiz;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.Task;
import com.google.firebase.codelab.awesomedrawingquiz.viewmodel.AwesomeDrawingQuizViewModelFactory;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import android.app.Application;
import android.util.Log;

public class AwesomeDrawingQuiz extends Application {

    private static final String ADMOB_APP_ID = "ca-app-pub-3940256099942544~5808341321";

    private AwesomeDrawingQuizViewModelFactory viewModelFactory;

    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(this, ADMOB_APP_ID);
        provideRemoteConfig();

        viewModelFactory = new AwesomeDrawingQuizViewModelFactory(this, provideRemoteConfig());
    }

    public AwesomeDrawingQuizViewModelFactory provideViewModelFactory() {
        return viewModelFactory;
    }

    private FirebaseRemoteConfig provideRemoteConfig() {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        remoteConfig.setConfigSettings(settings);
        remoteConfig.setDefaults(R.xml.remote_config_defaults);

        Task<Void> fetchTask;
        if (BuildConfig.DEBUG) {
            fetchTask = remoteConfig.fetch(0L);
        } else {
            fetchTask = remoteConfig.fetch();
        }

        fetchTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("AwesomeDrawingQuiz", "Remote config value fetched");
                remoteConfig.activateFetched();
            }
        });
        return remoteConfig;
    }
}
