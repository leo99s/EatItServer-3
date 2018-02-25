package pht.eatitserver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.rengwuxian.materialedittext.MaterialEditText;
import info.hoang8f.widget.FButton;
import pht.eatitserver.global.Global;
import pht.eatitserver.model.Notification;
import pht.eatitserver.model.Response;
import pht.eatitserver.model.Sender;
import pht.eatitserver.remote.FCMService;
import retrofit2.Call;
import retrofit2.Callback;

public class Market extends AppCompatActivity {

    MaterialEditText edtTitle, edtMessage;
    FButton btnSend;

    FCMService mFcmService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        mFcmService = Global.getFCMAPI();

        edtTitle = findViewById(R.id.edtTitle);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a notification
                Notification notification = new Notification(
                        edtTitle.getText().toString(),
                        edtMessage.getText().toString()
                );

                Sender toTopic = new Sender();
                toTopic.to = new StringBuilder("/topics/").append("News").toString();
                toTopic.notification = notification;

                mFcmService.sendNotification(toTopic).enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        if(response.isSuccessful()){
                            Intent home = new Intent(Market.this, Home.class);
                            startActivity(home);
                            Toast.makeText(Market.this, "Message was sent !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        Toast.makeText(Market.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
