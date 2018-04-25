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

import android.content.Context;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

/** Helper class for Firebase storage of cloud anchor IDs. */
class StorageManager {

  /** Listener for a new Cloud Anchor ID from the Firebase Database. */
  interface CloudAnchorIdListener {
    void onCloudAnchorIdAvailable(String cloudAnchorId);
  }

  /** Listener for a new short code from the Firebase Database. */
  interface ShortCodeListener {
    void onShortCodeAvailable(Integer shortCode);
  }

  private static final String TAG = StorageManager.class.getName();
  private static final String KEY_ROOT_DIR = "shared_anchor_codelab_root";
  private static final String KEY_NEXT_SHORT_CODE = "next_short_code";
  private static final String KEY_PREFIX = "anchor;";
  private final DatabaseReference rootRef;

  StorageManager(Context context) {
    FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
    rootRef = FirebaseDatabase.getInstance(firebaseApp).getReference().child(KEY_ROOT_DIR);
    DatabaseReference.goOnline();
  }

  /** Gets a new short code that can be used to store the anchor ID. */
  void nextShortCode(ShortCodeListener listener) {
    // Run a transaction on the node containing the next short code available. This increments the
    // value in the database and retrieves it in one atomic all-or-nothing operation.
    rootRef
        .child(KEY_NEXT_SHORT_CODE)
        .runTransaction(
            new Transaction.Handler() {
              @Override
              public Transaction.Result doTransaction(MutableData currentData) {
                Integer shortCode = currentData.getValue(Integer.class);
                if (shortCode == null) {
                  shortCode = 0;
                }
                currentData.setValue(shortCode + 1);
                return Transaction.success(currentData);
              }

              @Override
              public void onComplete(
                  DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (!committed) {
                  Log.e(TAG, "Firebase Error", error.toException());
                  listener.onShortCodeAvailable(null);
                } else {
                  listener.onShortCodeAvailable(currentData.getValue(Integer.class));
                }
              }
            });
  }

  /** Stores the cloud anchor ID in the configured Firebase Database. */
  void storeUsingShortCode(int shortCode, String cloudAnchorId) {
    rootRef.child(KEY_PREFIX + shortCode).setValue(cloudAnchorId);
  }

  /**
   * Retrieves the cloud anchor ID using a short code. Returns an empty string if a cloud anchor ID
   * was not stored for this short code.
   */
  void getCloudAnchorID(int shortCode, CloudAnchorIdListener listener) {
    rootRef
        .child(KEY_PREFIX + shortCode)
        .addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onCloudAnchorIdAvailable(String.valueOf(dataSnapshot.getValue()));
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {}
            });
  }
}
