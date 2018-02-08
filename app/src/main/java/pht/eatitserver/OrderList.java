package pht.eatitserver;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import pht.eatitserver.global.Global;
import pht.eatitserver.model.Notification;
import pht.eatitserver.model.Request;
import pht.eatitserver.model.Response;
import pht.eatitserver.model.Sender;
import pht.eatitserver.model.Token;
import pht.eatitserver.onclick.ItemClickListener;
import pht.eatitserver.remote.APIService;
import pht.eatitserver.viewholder.OrderViewHolder;
import retrofit2.Call;
import retrofit2.Callback;

public class OrderList extends AppCompatActivity {

    RecyclerView rcvOrder;
    RecyclerView.LayoutManager layoutManager;

    MaterialSpinner spinnerStatus;

    FirebaseDatabase database;
    DatabaseReference request;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        mService = Global.getFCMService();

        database = FirebaseDatabase.getInstance();
        request = database.getReference("Request");

        rcvOrder = findViewById(R.id.rcvOrder);
        rcvOrder.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rcvOrder.setLayoutManager(layoutManager);

        loadOrder();
    }

    // Load all requests (cart = order list / order = request list)
    private void loadOrder() {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.item_order,
                OrderViewHolder.class,
                request
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, final int position) {
                viewHolder.id_order.setText(adapter.getRef(position).getKey());
                viewHolder.phone_order.setText(model.getPhone());
                viewHolder.address_order.setText(model.getAddress());
                viewHolder.status_order.setText(Global.convertCodeToStatus(model.getStatus()));

                viewHolder.btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateDialog(adapter.getRef(position).getKey(), adapter.getItem(position));
                    }
                });

                viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(adapter.getRef(position).getKey(), adapter.getItem(position));
                    }
                });

                viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent orderDetail = new Intent(OrderList.this, OrderDetail.class);
                        Global.currentRequest = model;
                        orderDetail.putExtra("orderID", adapter.getRef(position).getKey());
                        startActivity(orderDetail);
                    }
                });

                viewHolder.btnDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent trackOrder = new Intent(OrderList.this, TrackOrder.class);
                        Global.currentRequest = model;
                        startActivity(trackOrder);
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        rcvOrder.setAdapter(adapter);
    }

    private void deleteOrder(String key, Request item) {
        request.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }

    private void showUpdateDialog(final String key, final Request item) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(OrderList.this);
        alert.setTitle("Update order");
        alert.setMessage("Please select a status :");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order, null);

        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        spinnerStatus.setItems("Placed", "On my way", "Shipped");

        alert.setView(view);

        alert.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                item.setStatus(String.valueOf(spinnerStatus.getSelectedIndex()));
                request.child(key).setValue(item);
                adapter.notifyDataSetChanged();
                sendNotification(key, item);
            }
        });

        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void sendNotification(final String key, Request item) {
        DatabaseReference reference = database.getReference("Token");

        reference.orderByKey().equalTo(item.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                    Token clientToken = childDataSnapshot.getValue(Token.class);

                    // Create raw payload to send
                    Notification notification = new Notification("Hoàng Tâm", "Your order " + key + " was updated !");
                    Sender content = new Sender(clientToken.getToken(), notification);
                    mService.sendNotification(content)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    if(response.code() == 200){
                                        if(response.body().success == 1){
                                            Toast.makeText(OrderList.this, "Your order was update !", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(OrderList.this, "Your order was update but we can't send notification !", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
