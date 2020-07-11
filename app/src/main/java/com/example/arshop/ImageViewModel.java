package com.example.arshop;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.arshop.ui.gallery.ImageAdapter;
import com.google.firebase.database.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class ImageViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Pair>> sendImages;
    private ImageAdapter adapter;
    public File directory;
    private File[] files;

    public void setAdapter(ImageAdapter adapter) {
        this.adapter = adapter;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public MutableLiveData<ArrayList<Pair>> getImages() {
        files = directory.listFiles();
        if (sendImages == null) {
            sendImages = new MutableLiveData<>();
            prepareImages();
        }
        else if (sendImages.getValue().size() != files.length) {
            prepareImages();
        } else {
            adapter.addAllImages(sendImages.getValue());
        }
        return sendImages;
    }

    private void prepareImages() {
        if (files.length == 0) return;
        for (int i = adapter.getImageList().size(); i < files.length; i++) {
            adapter.addImage(files[i].getPath());
            adapter.notifyItemChanged(i);
        }
        sendImages.setValue((ArrayList<Pair>) adapter.getImageList());
    }


}