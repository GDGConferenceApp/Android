package mn.devfest.api;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.converter.GsonConverter;

/**
 * Release Dagger module for interacting with the DevFest API
 *
 * @author bherbst
 */
@Module(includes = ApiModule.class)
public final class ReleaseApiModule {
    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @Singleton
    RestAdapter provideRestAdapter(Client client, Gson gson) {
        // TODO use real endpoint
        return new RestAdapter.Builder()
                .setEndpoint("https://devfest.mn")
                .setClient(client)
                .setConverter(new GsonConverter(gson))
                .build();
    }

    @Provides
    @Singleton
    DevFestApi provideDevFestApi(RestAdapter restAdapter) {
        return restAdapter.create(DevFestApi.class);
    }
}