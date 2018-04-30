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

package com.google.firebase.codelab.awesomedrawingquiz.ui.game.event;

import com.google.auto.value.AutoValue;
import com.google.firebase.codelab.awesomedrawingquiz.data.Drawing;

import android.support.annotation.CheckResult;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

@AutoValue
public abstract class GameLevelClearEvent extends GameEvent {

    @CheckResult
    @NonNull
    public static GameLevelClearEvent create(
            @IntRange(from = 1) int numAttempts,
            @IntRange(from = 0) int elapsedTimeInSeconds,
            boolean isFinalLevel,
            boolean isHintUsed,
            @NonNull Drawing drawing) {
        return new AutoValue_GameLevelClearEvent(
                numAttempts, elapsedTimeInSeconds, isFinalLevel, isHintUsed, drawing);
    }

    public abstract int numAttempts();

    public abstract int elapsedTimeInSeconds();

    public abstract boolean isFinalLevel();

    public abstract boolean isHintUsed();

    @NonNull
    public abstract Drawing drawing();
}
