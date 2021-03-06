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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import java.util.UUID;
import info.hoang8f.widget.FButton;
import pht.eatitserver.global.Global;
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
    Uri filePath;    // The local path of image

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

    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void loadFood(String category_id) {
        Query query = food.orderByChild("category_id").equalTo(category_id);

        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query, Food.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_food, parent, false);
                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                holder.name_food.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.image_food);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        rcvFood.setAdapter(adapter);
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
                    loadFood(category_id);
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
            filePath = data.getData();
            btnBrowse.setText("Picked");
        }
    }

    private void uploadImage() {
        if(filePath != null){
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading...");
            dialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = reference.child("images/" + imageName);

            imageFolder.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded successfully !", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newFood = new Food(
                                            category_id,
                                            edtFoodName.getText().toString(),
                                            uri.toString(),
                                            edtFoodDescription.getText().toString(),
                                            edtFoodPrice.getText().toString(),
                                            edtFoodDiscount.getText().toString()
                                    );
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("Update")){
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals("Delete")){
            deleteFood(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }

        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(final String key, final Food item) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(FoodList.this);
        alert.setIcon(R.drawable.ic_shopping_cart);
        alert.setTitle("Update the food");
        alert.setMessage("Please fill in all fields :");
        LayoutInflater inflater = this.getLayoutInflater();
        View update_food = inflater.inflate(R.layout.add_food, null);

        edtFoodName = update_food.findViewById(R.id.edtFoodName);
        edtFoodDescription = update_food.findViewById(R.id.edtFoodDescription);
        edtFoodPrice = update_food.findViewById(R.id.edtFoodPrice);
        edtFoodDiscount = update_food.findViewById(R.id.edtFoodDiscount);
        btnBrowse = update_food.findViewById(R.id.btnBrowse);
        btnUpload = update_food.findViewById(R.id.btnUpload);

        // Old values
        edtFoodName.setText(item.getName());
        edtFoodDescription.setText(item.getDescription());
        edtFoodPrice.setText(item.getPrice());
        edtFoodDiscount.setText(item.getDiscount());

        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });

        alert.setView(update_food);

        alert.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                item.setName(edtFoodName.getText().toString());
                item.setDescription(edtFoodDescription.getText().toString());
                item.setPrice(edtFoodPrice.getText().toString());
                item.setDiscount(edtFoodDiscount.getText().toString()); // item.setImage() in changeImage()
                food.child(key).setValue(item);
                loadFood(category_id);
                Snackbar.make(root_layout, item.getName() + " was updated !", Snackbar.LENGTH_LONG).show();
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

    private void changeImage(final Food item) {
        if(filePath != null){
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading...");
            dialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = reference.child("images/" + imageName);

            imageFolder.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded successfully !", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage(uri.toString());
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

    private void deleteFood(String key, Food item) {
        food.child(key).removeValue();
        Snackbar.make(root_layout, item.getName() + " was deleted !", Snackbar.LENGTH_LONG).show();
    }
}
