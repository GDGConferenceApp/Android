package mn.devfest.api;

import mn.devfest.api.model.Conference;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Retrofit API interface for DevFest data
 *
 * @author bherbst
 */
public interface DevFestApi {

    @GET("devfest.json")
    void getConferenceInfo(Callback<Conference> callback);

}
