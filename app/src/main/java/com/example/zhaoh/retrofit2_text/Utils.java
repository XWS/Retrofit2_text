package com.example.zhaoh.retrofit2_text;

import android.text.TextUtils;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.ByteString;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

import static com.example.zhaoh.retrofit2_text.Constants.PUBLIC_KEY;
import static com.example.zhaoh.retrofit2_text.Constants.URL;

/**
 * Created by zhaoh on 2016/4/11.
 */
public class Utils {

    private static Retrofit retrofit;

    static {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        String url = request.url().toString();
                        if (url.startsWith(URL) && !url.startsWith(URL + "api/files")) {
                            String[] split = url.split("\\?");
                            if (split.length == 1) {
                                String parameters = convertRequestBodyToString(request.body());
                                RequestBody requestBodyAdd = new FormBody.Builder()
                                        .add("sign", generateParametersWithSignature(parameters, split[0]))
                                        .build();
                                RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), parameters + "&" + convertRequestBodyToString(requestBodyAdd));
                                request = request.newBuilder()
                                        .method(request.method(), body)
                                        .build();
                                return chain.proceed(request);
                            } else if (split.length == 2) {
                                HttpUrl sign = request.url()
                                        .newBuilder()
                                        .addQueryParameter("sign", generateParametersWithSignature(split[1], split[0]))
                                        .build();
                                Request build = request.newBuilder()
                                        .url(sign)
                                        .build();
                                return chain.proceed(build);
                            }
                        }
                        return chain.proceed(request);
                    }
                })
//                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(logging)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))//请求后拿到
                .build();
    }

    private Utils() {
        throw new AssertionError("No instance.");
    }

    public static <T> T create(final Class<T> service) {
        return retrofit.create(service);
    }

    private static String convertRequestBodyToString(RequestBody request) throws IOException {
        Buffer buffer = new Buffer();
        request.writeTo(buffer);
        return buffer.readUtf8();
    }

    private static String generateParametersWithSignature(String parameters, String baseUrl) {
        String[] parameterArray = parameters.split("&");
        TreeMap<String, String> map = new TreeMap<>();
        for (String parameter : parameterArray) {
            String[] keyValuePair = parameter.split("=");
            if (keyValuePair.length > 1) {
                map.put(keyValuePair[0], keyValuePair[1]);
            } else {
                map.put(keyValuePair[0], "");
            }
        }
        String id = baseUrl.substring(baseUrl.lastIndexOf("/") + 1);
        if (!TextUtils.isDigitsOnly(id)) {
            id = "";
        }
        String sign = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sign += "&" + entry.getKey() + "=" + entry.getValue();
        }
        sign = sign.replaceFirst("&", "").replace("%20", "+") + id + PUBLIC_KEY;
        return ByteString.encodeUtf8(sign).md5().hex();
    }
}
