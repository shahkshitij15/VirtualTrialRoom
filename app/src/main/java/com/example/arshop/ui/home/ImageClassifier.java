package com.example.arshop.ui.home;


import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import org.tensorflow.lite.Delegate;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Interpreter.Options;
import org.tensorflow.lite.gpu.GpuDelegate;

public abstract class ImageClassifier {
    private int[] intValues = null;
    @Nullable
    public Interpreter tflite;
    @Nullable
    public ByteBuffer imgData;
    @Nullable
    public float[][] mPrintPointArray;
    public final Activity activity;
    private final int imageSizeX;
    private final int imageSizeY;
    private final String modelPath;
    private static final String TAG = "TfLiteCameraDemo";
    private static final int RESULTS_TO_SHOW = 3;
    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_PIXEL_SIZE = 3;
    private static final int FILTER_STAGES = 3;
    private static final float FILTER_FACTOR = 0.4F;
//    public static final ImageClassifier.Companion Companion = new ImageClassifier.Companion(null);

    @Nullable
    protected final Interpreter getTflite() {
        return this.tflite;
    }

    protected final void setTflite(@Nullable Interpreter var1) {
        this.tflite = var1;
    }

    @Nullable
    protected final ByteBuffer getImgData() {
        return this.imgData;
    }

    protected final void setImgData(@Nullable ByteBuffer var1) {
        this.imgData = var1;
    }

    @Nullable
    public final float[][] getMPrintPointArray() {
        return this.mPrintPointArray;
    }

    public final void setMPrintPointArray(@Nullable float[][] var1) {
        this.mPrintPointArray = var1;
    }

    public final Activity getActivity() {
        return this.activity;
    }

    public final void initTflite(boolean useGPU) {
        Options tfliteOptions = new Options();
        tfliteOptions.setNumThreads(1);
        if (useGPU) {
            tfliteOptions.addDelegate((Delegate)(new GpuDelegate()));
        }

        try {
            this.tflite = new Interpreter((ByteBuffer)this.loadModelFile(getActivity()), tfliteOptions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final String classifyFrame(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, imageSizeX, imageSizeY, false);
        if (this.tflite == null) {
            Log.e("TfLiteCameraDemo", "Image classifier has not been initialized; Skipped.");
            return "Uninitialized Classifier.";
        } else {
            this.convertBitmapToByteBuffer(bitmap);
            long startTime = SystemClock.uptimeMillis();
            this.runInference();
            long endTime = SystemClock.uptimeMillis();
            Log.d("TfLiteCameraDemo", "Timecost to run model inference: " + Long.toString(endTime - startTime));
            bitmap.recycle();
            return Long.toString(endTime - startTime) + "ms";
        }
    }

    public final void close() {
        Interpreter var10000 = this.tflite;
        if (var10000 == null) {
        }

        var10000.close();
        this.tflite = (Interpreter)null;
    }

    private final MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        MappedByteBuffer var10000 = fileChannel.map(MapMode.READ_ONLY, startOffset, declaredLength);
        return var10000;
    }

    private final void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (this.imgData != null) {
            ByteBuffer var10000 = this.imgData;
            var10000.rewind();
            bitmap.getPixels(this.intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            int pixel = 0;
            long startTime = SystemClock.uptimeMillis();
            int var5 = 0;

            for(int var6 = this.imageSizeX; var5 < var6; ++var5) {
                int var7 = 0;

                for(int var8 = this.imageSizeY; var7 < var8; ++var7) {
                    int v = this.intValues[pixel++];
                    this.addPixelValue(v);
                }
            }

            long endTime = SystemClock.uptimeMillis();
            Log.d("TfLiteCameraDemo", "Timecost to put values into ByteBuffer: " + Long.toString(endTime - startTime));
        }
    }

    protected abstract void addPixelValue(int var1);

    protected abstract float getProbability(int var1);

    protected abstract void setProbability(int var1, Number var2);

    protected abstract float getNormalizedProbability(int var1);

    protected abstract void runInference();

    public final int getImageSizeX() {
        return this.imageSizeX;
    }

    public final int getImageSizeY() {
        return this.imageSizeY;
    }

    public ImageClassifier(Activity activity, int imageSizeX, int imageSizeY, String modelPath, int numBytesPerChannel) throws IOException {
        super();
        this.imageSizeX = imageSizeX;
        this.imageSizeY = imageSizeY;
        this.modelPath = modelPath;
        this.intValues = new int[this.imageSizeX * this.imageSizeY];
        this.activity = activity;
        this.imgData = ByteBuffer.allocateDirect(1 * this.imageSizeX * this.imageSizeY * 3 * numBytesPerChannel);
        ByteBuffer var10000 = this.imgData;
        if (var10000 == null) {
        }

        var10000.order(ByteOrder.nativeOrder());
        Log.d("TfLiteCameraDemo", "Created a Tensorflow Lite Image Classifier.");
    }
//    public static final class Companion {
//        private Companion() {
//        }
//
//        // $FF: synthetic method
//        public Companion(DefaultConstructorMarker $constructor_marker) {
//            this();
//        }
//    }
}
