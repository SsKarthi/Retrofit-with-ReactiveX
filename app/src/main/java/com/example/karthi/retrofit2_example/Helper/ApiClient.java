package com.example.karthi.retrofit2_example.Helper;

import com.example.karthi.retrofit2_example.AppControler;
import com.example.karthi.retrofit2_example.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS;
import static okhttp3.logging.HttpLoggingInterceptor.Level.NONE;


public class ApiClient {

    public static final String github = "https://api.github.com/users/";

    private static Retrofit retrofit1 = null;
    private static Retrofit retrofit2 = null;
    private static final String CACHE_CONTROL = "Cache-Control";


    public static Retrofit getClient() {
        if (retrofit2 == null) {
            retrofit2 = new Retrofit.Builder()
                    .baseUrl(github)
                    .client(provideOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofit2;
    }

    private static OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(provideHttpLoggingInterceptor())
                .addInterceptor(provideOfflineCacheInterceptor())
                .addNetworkInterceptor(provideCacheInterceptor())
                .cache(provideCache())

                .build();
    }

    private static Cache provideCache() {
        Cache cache = null;
        try {
            Timber.e(AppControler.getInstance().getCacheDir().getPath());
            cache = new Cache(new File(AppControler.getInstance().getCacheDir(), "http-cache"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            Timber.e("Could not create Cache!");
        }
        return cache;
    }

    private static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor((message) -> Timber.e(message));
        httpLoggingInterceptor.setLevel(BuildConfig.DEBUG ? HEADERS : NONE);
        return httpLoggingInterceptor;
    }

    public static Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());

                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(60, TimeUnit.SECONDS)
                        .maxStale(300, TimeUnit.SECONDS)
                        .build();

                return response.newBuilder()
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .removeHeader("Access-Control-Allow-Origin")
                        .removeHeader("Vary")
                        .removeHeader("Age")
                        .removeHeader("expires")
                        .removeHeader("access-control-allow-credentials")
                        .removeHeader("Via")
                        .removeHeader("cf-cache-status")
                        .removeHeader("pragma")
                        .removeHeader("C3-Request")
                        .removeHeader("C3-Domain")
                        .removeHeader("C3-Date")
                        .removeHeader("C3-Hostname")
                        .removeHeader("C3-Cache-Control")
                        .removeHeader("X-Varnish-back")
                        .removeHeader("X-Varnish")
                        .removeHeader("X-Cache")
                        .removeHeader("X-Cache-Hit")
                        .removeHeader("X-Varnish-front")
                        .removeHeader("Connection")
                        .removeHeader("Accept-Ranges")
                        .removeHeader("Transfer-Encoding")
                        .build();
            }
        };
    }

    public static Interceptor provideOfflineCacheInterceptor() {
        return (chain) -> {
            Request request = chain.request();

            if (!AppControler.hasNetwork()) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();

                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }

            return chain.proceed(request);

        };
    }
}