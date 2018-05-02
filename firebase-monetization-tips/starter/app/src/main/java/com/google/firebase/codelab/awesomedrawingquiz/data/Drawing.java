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
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.IOException;

@Entity(tableName = "drawings")
public class Drawing {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "key_id")
    private String keyId;

    private String word;

    @ColumnInfo(name = "countrycode")
    private String countryCode;

    private String timestamp;

    private boolean recognized;

    private String drawing;

    public Drawing(@NonNull String keyId, String word,
            String countryCode, String timestamp, boolean recognized, String drawing) {
        this.keyId = keyId;
        this.word = word;
        this.countryCode = countryCode;
        this.timestamp = timestamp;
        this.recognized = recognized;
        this.drawing = drawing;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRecognized() {
        return recognized;
    }

    public void setRecognized(boolean recognized) {
        this.recognized = recognized;
    }

    public String getDrawing() {
        return drawing;
    }

    public void setDrawing(String drawing) {
        this.drawing = drawing;
    }

    public static class GsonTypeAdapter extends TypeAdapter<Drawing> {
        private final TypeAdapter<String> string_adapter;

        private final TypeAdapter<Boolean> boolean__adapter;

        private final TypeAdapter<JsonArray> jsonArray_adapter;

        public GsonTypeAdapter() {
            Gson gson = new Gson();
            this.string_adapter = gson.getAdapter(String.class);
            this.boolean__adapter = gson.getAdapter(Boolean.class);
            this.jsonArray_adapter = gson.getAdapter(JsonArray.class);
        }

        @Override
        public void write(JsonWriter jsonWriter, Drawing object) throws IOException {
            if (object == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.beginObject();
            jsonWriter.name("key_id");
            string_adapter.write(jsonWriter, object.keyId);
            jsonWriter.name("word");
            string_adapter.write(jsonWriter, object.word);
            jsonWriter.name("countrycode");
            string_adapter.write(jsonWriter, object.countryCode);
            jsonWriter.name("timestamp");
            string_adapter.write(jsonWriter, object.timestamp);
            jsonWriter.name("recognized");
            boolean__adapter.write(jsonWriter, object.recognized);
            jsonWriter.name("drawing");
            jsonArray_adapter.write(jsonWriter, jsonArray_adapter.fromJson(object.drawing));
            jsonWriter.endObject();
        }

        @Override
        public Drawing read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            jsonReader.beginObject();
            String keyId = null;
            String word = null;
            String countryCode = null;
            String timestamp = null;
            boolean recognized = false;
            String drawing = null;
            while (jsonReader.hasNext()) {
                String _name = jsonReader.nextName();
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull();
                    continue;
                }
                switch (_name) {
                    case "key_id": {
                        keyId = string_adapter.read(jsonReader);
                        if (null == keyId) {
                            throw new NullPointerException("key_id cannot be null");
                        }
                        break;
                    }
                    case "word": {
                        word = string_adapter.read(jsonReader);
                        break;
                    }
                    case "countrycode": {
                        countryCode = string_adapter.read(jsonReader);
                        break;
                    }
                    case "timestamp": {
                        timestamp = string_adapter.read(jsonReader);
                        break;
                    }
                    case "recognized": {
                        recognized = boolean__adapter.read(jsonReader);
                        break;
                    }
                    case "drawing": {
                        drawing = jsonArray_adapter.read(jsonReader).toString();
                        break;
                    }
                    default: {
                        jsonReader.skipValue();
                    }
                }
            }
            jsonReader.endObject();
            return new Drawing(keyId, word, countryCode, timestamp, recognized, drawing);
        }
    }
}
