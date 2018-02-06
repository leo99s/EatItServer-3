package pht.eatitserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import pht.eatitserver.global.Global;
import pht.eatitserver.viewholder.OrderDetailAdapter;

public class OrderDetail extends AppCompatActivity {

    TextView id_order, phone_order, address_order, total_order, comment_order;
    RecyclerView rcvOrderDetail;
    RecyclerView.LayoutManager layoutManager;
    String orderID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        id_order = findViewById(R.id.id_order);
        phone_order = findViewById(R.id.phone_order);
        address_order = findViewById(R.id.address_order);
        total_order = findViewById(R.id.total_order);
        comment_order = findViewById(R.id.comment_order);

        rcvOrderDetail = findViewById(R.id.rcvOrderDetail);
        rcvOrderDetail.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rcvOrderDetail.setLayoutManager(layoutManager);

        if(getIntent() != null){
            orderID = getIntent().getStringExtra("orderID");
        }

        // Set values of the current request
        id_order.setText(orderID);
        phone_order.setText(Global.activeUser.getPhone());
        total_order.setText(Global.currentRequest.getTotal());
        address_order.setText(Global.currentRequest.getAddress());
        comment_order.setText(Global.currentRequest.getComment());

        OrderDetailAdapter adapter = new OrderDetailAdapter(Global.currentRequest.getOrders());
        adapter.notifyDataSetChanged();
        rcvOrderDetail.setAdapter(adapter);
    }
}
