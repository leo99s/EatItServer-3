package pht.eatitserver.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MapService {

    @GET("maps/api/directions/json")
    Call<String> getDirection(@Query("origin") String origin, @Query("destination") String destination);
}