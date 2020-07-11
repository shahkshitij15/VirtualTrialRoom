package com.example.arshop.ui.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.arshop.ClothesViewModel;
import com.example.arshop.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class ClothAdapter extends RecyclerView.Adapter<ClothAdapter.ClothViewHolder> {

    final public Context mContext;
    private List<Cloth> clothList;
    private ClothViewHolder selectedHolder;
    private ClothesViewModel clothesViewModel;
    private Animation pulse;
    private Animation reversePulse;

    public class ClothViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView image, overflow;

        ClothViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            image = (ImageView) view.findViewById(R.id.image);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }


    public ClothAdapter(Context mContext, List<Cloth> clothList) {
        this.mContext = mContext;
        this.clothList = clothList;
    }

    public void setClothesViewModel(ClothesViewModel clothesViewModel) {
        this.clothesViewModel = clothesViewModel;
    }

    public void addClothes(Cloth cloth) {
        this.clothList.add(cloth);
    }

    public void addAllClothes(ArrayList<Cloth> clothList) {
        if(clothList!=null)
            this.clothList.addAll(clothList);
    }

    public List<Cloth> getClothList() {
        return this.clothList;
    }

    public Cloth getCloth(int index) {
        return this.clothList.get(index);
    }

    @Override
    public ClothViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);
        pulse = AnimationUtils.loadAnimation(this.mContext, R.anim.pulse);
        reversePulse = AnimationUtils.loadAnimation(this.mContext, R.anim.pulse);
        pulse.setDuration(500);
        reversePulse.setDuration(300);
        pulse.setFillAfter(true);
        reversePulse.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return Math.abs(input - 1f);
            }
        });
        reversePulse.setFillAfter(true);
        return new ClothViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ClothViewHolder holder, final int position) {
        final Cloth cloth = clothList.get(position);
        holder.title.setText(cloth.getBrand());
        holder.count.setText(cloth.getCategory(0));
        if (cloth.getImage() != null) {
            holder.image.setImageBitmap(cloth.getImage());
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedHolder != null) {
                    selectedHolder.image.startAnimation(reversePulse);
                    selectedHolder.itemView.setSelected(false);
                }
                selectedHolder = holder;
                clothesViewModel.setSelectedWearable(getCloth(holder.getLayoutPosition()));
                holder.image.startAnimation(pulse);
                holder.itemView.setSelected(true);

            }
        });
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, holder.getLayoutPosition());
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_cloth, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int clickedPosition;
        public MyMenuItemClickListener(int position) {
            clickedPosition = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_cart:
                    clothesViewModel.addToCart(getCloth(clickedPosition));
                    return true;
                default:
            }
            return false;
        }

    }

    @Override
    public int getItemCount() {
        return clothList.size();
    }
}
