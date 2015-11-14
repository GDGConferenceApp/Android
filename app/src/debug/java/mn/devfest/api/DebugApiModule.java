package mn.devfest.api;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
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
    @Singleton
    RestAdapter provideRestAdapter(Client client, Gson gson) {
        // TODO use real endpoint
        return new RestAdapter.Builder()
                .setEndpoint("https://devfest.mn.api")
                .setClient(client)
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }

    @Provides
    @Singleton
    DevFestApi provideGw2Api(RestAdapter restAdapter) {
        return restAdapter.create(DevFestApi.class);
    }
}