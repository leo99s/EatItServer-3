package pht.eatitserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import java.util.UUID;
import info.hoang8f.widget.FButton;
import pht.eatitserver.global.Global;
import pht.eatitserver.model.Category;
import pht.eatitserver.model.Food;
import pht.eatitserver.onclick.ItemClickListener;
import pht.eatitserver.viewholder.FoodViewHolder;

public class FoodList extends AppCompatActivity {

    RelativeLayout root_layout;

    RecyclerView rcvFood;
    RecyclerView.LayoutManager layoutManager;

    MaterialEditText edtFoodName, edtFoodDescription, edtFoodPrice, edtFoodDiscount;
    FButton btnBrowse, btnUpload;

    FloatingActionButton btnAdd;

    FirebaseDatabase database;
    DatabaseReference food;

    FirebaseStorage storage;
    StorageReference reference;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    String category_id = "";
    Food newFood;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        root_layout = findViewById(R.id.root_layout);

        rcvFood = findViewById(R.id.rcvFood);
        btnAdd = findViewById(R.id.btnAdd);

        database = FirebaseDatabase.getInstance();
        food = database.getReference("Food");

        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();

        rcvFood.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rcvFood.setLayoutManager(layoutManager);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });

        if(getIntent() != null){
            category_id = getIntent().getStringExtra("category_id");
        }

        if(!category_id.isEmpty() && category_id != null){
            loadFood(category_id);
        }
    }

    private void showAddDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(FoodList.this);
        alert.setIcon(R.drawable.ic_shopping_cart);
        alert.setTitle("Add new food");
        alert.setMessage("Please fill in all fields :");
        LayoutInflater inflater = this.getLayoutInflater();
        View add_food = inflater.inflate(R.layout.add_food, null);

        edtFoodName = add_food.findViewById(R.id.edtFoodName);
        edtFoodDescription = add_food.findViewById(R.id.edtFoodDescription);
        edtFoodPrice = add_food.findViewById(R.id.edtFoodPrice);
        edtFoodDiscount = add_food.findViewById(R.id.edtFoodDiscount);
        btnBrowse = add_food.findViewById(R.id.btnBrowse);
        btnUpload = add_food.findViewById(R.id.btnUpload);

        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alert.setView(add_food);

        alert.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                if(newFood != null){
                    food.push().setValue(newFood);
                    Snackbar.make(root_layout, newFood.getName() + " was added !", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void uploadImage() {
        if(uri != null){
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading...");
            dialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = reference.child("images/" + imageName);

            imageFolder.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded successfully !", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newFood = new Food();
                                    newFood.setCategory_ID(category_id);
                                    newFood.setName(edtFoodName.getText().toString());
                                    newFood.setImage(uri.toString());
                                    newFood.setDescription(edtFoodDescription.getText().toString());
                                    newFood.setPrice(edtFoodPrice.getText().toString());
                                    newFood.setDiscount(edtFoodDiscount.getText().toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(FoodList.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                            dialog.setMessage(progress + "% uploaded");
                        }
                    });
        }
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pick an image"), Global.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Global.PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null){
            uri = data.getData();
            btnBrowse.setText("Picked");
        }
    }

    private void loadFood(String category_id) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.item_food,
                FoodViewHolder.class,
                food.orderByChild("category_id").equalTo(category_id)
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.name_food.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.image_food);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        rcvFood.setAdapter(adapter);
    }
}
