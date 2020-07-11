package com.example.arshop.ui.category;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arshop.ClothesViewModel;
import com.example.arshop.R;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.jaredrummler.materialspinner.MaterialSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class CategoryFragment extends Fragment {

    private List<Cloth> clothes = new ArrayList<>();
    private ClothAdapter clothAdapter;
    private ClothesViewModel clothesViewModel;
    private String gender = "All",
            category = "All",
            color = "All",
            brand = "All";
    private ArrayList<Cloth> filtered = new ArrayList<>();

    private final static String TAG = "CategoryFragment";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clothesViewModel = ViewModelProviders.of(getActivity()).get(ClothesViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_category, container, false);
        clothAdapter = new ClothAdapter(getContext(), clothes);
        clothAdapter.setClothesViewModel(clothesViewModel);
        clothesViewModel.setAdapter(clothAdapter);
        clothesViewModel.getClothList().observe(this, new Observer<ArrayList<Cloth>>() {
            @Override
            public void onChanged(ArrayList<Cloth> cloths) {
                clothAdapter.notifyDataSetChanged();
            }
        });
        root.post(
                new Runnable() {
                    @Override
                    public void run() {
                        setUpSpinners(root);
                    }
                });
        root.post(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
                recyclerView.setAdapter(clothAdapter);
            }
        });
        return root;
    }

    public void setUpSpinners(View root) {
        MaterialSpinner gender_spinner = root.findViewById(R.id.gender_spinner);
        gender_spinner.setItems("All", "Male", "Female");
        MaterialSpinner category_spinner = root.findViewById(R.id.category_spinner);
        category_spinner.setItems("All", "Casuals", "Informal", "Party", "Beach");
        MaterialSpinner brand_spinner = root.findViewById(R.id.brand_spinner);
        brand_spinner.setItems("All", "Jack-and-Jones", "Ed-Hardy", "Forever-21", "Zara", "Biba", "H&M");
        MaterialSpinner color_spinner = root.findViewById(R.id.color_spinner);
        color_spinner.setItems("All", "Blue", "Blue-Stripped", "Green-Floral", "Red-Floral", "Red-Stripped", "Black");
        MaterialSpinner.OnItemSelectedListener filter = new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (clothesViewModel.getAllClothes() == null) return;
                filtered.clear();
                filtered.addAll(clothesViewModel.getAllClothes());
                if (view.getId() == R.id.gender_spinner) {
                    gender = (String) item;
                } else if (view.getId() == R.id.category_spinner) {
                    category = (String) item;
                } else if (view.getId() == R.id.color_spinner) {
                    color = (String) item;
                } else if (view.getId() == R.id.brand_spinner) {
                    brand = (String) item;
                }
                genderFilter(filtered, gender);
                categoryFilter(filtered, category);
                colorFilter(filtered, color);
                brandFilter(filtered, brand);
                clothAdapter.getClothList().clear();
                clothAdapter.getClothList().addAll(filtered);
                clothAdapter.notifyDataSetChanged();
            }
        };
        gender_spinner.setOnItemSelectedListener(filter);
        category_spinner.setOnItemSelectedListener(filter);
        color_spinner.setOnItemSelectedListener(filter);
        brand_spinner.setOnItemSelectedListener(filter);
    }

    public void categoryFilter(ArrayList<Cloth> clothes, String selected_category) {
        if (selected_category.equals("All")) {
            return;
        }
        ListIterator<Cloth> it = clothes.listIterator();
        while (it.hasNext()) {
            Cloth cloth = it.next();
            int i = 0;
            for (String category : cloth.getCategories()) {
                if (category.equals(selected_category)) {
                    break;
                } else {
                    i++;
                }
            }
            if (i == cloth.getCategories().length) {
                it.remove();
            }
        }
    }

    public void colorFilter(ArrayList<Cloth> clothes, String color) {
        if (color.equals("All")) {
            return;
        }
        ListIterator<Cloth> it = clothes.listIterator();
        while (it.hasNext()) {
            Cloth cloth = it.next();
            if (!cloth.getColor().equals(color))
                it.remove();
        }
    }

    public void brandFilter(ArrayList<Cloth> clothes, String brand) {
        if (brand.equals("All")) {
            return;
        }
        ListIterator<Cloth> it = clothes.listIterator();
        while (it.hasNext()) {
            Cloth cloth = it.next();
            if (!cloth.getBrand().equals(brand))
                it.remove();
        }
    }

    public void genderFilter(ArrayList<Cloth> clothes, String gender) {
        if (gender.equals("All")) {
            return;
        }
        ListIterator<Cloth> it = clothes.listIterator();
        while (it.hasNext()) {
            Cloth cloth = it.next();
            if (!cloth.getGender().equals(gender))
                it.remove();
        }
    }

    /**
     * Adding few albums for testing
     */


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
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

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
