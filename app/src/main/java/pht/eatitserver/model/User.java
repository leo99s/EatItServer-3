package pht.eatitserver.model;

public class User {

    private String phone, name, password, admin;

    public User() {
    }

    public User(String phone, String name, String password, String admin) {
        this.phone = phone;
        this.name = name;
        this.password = password;
        this.admin = admin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}