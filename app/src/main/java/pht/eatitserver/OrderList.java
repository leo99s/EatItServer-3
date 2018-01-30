package pht.eatitserver;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

import pht.eatitserver.global.Global;
import pht.eatitserver.model.Request;
import pht.eatitserver.onclick.ItemClickListener;
import pht.eatitserver.viewholder.OrderViewHolder;

public class OrderList extends AppCompatActivity {

    RecyclerView rcvOrder;
    RecyclerView.LayoutManager layoutManager;

    MaterialSpinner spinnerStatus;

    FirebaseDatabase database;
    DatabaseReference request;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

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
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.id_order.setText(adapter.getRef(position).getKey());
                viewHolder.phone_order.setText(model.getPhone());
                viewHolder.address_order.setText(model.getAddress());
                viewHolder.status_order.setText(Global.convertCodeToStatus(model.getStatus()));

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        rcvOrder.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Global.UPDATE)){
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Global.DELETE)){
            deleteOrder(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }

        return super.onContextItemSelected(item);
    }

    private void deleteOrder(String key, Request item) {
        request.child(key).removeValue();
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
}
