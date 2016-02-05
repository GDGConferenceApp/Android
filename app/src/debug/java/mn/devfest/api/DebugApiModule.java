package mn.devfest.api;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mn.devfest.BuildConfig;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.converter.GsonConverter;

/**
 * Debug Dagger module for interacting with the DevFest API
 *
 * @author bherbst
 */
@Module(includes = ApiModule.class)
public final class DebugApiModule {

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        OkHttpClient okClient = new OkHttpClient();
        okClient.networkInterceptors().add(new StethoInterceptor());

        return okClient;
    }

    @Provides
    @Named("conference")
    @Singleton
    RestAdapter provideConferenceRestAdapter(Client client, Gson gson) {
        return new RestAdapter.Builder()
                .setEndpoint(BuildConfig.CONFERENCE_API_BASE)
                .setClient(client)
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }

    @Provides
    @Named("feedback")
    @Singleton
    RestAdapter provideFeedbackRestAdapter(Client client, Gson gson) {
        return new RestAdapter.Builder()
                .setEndpoint(BuildConfig.FEEDBACK_API_BASE)
                .setClient(client)
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }
}