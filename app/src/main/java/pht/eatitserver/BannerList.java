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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.UUID;
import info.hoang8f.widget.FButton;
import pht.eatitserver.global.Global;
import pht.eatitserver.model.Banner;
import pht.eatitserver.viewholder.BannerViewHolder;

public class BannerList extends AppCompatActivity {

    RelativeLayout root_layout;

    RecyclerView rcvBanner;
    RecyclerView.LayoutManager layoutManager;

    MaterialEditText edtFoodID, edtFoodName;
    FButton btnBrowse, btnUpload;

    FloatingActionButton btnAdd;

    FirebaseDatabase database;
    DatabaseReference banner;

    FirebaseStorage storage;
    StorageReference reference;

    FirebaseRecyclerAdapter<Banner, BannerViewHolder> adapter;

    Banner newBanner;
    Uri uri;    // The local path of image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_list);

        database = FirebaseDatabase.getInstance();
        banner = database.getReference("Banner");
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();

        root_layout = findViewById(R.id.root_layout);
        rcvBanner = findViewById(R.id.rcvBanner);
        btnAdd = findViewById(R.id.btnAdd);
        rcvBanner.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rcvBanner.setLayoutManager(layoutManager);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });

        loadBanner();
    }

    private void loadBanner() {
        FirebaseRecyclerOptions<Banner> options = new FirebaseRecyclerOptions.Builder<Banner>()
                .setQuery(banner, Banner.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Banner, BannerViewHolder>(options) {
            @Override
            public BannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_banner, parent, false);

                return new BannerViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull BannerViewHolder holder, int position, Banner model) {
                holder.name_food.setText(model.getName());
                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(holder.image_food);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        rcvBanner.setAdapter(adapter);
    }

    private void showAddDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(BannerList.this);
        alert.setIcon(R.drawable.ic_photo_library);
        alert.setTitle("Add new banner");
        alert.setMessage("Please fill in all fields :");
        LayoutInflater inflater = this.getLayoutInflater();
        View add_banner = inflater.inflate(R.layout.add_banner, null);

        edtFoodID = add_banner.findViewById(R.id.edtFoodID);
        edtFoodName = add_banner.findViewById(R.id.edtFoodName);
        btnBrowse = add_banner.findViewById(R.id.btnBrowse);
        btnUpload = add_banner.findViewById(R.id.btnUpload);

        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alert.setView(add_banner);

        alert.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                if(newBanner != null){
                    banner.push().setValue(newBanner);
                    loadBanner();
                    Snackbar.make(root_layout, newBanner.getName() + " was added !", Snackbar.LENGTH_LONG).show();
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
            uri = data.getData();
            btnBrowse.setText("Picked");
        }
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
                            Toast.makeText(BannerList.this, "Uploaded successfully !", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newBanner = new Banner(
                                            edtFoodID.getText().toString(),
                                            edtFoodName.getText().toString(),
                                            uri.toString()
                                    );
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(BannerList.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            deleteBanner(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }

        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(final String key, final Banner item) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(BannerList.this);
        alert.setIcon(R.drawable.ic_photo_library);
        alert.setTitle("Update the banner");
        alert.setMessage("Please fill in all fields :");
        LayoutInflater inflater = this.getLayoutInflater();
        View update_banner = inflater.inflate(R.layout.add_banner, null);

        edtFoodID = update_banner.findViewById(R.id.edtFoodID);
        edtFoodName = update_banner.findViewById(R.id.edtFoodName);
        btnBrowse = update_banner.findViewById(R.id.btnBrowse);
        btnUpload = update_banner.findViewById(R.id.btnUpload);

        // Old values
        edtFoodID.setText(item.getFood_id());
        edtFoodName.setText(item.getName());

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

        alert.setView(update_banner);

        alert.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                item.setFood_id(edtFoodID.getText().toString());
                item.setName(edtFoodName.getText().toString()); // item.setImage() in changeImage()

                HashMap<String, Object> object = new HashMap<>();
                object.put("food_id", item.getFood_id());
                object.put("name", item.getName());
                object.put("image", item.getImage());

                banner.child(key).updateChildren(object).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadBanner();
                        Snackbar.make(root_layout, item.getName() + " was updated !", Snackbar.LENGTH_LONG).show();
                    }
                });
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

    private void changeImage(final Banner item) {
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
                            Toast.makeText(BannerList.this, "Uploaded successfully !", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(BannerList.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void deleteBanner(String key, Banner item) {
        banner.child(key).removeValue();
        Snackbar.make(root_layout, item.getName() + " was deleted !", Snackbar.LENGTH_LONG).show();
    }
}
