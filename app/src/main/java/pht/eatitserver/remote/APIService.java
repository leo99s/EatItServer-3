package pht.eatitserver.remote;

import pht.eatitserver.model.Response;
import pht.eatitserver.model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAqYfWFMU:APA91bGgyJH2sjrXUL-qnoLkdsvtUYsLviIY2B2h1X0BEQlsPdUuiQhj-Y9uoO9Xt35s2PXCyOgEPfc5VK4xF95-0S0pG62t2pzqGwKXgOaDo987K-kyw425o4m-_ohvjoR5xHuwCuWE"
            }
    )

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}