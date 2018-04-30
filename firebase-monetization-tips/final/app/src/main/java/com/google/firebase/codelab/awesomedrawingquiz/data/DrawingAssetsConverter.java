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

package com.google.firebase.codelab.awesomedrawingquiz.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public final class DrawingAssetsConverter extends Observable<Drawing> {

    private final AssetManager am;

    private final Gson gson;

    public DrawingAssetsConverter(AssetManager am) {
        this.am = am;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Drawing.class, new Drawing.GsonTypeAdapter())
                .create();
    }

    @Override
    protected void subscribeActual(Observer<? super Drawing> observer) {
        Listener listener = new Listener(observer);
        observer.onSubscribe(listener);

        try {
            String[] files = am.list("drawings");

            for (String path : files) {
                InputStream is = am.open("drawings/" + path);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(am.open("drawings/" + path)));

                String line;
                do {
                    line = reader.readLine();
                    Drawing drawing = gson.fromJson(line, Drawing.class);

                    if (null != drawing) {
                        listener.onReadDrawing(drawing);
                    }
                } while (null != line);

                is.close();
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        listener.onFinish();
    }

    private interface TaskListener {

        void onReadDrawing(Drawing d);

        void onFinish();
    }

    static final class Listener implements TaskListener, Disposable {

        private final Observer<? super Drawing> observer;

        private final AtomicBoolean unsubscribed;

        Listener(Observer<? super Drawing> observer) {
            this.observer = observer;
            this.unsubscribed = new AtomicBoolean();
        }

        @Override
        public void onReadDrawing(Drawing d) {
            if (!isDisposed()) {
                observer.onNext(d);
            }
        }

        @Override
        public void onFinish() {
            if (!isDisposed()) {
                observer.onComplete();
            }
        }

        @Override
        public void dispose() {
            unsubscribed.compareAndSet(false, true);
        }

        @Override
        public boolean isDisposed() {
            return unsubscribed.get();
        }
    }
}
