package com.google.firebase.codelab.mlkit_custommodel;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "MainActivity";
    private Button mRun;
    private ImageView mImageView;
    private Bitmap mSelectedImage;
    private GraphicOverlay mGraphicOverlay;
    // Max width (portrait mode)
    private Integer mImageMaxWidth;
    // Max height (portrait mode)
    private Integer mImageMaxHeight;
    private boolean mIsLandScape;

    /** Name of the model file hosted with Firebase. */
    private static final String HOSTED_MODEL_NAME = "mobilenet_v1_1.0_224_quant.tflite";

    /** Name of the label file stored in Assets. */
    private static final String LABEL_PATH = "labels.txt";

    /** Number of results to show in the UI. */
    private static final int RESULTS_TO_SHOW = 3;

    /** Dimensions of inputs. */
    private static final int DIM_BATCH_SIZE = 1;

    private static final int DIM_PIXEL_SIZE = 3;

    private static final int DIM_IMG_SIZE_X = 224;
    private static final int DIM_IMG_SIZE_Y = 224;

    /** Labels corresponding to the output of the vision model. */
    private List<String> mLabelList;

    private final PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });
    /** An instance of the driver class to run model inference with Firebase. */
    private FirebaseModelInterpreter mInterpreter;

    /** Data configuration of input & output data of model. */
    private FirebaseModelInputOutputOptions mDataOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGraphicOverlay = findViewById(R.id.graphic_overlay);
        mImageView = findViewById(R.id.image_view);

        Spinner dropdown = findViewById(R.id.spinner);
        String[] items = new String[]{"Image 1", "Image 2", "Image 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        FirebaseModelOptions modelOptions =
                new FirebaseModelOptions.Builder()
                        .setCloudModelName("mobilenet_v1")
                        .build();
        int[] inputDims = {DIM_BATCH_SIZE, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, DIM_PIXEL_SIZE};
        int[] outputDims = {1, mLabelList.size()};
        try {
            mInterpreter = FirebaseModelInterpreter.getInstance(modelOptions);
            mDataOptions =
                    new FirebaseModelInputOutputOptions.Builder()
                            .setInputFormat(0, FirebaseModelDataType.BYTE, inputDims)
                            .setOutputFormat(0, FirebaseModelDataType.BYTE, outputDims)
                            .build();
        } catch (FirebaseMLException e) {
            showToast("Error while setting up the model");
            e.printStackTrace();
        }
        mLabelList = loadLabelList(this);

//        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
//                .requireWifi()
//                .build();
//
//        FirebaseCloudModelSource cloudSource = new FirebaseCloudModelSource.Builder()
//                .setModelName("my_model")  // Specify the name you assigned the model in the console
//                .enableModelUpdates(true)
//                .setInitialDownloadConditions(conditions)
//                .setUpdatesDownloadConditions(conditions)  // You could also specify different conditions
//                // for updates
//                .build();
        mRun = findViewById(R.id.button_run);
        mRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runModelInference();
            }
        });
    }

    private void runModelInference() {

    }

    /** Reads label list from Assets. */
    private List<String> loadLabelList(Activity activity) {
        List<String> labelList = new ArrayList<>();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(activity.getAssets().open(LABEL_PATH)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                labelList.add(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to read label list.", e);
        }
        return labelList;
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        mGraphicOverlay.clear();
        switch (position) {
            case 0:
                mSelectedImage = getBitmapFromAsset(this, "Please_walk_on_the_grass.jpg");

                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                mSelectedImage = getBitmapFromAsset(this, "non-latin.jpg");
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                mSelectedImage = getBitmapFromAsset(this, "nl2.jpg");
                break;
        }
        if (mSelectedImage != null) {
            // Get the dimensions of the View
            Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

            int targetWidth = targetedSize.first;
            int maxHeight = targetedSize.second;

            // Determine how much to scale down the image
            float scaleFactor =
                    Math.max(
                            (float) mSelectedImage.getWidth() / (float) targetWidth,
                            (float) mSelectedImage.getHeight() / (float) maxHeight);

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            mSelectedImage,
                            (int) (mSelectedImage.getWidth() / scaleFactor),
                            (int) (mSelectedImage.getHeight() / scaleFactor),
                            true);

            mImageView.setImageBitmap(resizedBitmap);
            mSelectedImage = resizedBitmap;
        }
    }

    // Functions for loading images from app assets.

    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxWidth() {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to wait for
            // a UI layout pass to get the right values. So delay it to first time image rendering time.
            if (mIsLandScape) {
                mImageMaxWidth =
                        mImageView.getHeight();
            } else {
                mImageMaxWidth = mImageView.getWidth();
            }
        }

        return mImageMaxWidth;
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxHeight() {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to wait for
            // a UI layout pass to get the right values. So delay it to first time image rendering time.
            if (mIsLandScape) {
                mImageMaxHeight = mImageView.getWidth();
            } else {
                mImageMaxHeight =
                        mImageView.getHeight();
            }
        }

        return mImageMaxHeight;
    }
    // Gets the targeted width / height.
    private Pair<Integer, Integer> getTargetedWidthHeight() {
        int targetWidth;
        int targetHeight;
        int maxWidthForPortraitMode = getImageMaxWidth();
        int maxHeightForPortraitMode = getImageMaxHeight();
        targetWidth = mIsLandScape ? maxHeightForPortraitMode : maxWidthForPortraitMode;
        targetHeight = mIsLandScape ? maxWidthForPortraitMode : maxHeightForPortraitMode;
        return new Pair<>(targetWidth, targetHeight);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream is;
        Bitmap bitmap = null;
        try {
            is = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
