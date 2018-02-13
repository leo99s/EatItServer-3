package pht.eatitserver.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import info.hoang8f.widget.FButton;
import pht.eatitserver.R;

public class RequestViewHolder extends RecyclerView.ViewHolder {

    public TextView id_request, phone_request, address_request, status_request;
    public FButton btnUpdate, btnDelete, btnDetail, btnDirection;

    public RequestViewHolder(View view) {
        super(view);

        id_request = view.findViewById(R.id.id_request);
        phone_request = view.findViewById(R.id.phone_request);
        address_request = view.findViewById(R.id.address_request);
        status_request = view.findViewById(R.id.status_request);

        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnDetail = view.findViewById(R.id.btnDetail);
        btnDirection = view.findViewById(R.id.btnDirection);
    }
}