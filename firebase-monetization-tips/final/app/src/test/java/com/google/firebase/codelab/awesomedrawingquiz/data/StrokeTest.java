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

package com.google.firebase.codelab.awesomedrawingquiz.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class StrokeTest {

    @Test
    public void parseStrokes() throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Drawing.class, new Drawing.GsonTypeAdapter())
                .create();
        Drawing d = gson.fromJson(
                new FileReader(getTestResourceFilePath("item.ndjson")), Drawing.class);

        List<Stroke> strokes = Stroke.parseStrokes(gson, d);

        // Total strokes
        assertEquals(2, strokes.size());

        // First stroke
        Stroke stroke1 = strokes.get(0);
        // X coordinates of first stroke
        int[] stroke1_xcoords = stroke1.x;
        assertEquals(5, stroke1_xcoords.length);
        // verify X coordinate values
        assertEquals(122, stroke1_xcoords[0]);
        assertEquals(102, stroke1_xcoords[1]);
        assertEquals(56, stroke1_xcoords[2]);
        assertEquals(45, stroke1_xcoords[3]);
        assertEquals(66, stroke1_xcoords[4]);
        // Y coordinates of first stroke
        int[] stroke1_ycoords = stroke1.y;
        assertEquals(5, stroke1_ycoords.length);
        // verify Y coordinates of first stroke
        assertEquals(0, stroke1_ycoords[0]);
        assertEquals(3, stroke1_ycoords[1]);
        assertEquals(3, stroke1_ycoords[2]);
        assertEquals(29, stroke1_ycoords[3]);
        assertEquals(22, stroke1_ycoords[4]);

        // Second stroke
        Stroke stroke2 = strokes.get(1);
        // X coordinates of first stroke
        int[] stroke2_xcoords = stroke2.x;
        assertEquals(5, stroke2_xcoords.length);
        // verify X coordinate values
        assertEquals(112, stroke2_xcoords[0]);
        assertEquals(121, stroke2_xcoords[1]);
        assertEquals(167, stroke2_xcoords[2]);
        assertEquals(222, stroke2_xcoords[3]);
        assertEquals(224, stroke2_xcoords[4]);
        // Y coordinates of first stroke
        int[] stroke2_ycoords = stroke2.y;
        assertEquals(5, stroke2_ycoords.length);
        // verify Y coordinates of first stroke
        assertEquals(4, stroke2_ycoords[0]);
        assertEquals(2, stroke2_ycoords[1]);
        assertEquals(2, stroke2_ycoords[2]);
        assertEquals(7, stroke2_ycoords[3]);
        assertEquals(44, stroke2_ycoords[4]);
    }

    @NonNull
    private String getTestResourceFilePath(@NonNull String path) {
        String filename = this.getClass().getClassLoader().getResource(path).getFile();
        if (null == filename) {
            throw new IllegalStateException("Cannot find a file in path " + path);
        }
        return new File(filename).getAbsolutePath();
    }
}
