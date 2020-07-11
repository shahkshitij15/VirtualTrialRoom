package com.example.arshop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.arshop.ui.category.Cloth;
import com.example.arshop.ui.category.ClothAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.opencv.core.Point;

import java.util.ArrayList;

public class ClothesViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Cloth>> sendClothes = new MutableLiveData<>();
    private ArrayList<Cloth> allClothes = new ArrayList<>();
    private ArrayList<Cloth> cartClothes = new ArrayList<>();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private ClothAdapter adapter;
    private MutableLiveData<Cloth> selectedWearable;

    public ClothesViewModel() {
    }

    public MutableLiveData<ArrayList<Cloth>> getClothList() {
        if (sendClothes.getValue() == null) {
            sendClothes.setValue((ArrayList<Cloth>) adapter.getClothList());
            selectedWearable = new MutableLiveData<>();
            prepareClothes();
        } else if (adapter.getClothList().size() != sendClothes.getValue().size()) {
            adapter.getClothList().clear();
            adapter.addAllClothes(sendClothes.getValue());
        }
        return sendClothes;
    }

    public ArrayList<Cloth> getAllClothes() {
        return this.allClothes;
    }

    public LiveData<Cloth> getSelectedCloth() {
        if (selectedWearable == null) {
            selectedWearable = new MutableLiveData<>();
        }
        return selectedWearable;
    }

    public ArrayList<Cloth> getCartClothes() {
        return cartClothes;
    }

    public void setAdapter(ClothAdapter adapter) {
        this.adapter = adapter;
    }

    public void setSelectedWearable(Cloth cloth) {
        selectedWearable.setValue(cloth);
    }

    public void addToCart(Cloth cloth) {
        if (cartClothes.contains(cloth)) {
            Toast.makeText(adapter.mContext, "Already in Cart", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(adapter.mContext, "Added to Cart", Toast.LENGTH_SHORT).show();
        cartClothes.add(cloth);
    }

    private void prepareClothes() {
        //Cloth cloth = new Cloth("Black", new String[]{"Upper Body", "Outdoor", "Formals"}, );
        // Read from the database

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (int i = 0; i < dataSnapshot.getChildrenCount(); i++) {
                    final String name = dataSnapshot.child("/" + i + "/name").getValue(String.class);
                    final String gender = dataSnapshot.child("/" + i + "/Gender").getValue(String.class);
                    final String color = dataSnapshot.child("/" + i + "/Color").getValue(String.class);
                    final String brand = dataSnapshot.child("/" + i + "/Brand").getValue(String.class);
                    final int index = i;
                    final String[] categories;
                    if (dataSnapshot.child("/" + i + "/Categories").getChildrenCount() == 0) {
                        categories = new String[1];
                        categories[0] = "Casuals";
                    } else {
                        categories = new String[(int) dataSnapshot.child("/" + i + "/Categories").getChildrenCount()];
                        Iterable<DataSnapshot> categoriesItr = dataSnapshot.child("/" + i + "/Categories").getChildren();
                        int j = 0;
                        for (DataSnapshot category : categoriesItr) {
                            categories[j] = category.getValue(String.class);
                            j++;
                        }
                    }
                    adapter.addClothes(new Cloth(name, categories, gender, brand, color));
                    adapter.notifyItemChanged(index);
                    double[] top_left = new double[]{dataSnapshot.child("/" + i + "/r_shoulder/0").getValue(double.class), dataSnapshot.child("/" + i + "/r_shoulder/1").getValue(double.class)};
                    double[] top_right = new double[]{dataSnapshot.child("/" + i + "/l_shoulder/0").getValue(double.class), dataSnapshot.child("/" + i + "/l_shoulder/1").getValue(double.class)};
                    double[] bottom_left = new double[]{dataSnapshot.child("/" + i + "/r_hip/0").getValue(double.class), dataSnapshot.child("/" + i + "/r_hip/1").getValue(double.class)};
                    double[] bottom_right = new double[]{dataSnapshot.child("/" + i + "/l_hip/0").getValue(double.class), dataSnapshot.child("/" + i + "/l_hip/1").getValue(double.class)};
                    adapter.getCloth(index).setPoints(new Point(top_left), new Point(top_right), new Point(bottom_left), new Point(bottom_right));
                    storageRef.child("compressed/" + name.split("\\.")[0] + "-min.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(adapter.mContext).load(uri.toString()).asBitmap().into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                    try {
                                        adapter.getCloth(index).setImage(bitmap.copy(Bitmap.Config.ARGB_8888, false));
                                    } catch (IndexOutOfBoundsException e) {
                                        adapter.addClothes(new Cloth(name, categories, bitmap.copy(Bitmap.Config.ARGB_8888, false), gender, brand, color));
                                    }
                                    adapter.notifyItemChanged(index);
                                    sendClothes.setValue((ArrayList<Cloth>) adapter.getClothList());
                                    allClothes.clear();
                                    allClothes.addAll(adapter.getClothList());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ClothesViewModel", "Failed to read value.", error.toException());
            }
        });
    }
}