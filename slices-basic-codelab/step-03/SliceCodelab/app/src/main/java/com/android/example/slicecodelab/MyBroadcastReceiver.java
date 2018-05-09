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

import static com.android.example.slicecodelab.MainActivity.sTemperature;
import static com.android.example.slicecodelab.MainActivity.updateTemperature;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {

    public static String ACTION_CHANGE_TEMP = "com.android.example.slicecodelab.ACTION_CHANGE_TEMP";
    public static String EXTRA_TEMP_VALUE = "com.android.example.slicecodelab.EXTRA_TEMP_VALUE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (ACTION_CHANGE_TEMP.equals(action) && intent.getExtras() != null) {
            int newValue = intent.getExtras().getInt(EXTRA_TEMP_VALUE, sTemperature);
            updateTemperature(context, newValue);
        }
    }

}
