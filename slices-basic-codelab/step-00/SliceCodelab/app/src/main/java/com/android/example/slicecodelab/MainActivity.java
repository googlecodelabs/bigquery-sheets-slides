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

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.math.MathUtils;

import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static int sTemperature = 16; // Celsius
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.temperature_title);

        findViewById(R.id.increase_temp).setOnClickListener(this);
        findViewById(R.id.decrease_temp).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTextView.setText(getTemperatureString(getApplicationContext()));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.increase_temp:
                updateTemperature(getApplicationContext(), sTemperature + 1);
                break;
            case R.id.decrease_temp:
                updateTemperature(getApplicationContext(), sTemperature - 1);
                break;
        }
        mTextView.setText(getTemperatureString(getApplicationContext()));
    }

    public static String getTemperatureString(Context context) {
        return context.getString(R.string.temp_string, sTemperature);
    }

    public static void updateTemperature(Context context, int newValue) {
        newValue = MathUtils.clamp(newValue, 10, 30); // Lets keep temperatures reasonable
        if (newValue != sTemperature) {
            sTemperature = newValue;
        }
    }
}
