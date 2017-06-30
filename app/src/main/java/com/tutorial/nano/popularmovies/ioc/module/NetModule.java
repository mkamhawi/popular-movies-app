package com.tutorial.nano.popularmovies.ioc.module;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tutorial.nano.popularmovies.BuildConfig;
import com.tutorial.nano.popularmovies.network.api.ApiMethods;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetModule {
    private String mBaseUrl;

    public NetModule(String baseUrl) {
        this.mBaseUrl = baseUrl;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient
                .Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        HttpUrl url = originalHttpUrl.newBuilder()
                                .addQueryParameter("api_key", BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                                .build();

                        Request.Builder requestBuilder = original.newBuilder()
                                .url(url);
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .build();
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(mBaseUrl)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    ApiMethods provideApiMethods(Retrofit retrofit) {
        return retrofit.create(ApiMethods.class);
    }
}
