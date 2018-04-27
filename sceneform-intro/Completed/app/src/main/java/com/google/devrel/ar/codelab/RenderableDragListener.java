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
import android.content.ClipDescription;
import android.net.Uri;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Renderer;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.google.devrel.ar.codelab.GalleryItem.RENDERABLE;

public class RenderableDragListener implements View.OnDragListener {

    private final ArFragment fragment;
    private Renderable renderable;

    public RenderableDragListener(ArFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        // Defines a variable to store the action type for the incoming event
        final int action = dragEvent.getAction();

        // Handles each of the expected events
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED: {
                ClipData data = dragEvent.getClipData();
                // Determines if this View can accept the dragged data
                if (dragEvent.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST)
                        && dragEvent.getClipDescription().getLabel().equals(RENDERABLE)) {

                    // returns true to indicate that the View can accept the dragged data.
                    return true;

                }

                // Returns false. During the current drag and drop operation, this View will
                // not receive events again until ACTION_DRAG_ENDED is sent.
                return false;
            }
            case DragEvent.ACTION_DRAG_ENTERED: {
                PersistableBundle bundle = dragEvent.getClipDescription().getExtras();
                if (bundle != null && bundle.containsKey(GalleryItem.KeyUri)) {
                    Uri uri = Uri.parse(bundle.getString(GalleryItem.KeyUri));
                    String name = bundle.getString(GalleryItem.KeyName);
                    if (uri != null && name != null) {
                        Renderer renderer = fragment.getArSceneView().getRenderer();
                        CompletableFuture<Void> renderableFuture =
                                ModelRenderable.builder()
                                        .setSource(fragment.getContext(), URI.create(uri.toString()))
                                        .setRegistryId(name)
                                        .build()
                                        .thenAccept(renderable -> RenderableDragListener.this.renderable = renderable)
                                        .exceptionally((throwable -> {
                                            return null;
                                        }));

                    }
                }

                return true;
            }
            case DragEvent.ACTION_DRAG_LOCATION:
            {
                Frame frame = fragment.getArSceneView().getArFrame();
                List<HitResult> hits;
                if (frame != null) {
                    hits = frame.hitTest(dragEvent.getX(), dragEvent.getY());
                    for (HitResult hit : hits) {
                        Trackable trackable = hit.getTrackable();
                        if ((trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose()))) {
                            return true;
                        }
                    }
                }

                return false;
            }

            case DragEvent.ACTION_DROP:
            {
                String name = "";

                PersistableBundle bundle = dragEvent.getClipDescription().getExtras();
                if (bundle != null && bundle.containsKey(GalleryItem.KeyUri)) {
                    // Figure out Uri or URI
                    name = bundle.getString(GalleryItem.KeyName);
                }

                final Renderable droppedRenderable = renderable;
                final String itemName = name;
                Frame frame = fragment.getArSceneView().getArFrame();
                if (frame != null) {
                    List<HitResult> hits = frame.hitTest(dragEvent.getX(), dragEvent.getY());
                    for (HitResult hit : hits) {
                        Trackable trackable = hit.getTrackable();
                        if ((trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose()))) {
                            Anchor anchor = hit.createAnchor();
                            AnchorNode anchorNode = new AnchorNode(anchor);

                             TransformableNode  node =
                             new TransformableNode(fragment.getTransformationSystem());
                            anchorNode.addChild(node);
                            node.setRenderable(droppedRenderable);

                            // add the node .5 up so the model is on the plane, not in the middle
                            node.setName("Dropped " + itemName);

                               node.select();
                            fragment.getArSceneView().getScene().addChild(anchorNode);

                            return true;

                        }
                    }
                }
                return false;
            }

        }
        return false;
    }
}
