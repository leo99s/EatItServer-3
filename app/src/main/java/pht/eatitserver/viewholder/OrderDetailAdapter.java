package pht.eatitserver.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import pht.eatitserver.R;
import pht.eatitserver.model.Order;

class OrderDetailViewHolder extends RecyclerView.ViewHolder {

    public TextView name_food, quantity_food, price_food, discount_food;

    public OrderDetailViewHolder(View view) {
        super(view);
        name_food = view.findViewById(R.id.name_food);
        quantity_food = view.findViewById(R.id.quantity_food);
        price_food = view.findViewById(R.id.price_food);
        discount_food = view.findViewById(R.id.discount_food);
    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailViewHolder> {

    List<Order> orderList;

    public OrderDetailAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @Override
    public OrderDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_order, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderDetailViewHolder holder, int position) {
        Order item = orderList.get(position);
        holder.name_food.setText(String.format("Name : %s", item.getName()));
        holder.quantity_food.setText(String.format("Quantity : %s", item.getQuantity()));
        holder.price_food.setText(String.format("Price : %s", item.getPrice()));
        holder.discount_food.setText(String.format("Discount : %s", item.getDiscount()));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}