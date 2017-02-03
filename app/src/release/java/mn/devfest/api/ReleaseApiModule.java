package com.devfestmn.api;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import com.devfestmn.BuildConfig;
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
    @Named("conference")
    @Singleton
    RestAdapter provideConferenceRestAdapter(Client client, Gson gson) {
        return new RestAdapter.Builder()
                .setEndpoint(BuildConfig.CONFERENCE_API_BASE)
                .setClient(client)
                .setConverter(new GsonConverter(gson))
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
                .build();
    }
}