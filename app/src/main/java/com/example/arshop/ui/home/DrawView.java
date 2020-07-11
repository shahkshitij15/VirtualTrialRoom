package com.example.arshop.ui.home;

/*
 * Copyright 2018 Zihua Zeng (edvard_hua@live.com), Lang Feng (tearjeaker@hotmail.com)
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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.arshop.ui.category.Cloth;

import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


public class DrawView extends View {

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    private List<PointF> mDrawPoint = new ArrayList<>();
    private int mWidth = 0;
    private int mHeight = 0;
    private float mRatioX = 0;
    private float mRatioY = 0;
    private int mImgWidth = 0;
    private int mImgHeight = 0;
    private final static String TAG = "DrawView";
    private Cloth selectedCloth;

    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    public void setImgSize(int width, int height) {
        mImgWidth = width;
        mImgHeight = height;
        requestLayout();
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                mWidth = width;
                mHeight = width * mRatioHeight / mRatioWidth;
            } else {
                mWidth = height * mRatioWidth / mRatioHeight;
                mHeight = height;
            }
        }

        setMeasuredDimension(mWidth, mHeight);

        mRatioX = (float) mImgWidth / mWidth;
        mRatioY = (float) mImgHeight / mHeight;
    }

    public void setDrawPoint(float[][] point, float ratio) {
        mDrawPoint.clear();
        Float tempX;
        Float tempY;
        for (int i = 0; i < 13; i++) {
            tempX = point[0][i] / ratio / mRatioX;
            tempY = point[1][i] / ratio / mRatioY;
            this.mDrawPoint.add(new PointF(tempX, tempY));
        }
    }

    public void setSelectedWearable(Cloth cloth) {
        this.selectedCloth = cloth;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawPoint.isEmpty()) return;
        if (selectedCloth == null) return;
        Bitmap wearable = selectedCloth.getImage();
        Mat wearable_mat = new Mat();
        if (wearable == null) return;
        Utils.bitmapToMat(wearable, wearable_mat);
        org.opencv.core.Point top_left_pt = new org.opencv.core.Point(mDrawPoint.get(2).x, mDrawPoint.get(2).y);
        org.opencv.core.Point top_right_pt = new org.opencv.core.Point(mDrawPoint.get(5).x, mDrawPoint.get(5).y);
        org.opencv.core.Point bottom_left_pt = new org.opencv.core.Point(mDrawPoint.get(8).x, mDrawPoint.get(8).y);
        org.opencv.core.Point bottom_right_pt = new org.opencv.core.Point(mDrawPoint.get(11).x, mDrawPoint.get(11).y);

        MatOfPoint2f personPoints = new MatOfPoint2f(top_left_pt, bottom_right_pt, bottom_left_pt, top_right_pt);
        //MatOfPoint2f personPoints = new MatOfPoint2f(top_left_pt, top_right_pt, bottom_left_pt);


        Mat homography = Calib3d.findHomography(selectedCloth.getPoints(), personPoints);
        //Mat affine = Imgproc.getAffineTransform(selectedCloth.getPoints(), personPoints);
        if (!homography.empty()) {
            Mat wearable_transformed = new Mat();

            Utils.bitmapToMat(wearable, wearable_mat);
            Imgproc.warpPerspective(wearable_mat, wearable_transformed, homography, new org.opencv.core.Size(canvas.getWidth(), canvas.getHeight()));

            Bitmap wearable_new = Bitmap.createBitmap(wearable_transformed.width(), wearable_transformed.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(wearable_transformed, wearable_new);
            canvas.drawBitmap(wearable_new, 0.0f, 0.0f, null);
        }
    }
}
