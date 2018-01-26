package pht.eatitserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import info.hoang8f.widget.FButton;
import pht.eatitserver.global.Global;
import pht.eatitserver.model.User;

public class SignIn extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    FButton btnSignIn;

    FirebaseDatabase database;
    DatabaseReference user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);

        database = FirebaseDatabase.getInstance();
        user = database.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(edtPhone.getText().toString(), edtPassword.getText().toString());
            }
        });
    }

    private void signIn(final String phone, final String password) {
        final ProgressDialog dialog = new ProgressDialog(SignIn.this);
        dialog.setMessage("Please wait...");
        dialog.show();

        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(phone).exists()){
                    dialog.dismiss();
                    User child = dataSnapshot.child(phone).getValue(User.class);
                    child.setPhone(phone);

                    if(Boolean.parseBoolean(child.getAdmin()) && child.getPassword().equals(password)){
                        Intent home = new Intent(SignIn.this, Home.class);
                        Global.activeUser = child;
                        startActivity(home);
                        finish();
                    }
                    else {
                        dialog.dismiss();
                        Toast.makeText(SignIn.this, "Signed in failed !", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    dialog.dismiss();
                    Toast.makeText(SignIn.this, "User doesn't exist !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
