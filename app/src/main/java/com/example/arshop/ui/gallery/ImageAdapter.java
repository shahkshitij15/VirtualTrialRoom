package com.example.arshop.ui.gallery;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.ExifInterface;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.arshop.R;
import com.google.android.material.button.MaterialButton;

import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<Pair> imageList;
    private Context context;
    private ArrayList<String> deleteImages = new ArrayList<>();
    private static String clickState;
    RelativeLayout layout;
    MaterialButton deleteButton;

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        ImageViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
        }
    }

    public ImageAdapter(Context context, List<Pair> imageList) {
        this.imageList = imageList;
        this.context = context;
    }

    public void addImage(String imagePath) {
        Pair pair = new Pair(imagePath, getCameraPhotoOrientation(imagePath));
        this.imageList.add(pair);
    }

    public void addAllImages(List<Pair> imageList) {
        this.imageList.addAll(imageList);
    }

    public List<Pair> getImageList() {
        return this.imageList;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_layout, parent, false);
        deleteButton = new MaterialButton(context);
        deleteButton.setText("Delete Selected");
        deleteButton.setTextColor(Color.WHITE);
        deleteButton.setBackgroundColor(Color.BLACK);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(String imagePath: deleteImages)
                {
                    new File(imagePath).delete();
                }
                Navigation.findNavController(v).navigate(R.id.nav_gallery);
            }
        });
        clickState = "VIEW_IMAGE";
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final Pair ImagePair = imageList.get(position);
        Glide.with(context).load(ImagePair.first).asBitmap().into(holder.image);
        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clickState = "MULTI_SELECT";
                if(layout == null)
                    layout = (RelativeLayout) holder.itemView.getParent().getParent();
                layout.addView(deleteButton);
                holder.itemView.setBackgroundColor(Color.DKGRAY);
                holder.itemView.setSelected(!holder.itemView.isSelected());
                deleteImages.add((String) imageList.get(position).first);
                return true;
            }
        });
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (clickState.equals("MULTI_SELECT")) {
                    holder.itemView.setSelected(!holder.itemView.isSelected());
                    if (holder.itemView.isSelected()) {
                        holder.itemView.setBackgroundColor(Color.DKGRAY);
                        deleteImages.add((String) imageList.get(position).first);
                    } else {
                        holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                        deleteImages.remove(imageList.get(holder.getLayoutPosition()).first);
                        if (deleteImages.size() == 0) {
                            clickState = "VIEW_IMAGE";
                            layout.removeView(deleteButton);
                        }
                    }
                } else if (clickState.equals("VIEW_IMAGE")) {
                    final ImageView imageView = (ImageView) LayoutInflater.from(v.getContext()).inflate(R.layout.image_layout
                            , null);
                    final Dialog imageDialog = new Dialog(v.getContext(), R.style.translucentDialog);
                    imageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    imageDialog.setContentView(imageView);
                    Glide.with(context).load(ImagePair.first).asBitmap().into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            imageView.setImageBitmap(resource);
                            imageDialog.show();
                        }
                    });
                }
            }
        });
    }

    private static int getCameraPhotoOrientation(String imagePath) {
        int rotate = 0;
        try {
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imagePath);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 90;
                    break;
                default:
                    rotate = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rotate;
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }


}
