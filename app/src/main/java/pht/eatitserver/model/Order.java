package pht.eatitserver.model;

public class Order {

    private String food_id, name, price, quantity, discount;

    public Order() {
    }

    public Order(String food_id, String name, String price, String quantity, String discount) {
        this.food_id = food_id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
    }

    public String getFood_ID() {
        return food_id;
    }

    public void setFood_ID(String food_id) {
        this.food_id = food_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}