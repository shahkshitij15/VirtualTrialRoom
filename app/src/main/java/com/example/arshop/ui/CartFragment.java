package com.example.arshop.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.arshop.ClothesViewModel;
import com.example.arshop.R;
import com.example.arshop.ui.category.Cloth;
import com.example.arshop.ui.category.ClothAdapter;
import com.google.api.Distribution;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CartFragment extends Fragment {
    private ClothesViewModel clothesViewModel;
    private CartAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        final ListView cart = root.findViewById(R.id.cart_list);
        clothesViewModel = ViewModelProviders.of(getActivity()).get(ClothesViewModel.class);
        adapter = new CartAdapter(getContext(), clothesViewModel.getCartClothes());
        cart.setAdapter(adapter);
        return root;
    }
}

class CartAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Cloth> cloths;
    private Button increase;
    private Button decrease;
    public CartAdapter(Context context, ArrayList<Cloth> cloths) {
        this.context = context;
        this.cloths = cloths;
    }
    public CartAdapter getAdapter(){
        return this;
    }

    @Override
    public int getCount() {
        return cloths.size();
    }

    @Override
    public Object getItem(int position) {
        return cloths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void setUpButtons()
    {
        increase = new Button(context);
        increase.setLayoutParams(new RelativeLayout.LayoutParams(100, 100));
        RelativeLayout.LayoutParams iparams = (RelativeLayout.LayoutParams) increase.getLayoutParams();
        iparams.topMargin = 400;
        increase.setText("+");
        increase.setBackgroundColor(Color.GREEN);
        decrease = new Button(context);
        decrease.setLayoutParams(new RelativeLayout.LayoutParams(100, 100));
        RelativeLayout.LayoutParams dparams = (RelativeLayout.LayoutParams) decrease.getLayoutParams();
        dparams.leftMargin = 950;
        dparams.topMargin = 400;
        decrease.setBackgroundColor(Color.RED);
        decrease.setText("-");
    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        Cloth cloth = cloths.get(position);
        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cards_layout, parent, false);
        }
        TextView titleText =  view.findViewById(R.id.title);
        ImageView imageView =  view.findViewById(R.id.image);
        final TextView count = view.findViewById(R.id.count);
        view.findViewById(R.id.overflow).setVisibility(View.GONE);
        titleText.setText(cloth.getName());
        imageView.setImageBitmap(cloth.getImage());
        count.setText("1");
        RelativeLayout layout = (RelativeLayout) titleText.getParent();
        setUpButtons();
        layout.addView(increase);
        layout.addView(decrease);
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count.setText(String.valueOf(Integer.valueOf(String.valueOf(count.getText())) + 1));
            }
        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current_count = Integer.valueOf(String.valueOf(count.getText()));
                if(current_count - 1 == 0)
                {
                    cloths.remove(position);
                    getAdapter().notifyDataSetChanged();
                    return;
                }
                count.setText(String.valueOf(current_count - 1));
            }
        });
        return view;

    }

    ;
}
