package com.base.mvvm.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.base.mvvm.BuildConfig;
import com.base.mvvm.http.cookie.CookieJarImpl;
import com.base.mvvm.http.cookie.store.PersistentCookieStore;
import com.base.mvvm.http.interceptor.BaseInterceptor;
import com.base.mvvm.http.interceptor.CacheInterceptor;
import com.base.mvvm.http.interceptor.logging.Level;
import com.base.mvvm.http.interceptor.logging.LoggingInterceptor;
import com.base.mvvm.utils.HttpsUtils;
import com.base.mvvm.utils.KLog;
import com.base.mvvm.utils.Utils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitClient封装单例类, 实现网络请求
 */
public class RetrofitClient {

    private static final Context mContext = Utils.getContext();

    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;

    private Cache cache = null;
    private File httpCacheDirectory;

    //超时时间
    final static int DEFAULT_TIMEOUT = 30;
    //缓存时间
    final static int MAX_CACHE_SIZE = 10 * 1024 * 1024;

    @SuppressLint("StaticFieldLeak")
    private static volatile RetrofitClient INSTANCE;

    public static RetrofitClient get(String url, Map<String, String> headers) {
        if (INSTANCE == null)
            synchronized (RetrofitClient.class) {
                if (INSTANCE == null)
                    INSTANCE = new RetrofitClient(url, headers);
            }
        return INSTANCE;
    }

    private RetrofitClient(String url, Map<String, String> headers) {

        if (TextUtils.isEmpty(url)) {
            throw new RuntimeException("Domain is null!");
        }

        if (httpCacheDirectory == null) {
            httpCacheDirectory = new File(mContext.getCacheDir(), "accompany_cache");
        }

        try {
            if (cache == null) {
                cache = new Cache(httpCacheDirectory, MAX_CACHE_SIZE);
            }
        } catch (Exception e) {
            KLog.e("Could not create http cache", e);
        }

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJarImpl(new PersistentCookieStore(mContext)))
                .cache(cache)
                .addInterceptor(new BaseInterceptor(headers))
                .addInterceptor(new CacheInterceptor(mContext))
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .addInterceptor(new LoggingInterceptor
                        .Builder()//构建者模式
                        .loggable(BuildConfig.DEBUG) //是否开启日志打印
                        .setLevel(Level.BASIC) //打印的等级
                        .log(Platform.INFO) // 打印类型
                        .request("Request") // request的Tag
                        .response("Response")// Response的Tag
                        .addHeader("log-header", "I am the log request header.") // 添加打印头, 注意 key 和 value 都不能是中文
                        .build()
                )
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为10s
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .build();

    }

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }

    /**
     * /**
     * execute your customer API
     * For example:
     * MyApiService service =
     * RetrofitClient.getInstance(MainActivity.this).create(MyApiService.class);
     * <p>
     * RetrofitClient.getInstance(MainActivity.this)
     * .execute(service.lgon("name", "password"), subscriber)
     * * @param subscriber
     */

    public static <T> T execute(Observable<T> observable, Observer<T> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        return null;
    }
}
