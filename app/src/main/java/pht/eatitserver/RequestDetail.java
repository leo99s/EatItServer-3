package pht.eatitserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import pht.eatitserver.global.Global;
import pht.eatitserver.viewholder.OrderAdapter;

public class RequestDetail extends AppCompatActivity {

    TextView id_request, phone_request, address_request, total_price_request, message_request;
    RecyclerView rcvOrder;
    RecyclerView.LayoutManager layoutManager;
    String requestID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        id_request = findViewById(R.id.id_request);
        phone_request = findViewById(R.id.phone_request);
        address_request = findViewById(R.id.address_request);
        total_price_request = findViewById(R.id.total_price_request);
        message_request = findViewById(R.id.message_request);

        rcvOrder = findViewById(R.id.rcvOrder);
        rcvOrder.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rcvOrder.setLayoutManager(layoutManager);

        if(getIntent() != null){
            requestID = getIntent().getStringExtra("requestID");
        }

        // Set values of the current request
        id_request.setText(requestID);
        phone_request.setText(Global.activeUser.getPhone());
        address_request.setText(Global.currentRequest.getAddress());
        total_price_request.setText(Global.currentRequest.getTotalPrice());
        message_request.setText(Global.currentRequest.getMessage());

        OrderAdapter adapter = new OrderAdapter(Global.currentRequest.getOrders());
        adapter.notifyDataSetChanged();
        rcvOrder.setAdapter(adapter);
    }
}
