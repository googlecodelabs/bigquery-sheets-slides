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

package com.google.firebase.codelab.awesomedrawingquiz.view;

import com.google.firebase.codelab.awesomedrawingquiz.data.Drawing;
import com.google.firebase.codelab.awesomedrawingquiz.data.Stroke;
import com.google.gson.Gson;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.List;

public final class QuickDrawView extends View {

    private static final String TAG = "QuickDrawView";

    private static final float PADDING_DP = 48.f;

    private final Gson gson = new Gson();

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Path path = new Path();

    private final Matrix matrix = new Matrix();

    private final RectF originalRect = new RectF(0, 0, 200, 200);

    private RectF targetRect = new RectF(0, 0, 200, 200);

    public QuickDrawView(Context context) {
        this(context, null);
    }

    public QuickDrawView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1.f,
                context.getResources().getDisplayMetrics()));
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PADDING_DP,
                getContext().getResources().getDisplayMetrics());
        targetRect.set(padding, padding, w - padding * 2, h - padding * 2);
        matrix.setRectToRect(originalRect, targetRect, Matrix.ScaleToFit.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                resolveSize(200, widthMeasureSpec),
                resolveSize(200, heightMeasureSpec));
    }

    public void setDrawing(@NonNull Drawing drawing) {
        Log.d(TAG, "Drawing set to " + drawing.getWord());
        if (!path.isEmpty()) {
            path.reset();
        }

        List<Stroke> strokes = Stroke.parseStrokes(gson, drawing);

        for (Stroke stroke : strokes) {
            path.moveTo(stroke.x[0], stroke.y[0]);

            int coordsLength = stroke.x.length;
            for (int i = 1; i < coordsLength; i++) {
                path.lineTo(stroke.x[i], stroke.y[i]);
            }
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.setMatrix(matrix);
        canvas.drawPath(path, paint);
        canvas.restore();
    }
}
