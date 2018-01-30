package pht.eatitserver.model;

public class Order {

    private String Food_ID, Name, Price, Quantity, Discount;

    public Order() {
    }

    public Order(String food_ID, String name, String price, String quantity, String discount) {
        Food_ID = food_ID;
        Name = name;
        Price = price;
        Quantity = quantity;
        Discount = discount;
    }

    public String getFood_ID() {
        return Food_ID;
    }

    public void setFood_ID(String food_ID) {
        Food_ID = food_ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}