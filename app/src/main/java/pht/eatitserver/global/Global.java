package pht.eatitserver.global;

import pht.eatitserver.model.User;

public class Global {
    public static User activeUser;
    public static final int PICK_IMAGE_REQUEST = 71;
    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static String convertCodeToStatus(String code){
        if(code.equals("0")){
            return "Placed";
        }
        else if(code.equals("1")){
            return "On my way";
        }
        else {
            return "Shipped";
        }
    }
}