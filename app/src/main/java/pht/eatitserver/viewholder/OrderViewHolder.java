package pht.eatitserver.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import info.hoang8f.widget.FButton;
import pht.eatitserver.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView id_order, phone_order, address_order, status_order;
    public FButton btnUpdate, btnDelete, btnDetail, btnDirection;

    public OrderViewHolder(View view) {
        super(view);

        id_order = view.findViewById(R.id.id_order);
        phone_order = view.findViewById(R.id.phone_order);
        address_order = view.findViewById(R.id.address_order);
        status_order = view.findViewById(R.id.status_order);

        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnDetail = view.findViewById(R.id.btnDetail);
        btnDirection = view.findViewById(R.id.btnDirection);
    }
}