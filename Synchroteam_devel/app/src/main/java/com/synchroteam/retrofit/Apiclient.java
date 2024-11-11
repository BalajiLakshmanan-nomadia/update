package com.synchroteam.retrofit;

import android.os.Build;
import android.provider.SyncStateContract;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synchroteam.utils.Commons;
import com.synchroteam.utils.MyTLSSocketFactory;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

//import io.fabric.sdk.android.Logger;
import okhttp3.CertificatePinner;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Trident.
 */
public class Apiclient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
//            Gson gson = new GsonBuilder()
//                    .setLenient()
//                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(Commons.BASE_URL)
                    .client(getNewHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
//        //new changes
//        retrofit = new Retrofit.Builder()
//                .baseUrl(Commons.BASE_URL)
//                .client(provideClient())
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
        return retrofit;
    }

    /**
     * To log the HTTP client about web services
     * USAGE: .client(provideClient())
     * in getClient method
     *
     * @return
     */
    public static OkHttpClient provideClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//        return new OkHttpClient.Builder()
//                .setReadTimeout(60 * 1000, TimeUnit.MILLISECONDS)
//                .addInterceptor(interceptor)
//                .build();
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1)
                .build();



       return new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.MINUTES)
                .connectionSpecs(Collections.singletonList(spec))
                .addInterceptor(interceptor)
                .build();
    }

    private static OkHttpClient getNewHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .cache(null)
                .addInterceptor(interceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.MINUTES);

        return enableTls12OnPreLollipop(client).build();
    }

    public static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT < 22) {
            try {
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, null);
                client.sslSocketFactory(new MyTLSSocketFactory(sc.getSocketFactory()));

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
            } catch (Exception exc) {
                com.synchroteam.utils.Logger.log("OkHttpTLSCompat",
                        "Error while setting TLS 1.2---->"+ exc);
            }
        }

        return client;
    }
}
