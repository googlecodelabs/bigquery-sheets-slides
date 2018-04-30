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

package com.google.firebase.codelab.awesomedrawingquiz.viewmodel;

import com.google.firebase.codelab.awesomedrawingquiz.data.DatabaseProvider;
import com.google.firebase.codelab.awesomedrawingquiz.data.DrawingDao;
import com.google.firebase.codelab.awesomedrawingquiz.ui.game.GameViewModel;
import com.google.firebase.codelab.awesomedrawingquiz.ui.splash.SplashViewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

public class AwesomeDrawingQuizViewModelFactory implements ViewModelProvider.Factory {

    private AssetManager assetManager;

    private SharedPreferences preferences;

    private DrawingDao drawingDao;

    public AwesomeDrawingQuizViewModelFactory(@NonNull Context context) {
        this.assetManager = context.getAssets();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.drawingDao = DatabaseProvider.provideDrawingDao(context);
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GameViewModel.class)) {
            return (T) new GameViewModel(drawingDao);
        } else if (modelClass.isAssignableFrom(SplashViewModel.class)) {
            return (T) new SplashViewModel(assetManager, preferences, drawingDao);
        }
        throw new IllegalArgumentException("unknown model class " + modelClass);
    }
}
