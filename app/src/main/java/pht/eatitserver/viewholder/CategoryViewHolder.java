package pht.eatitserver.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import pht.eatitserver.R;
import pht.eatitserver.global.Global;
import pht.eatitserver.onclick.ItemClickListener;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView name_category;
    public ImageView image_category;

    private ItemClickListener itemClickListener;

    public CategoryViewHolder(View view) {
        super(view);
        name_category = view.findViewById(R.id.name_category);
        image_category = view.findViewById(R.id.image_category);
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