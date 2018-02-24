package pht.eatitserver.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import pht.eatitserver.R;
import pht.eatitserver.onclick.ItemClickListener;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView name_food;
    public ImageView image_food;

    private ItemClickListener itemClickListener;

    public FoodViewHolder(View view) {
        super(view);
        name_food = view.findViewById(R.id.name_food);
        image_food = view.findViewById(R.id.image_food);
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
        menu.add(0, 0, getAdapterPosition(), "Update");
        menu.add(0, 1, getAdapterPosition(), "Delete");
    }
}