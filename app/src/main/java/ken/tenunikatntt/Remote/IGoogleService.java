package ken.tenunikatntt.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Emilken18 on 11/10/2018.
 */

public interface IGoogleService {
    @GET
    Call<String> getAddressName (@Url String url);
}
