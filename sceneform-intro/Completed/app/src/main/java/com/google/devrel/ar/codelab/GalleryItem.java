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
Copyright 2018 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.google.devrel.ar.codelab;

import android.content.ClipData;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.PersistableBundle;
import android.view.View;

class GalleryItem extends android.support.v7.widget.AppCompatImageView {
    private final Uri uri;

    public static final String RENDERABLE = "Renderable";
    public static final String KeyName = "name";
    public static final String KeyUri = "uri";


    public GalleryItem(Context context, String name, int resID, Uri uri) {
        super(context);
        setImageBitmap(BitmapFactory.decodeResource(context.getResources(), resID));
        setContentDescription(name);
        this.uri = uri;

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onStartDragging();
                return  true;
            }
        });

    }

    public Uri getUri() {
        return uri;
    }

    private void onStartDragging() {
        ClipData clipData =  ClipData.newRawUri(RENDERABLE, uri);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString(KeyName, getContentDescription().toString());
        bundle.putString(KeyUri, uri.toString());
        clipData.getDescription().setExtras(bundle);

        startDragAndDrop(clipData,  // the data to be dragged
                new ViewShadowBuilder(this),  // the drag shadow builder
                null,      // no need to use local data
                0          // flags (not currently used, set to 0)
        );
    }
}

