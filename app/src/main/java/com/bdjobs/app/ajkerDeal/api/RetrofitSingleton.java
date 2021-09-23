package com.bdjobs.app.ajkerDeal.api;

import android.content.Context;

import com.example.livevideoshopping.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MhRaju on 07/01/2017.
 */

public class RetrofitSingleton {

    private static Retrofit sRetrofit;
    private static OkHttpClient okHttpClient;


    private RetrofitSingleton() {
    }

    public synchronized static Retrofit getInstance(final Context context, String mBaseUrl) {

        if (okHttpClient == null) {
            createOkHttpClient(context);
        }

        if (sRetrofit == null) {
            createRetrofit(context, mBaseUrl);
        } else {
            createRetrofit(context, mBaseUrl);
        }
        return sRetrofit;
    }

    private static void createRetrofit(final Context context, String baseUrl) {

        if (baseUrl.equals("apiBase")) {

            sRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://api.ajkerdeal.com")  //  http://api.ajkerdeal.com
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        } else if (baseUrl.equals("elasticBase")) {
            sRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://elastic.ajkerdeal.com")    // https://elastic.ajkerdeal.com
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        } else if (baseUrl.equals("merchantApiBase")) {

            sRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://merchantapi.ajkerdeal.com")  //  http://merchantapi.ajkerdeal.com
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        } else if (baseUrl.equals("awsBase")) {
            sRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://es2.ajkerdeal.com")  //  https://es2.ajkerdeal.com
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        } else if (baseUrl.equals("imageUploadBase")) {
            sRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://adm.ajkerdeal.com")   // https://adm.ajkerdeal.com
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        } else if (baseUrl.equals("bridgeApiBase")) {

            sRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://bridge.ajkerdeal.com")   // http://bridge.ajkerdeal.com
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        } else if (baseUrl.equals("voucherApiBase")) {

            sRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://api.ajkerdeal.com")  //  http://api.ajkerdeal.com
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } else if (baseUrl.equals("httpsServer")) {

            sRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://api.ajkerdeal.com")  //  http://api.ajkerdeal.com
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        } else if (baseUrl.equals("rAndD")) {
            sRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://analytics.ajkerdeal.com")//http://52.76.15.2 Deprecated
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } else if (baseUrl.equals("sBase")) {

            //Log.e("checkSpinnAnim", "sBase");

            sRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://api.ajkerdeal.com")  //  http://api.ajkerdeal.com
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        } else if (baseUrl.equals("sheba")) {
            sRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://api.sheba.xyz")  //  https://api.dev-sheba.xyz
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } else if (baseUrl.equals("xtra")) {
            sRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("http://xtra.dev.aplectrum.com")  //  http://xtra.dev.aplectrum.com
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } else if (baseUrl.equals("barikoi")) {
            sRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://barikoi.xyz")  //  https://barikoi.xyz
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    private static void createOkHttpClient(final Context context) {

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        // Time out
        httpClientBuilder.connectTimeout(30, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(30, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(30, TimeUnit.SECONDS);
        // Cache
        httpClientBuilder.cache(new Cache(context.getCacheDir(),  10 * 1024 * 1024 )); // 10 MB
        // LoggingInterceptor
        if (BuildConfig.DEBUG) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            httpClientBuilder.addInterceptor(logging);
        }

        okHttpClient = httpClientBuilder.build();
    }

}
