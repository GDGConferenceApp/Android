package mn.devfest.api;

import mn.devfest.api.model.Feedback;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Retrofit API interface for submitting feedback
 *
 * @author bherbst
 */
public interface FeedbackApi {

    @POST("/feedback/2016mobile.json")
    void submitRating(@Body Feedback feedback, Callback<Feedback> result);

}
