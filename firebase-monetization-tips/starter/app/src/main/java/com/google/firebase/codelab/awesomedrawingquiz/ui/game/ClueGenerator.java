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

import android.support.annotation.NonNull;

public final class ClueGenerator {

    public static String generate(@NonNull String answer, int charCntToBeDisclosed) {
        StringBuilder b = new StringBuilder();
        int answerLength = answer.length();
        int disclosedCharCnt = 0;
        for (int i=0; i< answerLength; i++) {
            char c = answer.charAt(i);
            if (' ' == c) {
                b.append(c);
            } else if (disclosedCharCnt < charCntToBeDisclosed
                    && disclosedCharCnt < answerLength - 1) {
                b.append(c);
                disclosedCharCnt++;
            } else {
                b.append('*');
            }
        }
        return b.toString();
    }
}
