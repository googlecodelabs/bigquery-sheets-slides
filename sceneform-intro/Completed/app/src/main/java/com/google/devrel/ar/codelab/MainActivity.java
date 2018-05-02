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

import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.Renderer;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        initializeGallery();

        ArFragment fragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);

        fragment.getArSceneView().setOnDragListener(new RenderableDragListener(fragment));

        fragment.getArSceneView().getScene().setOnTouchListener(new Scene.OnTouchListener() {
            @Override
            public boolean onSceneTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                if (fragment.getTransformationSystem().getSelectedNode() != null) {
                    fragment.getTransformationSystem().selectNode(null);
                    return true;
                }
                return false;
            }
        });

    }

    private void takePhoto() {
        ArFragment fragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);

        fragment.getArSceneView().getRenderer().captureScreenshot(new Renderer.OnScreenshotListener() {
            @Override
            public void onScreenshotResult(ByteBuffer image, int width, int height) {
                Bitmap bitmap;
                try {
                    bitmap = createBitmap(image, width, height);
                } catch (IllegalArgumentException ex) {
                    Toast toast = Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                // Save the bitmap to disk as a JPG.
                String date =
                        new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
                final String filename =
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES) + File.separator + "RenderCore/" + date + "_screenshot.jpg";

                try {
                    saveBitmapToDisk(bitmap, filename, /*quality*/ 100);
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"Photo saved",Snackbar.LENGTH_LONG);
                    snackbar.setAction("Open in Photos", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File photoFile = new File(filename);

                            Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                    MainActivity.this.getPackageName() +".ar.codelab.name.provider",
                                    photoFile);
                            Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
                            intent.setDataAndType(photoURI,"image/*");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);

                        }
                    });
                    snackbar.show();

                } catch (IOException ex) {
                    Toast toast = Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public static void saveBitmapToDisk(Bitmap bitmap, String filename, int quality)
            throws IOException {
        File out = new File(filename);
        if(!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }

    public static Bitmap createBitmap(ByteBuffer image, int width, int height) {
        if (image == null || width < 1 || height < 1) {
            throw new IllegalArgumentException("Cannot create bitmap, invalid image.");
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            throw new IllegalArgumentException(
                    "Failed to create a bitmap: " + width + " x " + height + ".");
        }

        bitmap.copyPixelsFromBuffer(image);
        return bitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeGallery() {

        LinearLayout gallery = findViewById(R.id.galleryView);

        GalleryItem rose = new GalleryItem(this, "andy",R.drawable.droid_thumb,
                Uri.parse("andy_red.rcb"));
        gallery.addView(rose);


        GalleryItem andy = new GalleryItem(this,"house",R.drawable.blue_droid_thumb,
                Uri.parse("House.rcb"));
        gallery.addView(andy);

        GalleryItem cabin = new GalleryItem(this,"cabin",R.drawable.blue_droid_thumb,
                Uri.parse("Cabin.rcb"));
        gallery.addView(cabin);

        GalleryItem igloo = new GalleryItem(this,"igloo",R.drawable.igloo_thumb,
                Uri.parse("igloo.rcb"));
        gallery.addView(igloo);
    }

}
