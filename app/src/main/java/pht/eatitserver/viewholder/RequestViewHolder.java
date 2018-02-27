package pht.eatitserver.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import pht.eatitserver.R;

public class RequestViewHolder extends RecyclerView.ViewHolder {

    public TextView id_request, phone_request, address_request, delivery_status_request;
    public TextView txtUpdate, txtDelete, txtDetail, txtDirect;

    public RequestViewHolder(View view) {
        super(view);

        id_request = view.findViewById(R.id.id_request);
        phone_request = view.findViewById(R.id.phone_request);
        address_request = view.findViewById(R.id.address_request);
        delivery_status_request = view.findViewById(R.id.delivery_status_request);

        txtUpdate = view.findViewById(R.id.txtUpdate);
        txtDelete = view.findViewById(R.id.txtDelete);
        txtDetail = view.findViewById(R.id.txtDetail);
        txtDirect = view.findViewById(R.id.txtDirect);
    }
}