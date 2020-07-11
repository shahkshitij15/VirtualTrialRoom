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

import android.app.Activity;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

/**
 * Pose Estimator
 */
public class ImageClassifierFloatInception extends ImageClassifier {

    private float[][][][] heatMapArray;
    private int outputH = 0;
    private int outputW = 0;

    ImageClassifierFloatInception(Activity activity, int imageSizeX, int imageSizeY, int outputW, int outputH, String modelPath, int numBytesPerChannel) throws IOException {
        super(activity, imageSizeX, imageSizeY, modelPath, numBytesPerChannel);
        this.outputH = outputH;
        this.outputW = outputW;
        heatMapArray = new float[1][outputW][outputH][14];
    }

    /**
     * An array to hold inference results, to be feed into Tensorflow Lite as outputs.
     * This isn't part of the super class, because we need a primitive array here.
     */

    private Mat mMat = null;

    private Float get(int x, int y, float[] arr) {
        if (x < 0 || y < 0 || x >= outputW || y >= outputH)
            return -1f;
        else
            return arr[x * outputW + y];
    }

    public static ImageClassifierFloatInception create(Activity activity) throws IOException {

        int imageSizeX = 192;
        int imageSizeY = 192;
        int outputW = 96;
        int outputH = 96;
        String modelPath = "model.tflite";
        int numBytesPerChannel = 4;
        return new ImageClassifierFloatInception(activity, imageSizeX, imageSizeY, outputW, outputH, modelPath, numBytesPerChannel);
    }


    @Override
    protected void addPixelValue(int pixelValue) {
        imgData.putFloat((pixelValue & 0xFF));
        imgData.putFloat((pixelValue >> 8 & 0xFF));
        imgData.putFloat((pixelValue >> 16 & 0xFF));
    }

    @Override
    protected float getProbability(int var1) {
        return 0;
    }

    @Override
    protected void setProbability(int var1, Number var2) {

    }

    @Override
    protected float getNormalizedProbability(int var1) {
        return 0;
    }

    @Override
    protected void runInference() {
        tflite.run(imgData, heatMapArray);

        if (mPrintPointArray == null)
            mPrintPointArray = new float[2][14];


        // Gaussian Filter 5*5
        if (mMat == null)
            mMat = new Mat(outputW, outputH, CvType.CV_32F);

        float[] tempArray = new float[outputW * outputH];
        float[] outTempArray = new float[outputW * outputH];
        for (int i = 0; i < 13; i++) {
            int index = 0, x = 0;
            while (x < outputW) {
                int y = 0;
                while (y < outputH) {
                    tempArray[index] = heatMapArray[0][y][x][i];
                    index++;
                    y++;
                }
                x++;
            }

            mMat.put(0, 0, tempArray);
            Imgproc.GaussianBlur(mMat, mMat, new Size(5.0, 5.0), 0.0, 0.0);
            mMat.get(0, 0, outTempArray);

            float maxX = 0f;
            float maxY = 0f;
            float max = 0f;

            // Find keypoint coordinate through maximum values
            for (x = 0; x < outputW; x++) {
                for (int y = 0; y < outputH; y++) {
                    float center = get(x, y, outTempArray);
                    if (center > max) {
                        max = center;
                        maxX = x;
                        maxY = y;
                    }
                }
            }

            if (max == 0f) {
                mPrintPointArray = new float[2][14];
                return;
            }

            mPrintPointArray[0][i] = maxX;
            mPrintPointArray[1][i] = maxY;
        }
    }
}