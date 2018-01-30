package pht.eatitserver.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;
import pht.eatitserver.R;
import pht.eatitserver.global.Global;
import pht.eatitserver.onclick.ItemClickListener;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView id_order, phone_order, address_order, status_order;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(View view) {
        super(view);
        id_order = view.findViewById(R.id.id_order);
        phone_order = view.findViewById(R.id.phone_order);
        address_order = view.findViewById(R.id.address_order);
        status_order = view.findViewById(R.id.status_order);

        view.setOnClickListener(this);
        view.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select an action");
        menu.add(0, 0, getAdapterPosition(), Global.UPDATE);
        menu.add(0, 1, getAdapterPosition(), Global.DELETE);
    }
}