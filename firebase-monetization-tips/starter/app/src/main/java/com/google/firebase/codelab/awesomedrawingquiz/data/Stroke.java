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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

public final class Stroke {

    public final int[] x;

    public final int[] y;

    private Stroke(int[] x, int[] y) {
        this.x = x;
        this.y = y;
    }

    public static List<Stroke> parseStrokes(@NonNull Gson gson, @NonNull Drawing drawing) {
        List<Stroke> result = new LinkedList<>();

        JsonArray strokesInJson = new Gson().fromJson(drawing.getDrawing(), JsonArray.class);
        for (JsonElement strokeElem : strokesInJson) {
            JsonArray strokes = strokeElem.getAsJsonArray();
            int[] x = gson.fromJson(strokes.get(0), int[].class);
            int[] y = gson.fromJson(strokes.get(1), int[].class);

            result.add(new Stroke(x, y));
        }

        return result;
    }
}
