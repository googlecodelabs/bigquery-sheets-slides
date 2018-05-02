/*
 * Copyright 2018 Google Inc. All Rights Reserved.
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

package com.google.ar.core.codelab.cloudanchor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/** Helper class for managing on-device storage of cloud anchor IDs. */
class StorageManager {
  private static final String NEXT_SHORT_CODE = "next_short_code";
  private static final String KEY_PREFIX = "anchor;";
  private static final int INITIAL_SHORT_CODE = 142;

  /** Gets a new short code that can be used to store the anchor ID. */
  int nextShortCode(Activity activity) {
    SharedPreferences sharedPrefs = activity.getPreferences(Context.MODE_PRIVATE);
    int shortCode = sharedPrefs.getInt(NEXT_SHORT_CODE, INITIAL_SHORT_CODE);
    sharedPrefs
        .edit()
        .putInt(NEXT_SHORT_CODE, shortCode + 1)
        .apply();
    return shortCode;
  }

  /** Stores the cloud anchor ID in the activity's SharedPrefernces. */
  void storeUsingShortCode(Activity activity, int shortCode, String cloudAnchorId) {
    SharedPreferences sharedPrefs = activity.getPreferences(Context.MODE_PRIVATE);
    sharedPrefs.edit().putString(KEY_PREFIX + shortCode, cloudAnchorId).apply();
  }

  /**
   * Retrieves the cloud anchor ID using a short code. Returns an empty string if a cloud anchor ID
   * was not stored for this short code.
   */
  String getCloudAnchorId(Activity activity, int shortCode) {
    SharedPreferences sharedPrefs = activity.getPreferences(Context.MODE_PRIVATE);
    return sharedPrefs.getString(KEY_PREFIX + shortCode, "");
  }
}
