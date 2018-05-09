/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.slicecodelab;


import static com.android.example.slicecodelab.MainActivity.getTemperatureString;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.ListBuilder;

public class MySliceProvider extends SliceProvider {
    private Context context;

    @Override
    public boolean onCreateSliceProvider() {
        context = getContext();
        return true;
    }

    @Override
    public Slice onBindSlice(Uri sliceUri) {
        final String path = sliceUri.getPath();
        switch (path) {
            case "/temperature":
                return createTemperatureSlice(sliceUri);
        }
        return null;
    }

    private Slice createTemperatureSlice(Uri sliceUri) {
        // Construct our parent builder
        ListBuilder listBuilder = new ListBuilder(context, sliceUri, ListBuilder.INFINITY);

        // Construct the builder for the row
        ListBuilder.RowBuilder temperatureRow = new ListBuilder.RowBuilder(listBuilder);

        // Set title
        temperatureRow.setTitle(getTemperatureString(context));

        // TODO: add actions to row; in later step

        // Add the row to the parent builder
        listBuilder.addRow(temperatureRow);

        // Build the slice
        return listBuilder.build();
    }

    public static Uri getUri(Context context, String path) {
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(context.getPackageName())
                .appendPath(path)
                .build();
    }
}
