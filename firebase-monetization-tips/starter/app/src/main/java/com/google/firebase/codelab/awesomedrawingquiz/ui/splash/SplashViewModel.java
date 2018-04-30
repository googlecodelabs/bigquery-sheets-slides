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

package com.google.firebase.codelab.awesomedrawingquiz.ui.splash;

import com.google.firebase.codelab.awesomedrawingquiz.data.DrawingAssetsConverter;
import com.google.firebase.codelab.awesomedrawingquiz.data.DrawingDao;
import com.google.firebase.codelab.awesomedrawingquiz.data.DrawingsDbImporter;

import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class SplashViewModel extends ViewModel {

    private static final String TAG = "SplashViewModel";

    private static final String KEY_INITIALIZED = "db_initialized";

    private final AssetManager assetManager;

    private final SharedPreferences preferences;

    private final DrawingDao dao;

    private Disposable disposable = null;

    public SplashViewModel(@NonNull AssetManager assetManager,
            @NonNull SharedPreferences preferences, @NonNull DrawingDao dao) {
        this.assetManager = assetManager;
        this.preferences = preferences;
        this.dao = dao;
    }

    void importDrawingsIfRequired(Action callback) {
        // if there is an ongoing task, don't start another one.
        if (null != disposable) {
            return;
        }

        Completable signal;

        if (!preferences.getBoolean(KEY_INITIALIZED, false)) {
            Log.d(TAG, "Database is not ready");
            signal = new DrawingAssetsConverter(assetManager).toList()
                    .flatMapCompletable(drawings -> new DrawingsDbImporter(dao, drawings))
                    .doOnComplete(() ->
                            preferences.edit().putBoolean(KEY_INITIALIZED, true).apply()
                    );
        } else {
            Log.d(TAG, "Database is ready to use");
            // add 1 second delay even if we already imported all of the drawings,
            // just to earn some time to show nice splash screen!
            signal = Completable.timer(1, TimeUnit.SECONDS, Schedulers.io());
        }

        disposable = signal.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    @Override
    protected void onCleared() {
        if (null != disposable) {
            disposable.dispose();
        }
    }
}
