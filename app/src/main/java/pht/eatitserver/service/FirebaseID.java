package pht.eatitserver.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import pht.eatitserver.global.Global;
import pht.eatitserver.model.Token;

public class FirebaseID extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        updateToken(tokenRefreshed);
    }

    private void updateToken(String tokenRefreshed) {
        if(Global.activeUser != null){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference token = database.getReference("Token");
            Token child = new Token(tokenRefreshed, true);
            token.child(Global.activeUser.getPhone()).setValue(child);
        }
    }
}