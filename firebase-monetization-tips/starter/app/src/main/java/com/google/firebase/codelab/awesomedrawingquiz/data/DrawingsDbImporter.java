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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

public class DrawingsDbImporter extends Completable {

    private final DrawingDao dao;

    private final List<Drawing> drawings;

    public DrawingsDbImporter(DrawingDao dao, List<Drawing> drawings) {
        this.dao = dao;
        this.drawings = drawings;
    }

    @Override
    protected void subscribeActual(CompletableObserver observer) {
        Listener listener = new Listener(observer);
        observer.onSubscribe(listener);

        dao.addAll(drawings);
        listener.onFinish();
    }

    private interface TaskListener {

        void onFinish();
    }

    static final class Listener implements TaskListener, Disposable {

        private final CompletableObserver observer;

        private final AtomicBoolean unsubscribed;

        Listener(CompletableObserver observer) {
            this.observer = observer;
            this.unsubscribed = new AtomicBoolean();
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
