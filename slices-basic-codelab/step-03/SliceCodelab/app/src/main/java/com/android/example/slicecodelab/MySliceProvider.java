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
import static com.android.example.slicecodelab.MainActivity.sTemperature;
import static com.android.example.slicecodelab.MyBroadcastReceiver.ACTION_CHANGE_TEMP;
import static com.android.example.slicecodelab.MyBroadcastReceiver.EXTRA_TEMP_VALUE;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;

public class MySliceProvider extends SliceProvider {
    private Context context;
    private static int sReqCode = 0;

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
        // Define the actions used in this slice
        SliceAction tempUp = new SliceAction(getChangeTempIntent(sTemperature + 1),
                IconCompat.createWithResource(context, R.drawable.ic_temp_up).toIcon(),
                "Increase temperature");
        SliceAction tempDown = new SliceAction(getChangeTempIntent(sTemperature - 1),
                IconCompat.createWithResource(context, R.drawable.ic_temp_down).toIcon(),
                "Decrease temperature");

        // Construct our parent builder
        ListBuilder listBuilder = new ListBuilder(context, sliceUri);

        // Construct the builder for the row
        ListBuilder.RowBuilder temperatureRow = new ListBuilder.RowBuilder(listBuilder);

        // Set title
        temperatureRow.setTitle(getTemperatureString(context));

        // Add the actions to appear at the end of the row
        temperatureRow.addEndItem(tempDown);
        temperatureRow.addEndItem(tempUp);

        // Set the primary action; this will activate when the row is tapped
        Intent intent = new Intent(getContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), sliceUri.hashCode(),
                intent, 0);
        SliceAction openTempActivity = new SliceAction(pendingIntent,
                IconCompat.createWithResource(context, R.drawable.ic_home).toIcon(),
                "Temperature controls");
        temperatureRow.setPrimaryAction(openTempActivity);

        // Add the row to the parent builder
        listBuilder.addRow(temperatureRow);

        // Build the slice
        return listBuilder.build();
    }

    private PendingIntent getChangeTempIntent(int value) {
        Intent intent = new Intent(ACTION_CHANGE_TEMP);
        intent.setClass(context, MyBroadcastReceiver.class);
        intent.putExtra(EXTRA_TEMP_VALUE, value);
        return PendingIntent.getBroadcast(getContext(), sReqCode++, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public static Uri getUri(Context context, String path) {
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(context.getPackageName())
                .appendPath(path)
                .build();
    }
}
