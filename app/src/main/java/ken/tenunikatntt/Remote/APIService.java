package ken.tenunikatntt.Remote;


import ken.tenunikatntt.Model.MyResponse;
import ken.tenunikatntt.Model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


/**
 * Created by Emilken18 on 7/23/2018.
 */

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAvnrEl0w:APA91bGruA3QDxUclpKH7qRf0woUxuvDtTfyyfkGdPLq2yECNNBRiGAyptHg_SttVqthv9hFomQQFSHNgmcdWqLxx1evMqka76N04gVwyh4Ha_98MQS7-9dmXHdUuedX-5jPL39_6imXst4nk9SHPUGAlr500H_JCA"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
