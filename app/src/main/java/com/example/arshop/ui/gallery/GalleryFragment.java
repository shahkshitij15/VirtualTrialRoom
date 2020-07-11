package com.example.arshop.ui.gallery;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arshop.ImageViewModel;
import com.example.arshop.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Pair> images= new ArrayList<>();
    private ImageAdapter adapter;
    private final static String TAG = "GalleryFragment";
    private ImageViewModel imageViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        imageViewModel = ViewModelProviders.of(getActivity()).get(ImageViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        adapter = new ImageAdapter(getContext(), images);
        imageViewModel.setAdapter(adapter);
        if(imageViewModel.directory == null)
            imageViewModel.setDirectory(new File(getContext().getExternalFilesDir(null), "/images/"));
        imageViewModel.getImages().observe(this, new Observer<ArrayList<Pair>>() {
            @Override
            public void onChanged(ArrayList<Pair> strings) {
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView = root.findViewById(R.id.image_gallery);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GalleryFragment.GridSpacingItemDecoration(2, dpToPx(5), true));
        recyclerView.setAdapter(adapter);
        return root;
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}