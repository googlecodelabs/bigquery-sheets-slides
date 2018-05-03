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
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Renderer;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {
  private ArFragment fragment;
  private PointerDrawable pointer = new PointerDrawable();
  private boolean isTracking;
  private boolean isHitting;

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
    fragment = (ArFragment)
            getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);

    fragment.getArSceneView().getScene().setOnUpdateListener(frameTime -> {
      fragment.onUpdate(frameTime);
      onUpdate();
    });
    initializeGallery();
  }

  private void takePhoto() {
    final String filename = generateFilename();
    fragment.getArSceneView().getRenderer().captureScreenshot((image, width, height) -> {
      try {
        saveBitmapToDisk(filename, image, width, height);
      } catch (IllegalArgumentException | IOException ex) {
        Toast toast = Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_LONG);
        toast.show();
        return;
      }

      Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Photo saved", Snackbar.LENGTH_LONG);
      snackbar.setAction("Open in Photos", new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          File photoFile = new File(filename);

          Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                  MainActivity.this.getPackageName() + ".ar.codelab.name.provider",
                  photoFile);
          Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
          intent.setDataAndType(photoURI, "image/*");
          intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
          startActivity(intent);

        }
      });
      snackbar.show();
    });
  }

  private void saveBitmapToDisk(String filename, ByteBuffer imageData,
                                int width, int height) throws IOException {
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    if (bitmap == null) {
      throw new IllegalArgumentException(
              "Failed to create a bitmap: " + width + " x " + height + ".");
    }

    bitmap.copyPixelsFromBuffer(imageData);
    File out = new File(filename);
    if (!out.getParentFile().exists()) {
      out.getParentFile().mkdirs();
    }
    try (FileOutputStream outputStream = new FileOutputStream(filename);
         ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputData);
      outputData.writeTo(outputStream);
      outputStream.flush();
      outputStream.close();
    } catch (IOException ex) {
      throw new IOException("Failed to save bitmap to disk", ex);
    }
  }

  private String generateFilename() {
    String date =
            new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
    return Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES) + File.separator + "Sceneform/" + date + "_screenshot.jpg";
  }


  private void onUpdate() {
    boolean trackingChanged = updateTracking();
    View contentView = findViewById(android.R.id.content);
    if (trackingChanged) {
      if (isTracking) {
        contentView.getOverlay().add(pointer);
      } else {
        contentView.getOverlay().remove(pointer);
      }
      contentView.invalidate();
    }

    if (isTracking) {
      boolean hitTestChanged = updateHitTest();
      if (hitTestChanged) {
        pointer.setEnabled(isHitting);
        findViewById(android.R.id.content).invalidate();
      }
    }
  }

  private boolean updateHitTest() {
    Frame frame = fragment.getArSceneView().getArFrame();
    Point pt = getScreenCenter();
    List<HitResult> hits;
    boolean wasHitting = isHitting;
    isHitting = false;
    if (frame != null) {
      hits = frame.hitTest(pt.x, pt.y);
      for (HitResult hit : hits) {
        Trackable trackable = hit.getTrackable();
        if ((trackable instanceof Plane &&
                ((Plane) trackable).isPoseInPolygon(hit.getHitPose()))) {
          isHitting = true;
          break;
        }
      }
    }
    return wasHitting != isHitting;
  }

  private Point getScreenCenter() {
    View vw = findViewById(android.R.id.content);
    return new Point(vw.getWidth()/2, vw.getHeight()/2);
  }


  private boolean updateTracking() {
    Frame frame = fragment.getArSceneView().getArFrame();
    boolean wasTracking = isTracking;
    isTracking = frame.getCamera().getTrackingState() == TrackingState.TRACKING;
    return isTracking != wasTracking;
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
    LinearLayout gallery = findViewById(R.id.gallery_layout);

    ImageView andy = new ImageView(this);
    andy.setImageResource(R.drawable.droid_thumb);
    andy.setContentDescription("andy");
    andy.setOnClickListener(view ->{addObject("andy", Uri.parse("andy.rcb"));});
    gallery.addView(andy);

    ImageView cabin = new ImageView(this);
    cabin.setImageResource(R.drawable.cabin_thumb);
    cabin.setContentDescription("cabin");
    cabin.setOnClickListener(view ->{addObject("cabin",Uri.parse("Cabin.rcb"));});
    gallery.addView(cabin);

    ImageView house = new ImageView(this);
    house.setImageResource(R.drawable.house_thumb);
    house.setContentDescription("house");
    house.setOnClickListener(view ->{addObject("house",Uri.parse("House.rcb"));});
    gallery.addView(house);

    ImageView igloo = new ImageView(this);
    igloo.setImageResource(R.drawable.igloo_thumb);
    igloo.setContentDescription("igloo");
    igloo.setOnClickListener(view ->{addObject("igloo",Uri.parse("igloo.rcb"));});
    gallery.addView(igloo);
  }

  private void addObject(String name, Uri model) {
    Frame frame = fragment.getArSceneView().getArFrame();
    Point pt = getScreenCenter();
    List<HitResult> hits;
    if (frame != null) {
      hits = frame.hitTest(pt.x, pt.y);
      for (HitResult hit : hits) {
        Trackable trackable = hit.getTrackable();
        if ((trackable instanceof Plane &&
                ((Plane) trackable).isPoseInPolygon(hit.getHitPose()))) {
          placeObject(fragment, hit.createAnchor(), name, model);
          break;
        }
      }
    }
  }

  private void placeObject(ArFragment fragment, Anchor anchor, String name, Uri model) {
    Renderer renderer = fragment.getArSceneView().getRenderer();
    Scene scene = fragment.getArSceneView().getScene();
    CompletableFuture<Void> renderableFuture =
            ModelRenderable.builder()
                    .setSource(fragment.getContext(), URI.create(model.toString()))
                    .setRegistryId(name)
                    .build()
                    .thenAccept(renderable -> addNodeToScene(fragment, anchor, renderable))
                    .exceptionally((throwable -> {
                      AlertDialog.Builder builder = new AlertDialog.Builder(this);
                      builder.setMessage(throwable.getMessage())
                              .setTitle("Codelab error!");
                      AlertDialog dialog = builder.create();
                      dialog.show();
                      return null;
                    }));

  }
  private void addNodeToScene(ArFragment fragment, Anchor anchor, Renderable renderable) {
    AnchorNode anchorNode = new AnchorNode(anchor);
    TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
    node.setRenderable(renderable);
    node.setParent(anchorNode);
    fragment.getArSceneView().getScene().addChild(anchorNode);
    node.select();
  }

}
